package sk.ab.herbsplus.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsbase.tools.Utils;
import sk.ab.herbsplus.SpecificConstants;

/**
 *
 * Created by adrian on 2/4/2018.
 */

public class SynchronizationService extends IntentService {

    private static final String TAG = "SynchronizationService";
    private static final String FIRST_FLOWER = "Acer campestre";

    private FirebaseDatabase database;
    private FirebaseUser currentUser;

    public SynchronizationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean offlineMode = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        if (!offlineMode || !BaseApp.isConnectedToWifi(getApplicationContext())) {
            return;
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        database = FirebaseDatabase.getInstance();

        DatabaseReference mFirebaseRefCount = database.getReference(AndroidConstants.FIREBASE_PLANTS_TO_UPDATE
                + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_DATA_COUNT);
        mFirebaseRefCount.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Integer countAll = ((Long)dataSnapshot.getValue()).intValue();

                DatabaseReference mFirebaseRefPlant = database.getReference(AndroidConstants.FIREBASE_PLANTS + AndroidConstants.SEPARATOR + FIRST_FLOWER);
                mFirebaseRefPlant.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebasePlant plant = dataSnapshot.getValue(FirebasePlant.class);

                        File photoIllustration = new File(SynchronizationService.this.getApplicationContext().getFilesDir()
                                + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_PHOTOS + plant.getIllustrationUrl());
                        if (photoIllustration.exists()) {
                            int from = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getInt(SpecificConstants.OFFLINE_PLANT_KEY, -1);
                            synchronizePlant(from + 1, countAll);
                        } else {
                            synchronizePlant(0, countAll);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                        broadcast(-1, -1);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                broadcast(-1, -1);
            }
        });
    }

    private void synchronizePlant(final Integer from, final Integer countAll) {
        DatabaseReference mFirebaseRefPlant = database.getReference(AndroidConstants.FIREBASE_PLANTS_TO_UPDATE
                + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_DATA_LIST + AndroidConstants.SEPARATOR + from);
        mFirebaseRefPlant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String plantName = dataSnapshot.getValue(String.class);

                    DatabaseReference mFirebaseRefPlant = database.getReference(AndroidConstants.FIREBASE_PLANTS + AndroidConstants.SEPARATOR + plantName);
                    mFirebaseRefPlant.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                FirebasePlant plant = dataSnapshot.getValue(FirebasePlant.class);
                                downloadPlant(from, plant, countAll);
                            } else {
                                broadcast(-1, -1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                            broadcast(-1, -1);
                        }
                    });
                } else {
                    broadcast(-1, -1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                broadcast(-1, -1);
            }
        });
    }

    private void downloadPlant(final int number, FirebasePlant firebasePlant, final Integer countAll) {
        FirebaseStorage storage = FirebaseStorage.getInstance(SpecificConstants.STORAGE);

        String localPath = getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_PHOTOS;
        final int numberOfFiles = 1 + firebasePlant.getPhotoUrls().size()*2;

        File plantDir = new File(localPath + firebasePlant.getIllustrationUrl().substring(0, firebasePlant.getIllustrationUrl().lastIndexOf('/')));
        if (plantDir.exists()) {
            sk.ab.common.util.Utils.deleteRecursive(plantDir);
        }
        File thumbDir = new File(plantDir.getPath() + AndroidConstants.SEPARATOR + AndroidConstants.THUMBNAIL_DIR);
        thumbDir.mkdirs();

        final SynchronizedCounter counter = new SynchronizedCounter();

        File illustrationFile = new File(localPath + firebasePlant.getIllustrationUrl());
        StorageReference storageRefIllustration = storage.getReference(SpecificConstants.PHOTOS + AndroidConstants.SEPARATOR + firebasePlant.getIllustrationUrl());
        storageRefIllustration.getFile(illustrationFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                counter.increment();
                if (counter.value() == numberOfFiles) {
                    saveOffline(number, countAll);
                }
            }
        });

        for (String photoPath : firebasePlant.getPhotoUrls()) {
            File photoFile = new File(localPath + photoPath);
            StorageReference storageRefPhoto = storage.getReference(SpecificConstants.PHOTOS + AndroidConstants.SEPARATOR + photoPath);
            storageRefPhoto.getFile(photoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    counter.increment();
                    if (counter.value() == numberOfFiles) {
                        saveOffline(number, countAll);
                    }
                }
            });

            String thumbPath = Utils.getThumbnailUrl(photoPath);
            File thumbFile = new File(localPath + thumbPath);
            StorageReference storageRefThumb = storage.getReference(SpecificConstants.PHOTOS + AndroidConstants.SEPARATOR + thumbPath);
            storageRefThumb.getFile(thumbFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    counter.increment();
                    if (counter.value() == numberOfFiles) {
                        saveOffline(number, countAll);
                    }
                }
            });
        }
    }

    private void saveOffline(Integer number, Integer countAll) {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        int from = preferences.getInt(SpecificConstants.OFFLINE_PLANT_KEY, -1);
        if (from < number) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SpecificConstants.OFFLINE_PLANT_KEY, number);
            editor.apply();

            broadcast(number, countAll);

            if (BaseApp.isConnectedToWifi(getApplicationContext())) {
                synchronizePlant(number + 1, countAll);
            } else {
                broadcast(-1, -1);
            }
        }
    }

    private void broadcast(Integer number, Integer countAll) {
        Intent localIntent = new Intent(AndroidConstants.BROADCAST_ACTION)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_ALL, countAll)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_SYNCHONIZED, number);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
