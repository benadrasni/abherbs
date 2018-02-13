package sk.ab.herbsplus.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsbase.tools.Utils;
import sk.ab.herbsplus.SpecificConstants;

/**
 * Service to synchronize offline photos (download) and observations (upload)
 *
 * Created by adrian on 2/4/2018.
 */

public class SynchronizationService extends IntentService {

    private static final String TAG = "SynchronizationService";
    private static final String FIRST_FLOWER = "Acer campestre";
    private static final String FIRST_FAMILY = "Acanthaceae";

    private FirebaseDatabase database;

    public SynchronizationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!BaseApp.isConnectedToWifi(getApplicationContext())) {
            return;
        }

        database = FirebaseDatabase.getInstance();

        boolean offlineMode = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        if (offlineMode) {
            // download photos and family icons
            DatabaseReference mFirebaseRefCount = database.getReference(AndroidConstants.FIREBASE_PLANTS_TO_UPDATE);
            mFirebaseRefCount.keepSynced(true);
            final Query queryCount = mFirebaseRefCount.child(AndroidConstants.FIREBASE_DATA_COUNT);
            mFirebaseRefCount.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    queryCount.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            assert dataSnapshot.getValue() != null;
                            final Integer countAll = ((Long) dataSnapshot.getValue()).intValue();

                            DatabaseReference mFirebaseRefPlant = database.getReference(AndroidConstants.FIREBASE_PLANTS + AndroidConstants.SEPARATOR + FIRST_FLOWER);
                            mFirebaseRefPlant.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    FirebasePlant plant = dataSnapshot.getValue(FirebasePlant.class);

                                    assert plant != null;
                                    File photoIllustration = new File(SynchronizationService.this.getApplicationContext().getFilesDir()
                                            + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_PHOTOS + plant.getIllustrationUrl());
                                    if (photoIllustration.exists()) {
                                        int from = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getInt(SpecificConstants.OFFLINE_PLANT_KEY, -1);
                                        synchronizePlant(from + 1, countAll);
                                    } else {
                                        synchronizePlant(0, countAll);
                                    }

                                    File familyIcon = new File(SynchronizationService.this.getApplicationContext().getFilesDir()
                                            + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_FAMILIES + FIRST_FAMILY + AndroidConstants.DEFAULT_EXTENSION);
                                    if (familyIcon.exists()) {
                                        int from = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getInt(SpecificConstants.OFFLINE_FAMILY_KEY, -1);
                                        synchronizeFamily(from + 1);
                                    } else {
                                        synchronizeFamily(0);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, databaseError.getMessage());
                                    broadcastDownload(-1, -1);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                            broadcastDownload(-1, -1);
                        }
                    });
                }
            });
        }

            // upload observations
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && sk.ab.herbsplus.util.Utils.isSubscription(currentUser)) {
            DatabaseReference mFirebaseRefObservations = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS
                    + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS
                    + AndroidConstants.SEPARATOR + currentUser.getUid() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE);
            mFirebaseRefObservations.keepSynced(true);
            final Query queryPrivate = mFirebaseRefObservations.child(AndroidConstants.FIREBASE_DATA_LIST)
                    .orderByChild(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .equalTo(SpecificConstants.FIREBASE_STATUS_PRIVATE);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    queryPrivate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                uploadOneObservation(1, (int) dataSnapshot.getChildrenCount());
                            } else {
                                revertStatus();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            revertStatus();
                        }
                    });
                }
            });
        }
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
                                broadcastDownload(-1, -1);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                            broadcastDownload(-1, -1);
                        }
                    });
                } else {
                    broadcastDownload(-1, -1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                broadcastDownload(-1, -1);
            }
        });
    }

    private void downloadPlant(final int number, FirebasePlant firebasePlant, final Integer countAll) {
        FirebaseStorage storage = FirebaseStorage.getInstance(SpecificConstants.STORAGE);

        String localPath = getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_PHOTOS;
        final int numberOfFiles = 1 + firebasePlant.getPhotoUrls().size() * 2;

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
                    savePlantOffline(number, countAll);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                broadcastDownload(-1, -1);
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
                        savePlantOffline(number, countAll);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    broadcastDownload(-1, -1);
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
                        savePlantOffline(number, countAll);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    broadcastDownload(-1, -1);
                }
            });
        }
    }

    private void savePlantOffline(Integer number, Integer countAll) {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        int from = preferences.getInt(SpecificConstants.OFFLINE_PLANT_KEY, -1);
        if (from < number) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SpecificConstants.OFFLINE_PLANT_KEY, number);
            editor.apply();

            broadcastDownload(number, countAll);

            boolean offlineMode = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
            if (offlineMode && BaseApp.isConnectedToWifi(getApplicationContext())) {
                synchronizePlant(number + 1, countAll);
            } else {
                broadcastDownload(-1, -1);
            }
        }
    }

    private void broadcastDownload(Integer number, Integer countAll) {
        Intent localIntent = new Intent(AndroidConstants.BROADCAST_DOWNLOAD)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_ALL, countAll)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_SYNCHONIZED, number);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void synchronizeFamily(final Integer from) {
        DatabaseReference mFirebaseRefPlant = database.getReference(AndroidConstants.FIREBASE_FAMILIES_TO_UPDATE
                + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_DATA_LIST + AndroidConstants.SEPARATOR + from);
        mFirebaseRefPlant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String familyName = dataSnapshot.getValue(String.class);
                    downloadFamily(from, familyName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void downloadFamily(final Integer number, String familyName) {
        FirebaseStorage storage = FirebaseStorage.getInstance(SpecificConstants.STORAGE);

        String localPath = getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_FAMILIES;

        File plantDir = new File(localPath);
        plantDir.mkdirs();

        String fileName = familyName + AndroidConstants.DEFAULT_EXTENSION;
        File illustrationFile = new File(localPath + fileName);
        StorageReference storageRefIcon = storage.getReference(SpecificConstants.FAMILIES + AndroidConstants.SEPARATOR + fileName);
        storageRefIcon.getFile(illustrationFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                saveFamilyOffline(number);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                broadcastDownload(-1, -1);
            }
        });
    }

    private void saveFamilyOffline(Integer number) {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        int from = preferences.getInt(SpecificConstants.OFFLINE_FAMILY_KEY, -1);
        if (from < number) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(SpecificConstants.OFFLINE_FAMILY_KEY, number);
            editor.apply();

            boolean offlineMode = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE).getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
            if (offlineMode && BaseApp.isConnectedToWifi(getApplicationContext())) {
                synchronizeFamily(number + 1);
            }
        }
    }

    private void uploadOneObservation(final Integer number, final Integer countAll) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference mFirebaseRefObservations = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS
                    + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS
                    + AndroidConstants.SEPARATOR + currentUser.getUid() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE);
            mFirebaseRefObservations.keepSynced(true);
            final Query query = mFirebaseRefObservations.child(AndroidConstants.FIREBASE_DATA_LIST)
                    .orderByChild(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .equalTo(SpecificConstants.FIREBASE_STATUS_PRIVATE)
                    .limitToFirst(1);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                final Observation observation = dataSnapshot.getChildren().iterator().next().getValue(Observation.class);
                                assert observation != null;
                                if (observation.getPhotoPaths().size() > 0) {
                                    final SynchronizedCounter counter = new SynchronizedCounter();

                                    for (String photoPath : observation.getPhotoPaths()) {
                                        File photoFile = new File(getApplicationContext().getFilesDir()
                                                + AndroidConstants.SEPARATOR + photoPath);
                                        if (photoFile.exists()) {
                                            FirebaseStorage storage = FirebaseStorage.getInstance(SpecificConstants.STORAGE);
                                            StorageReference imagesRef = storage.getReference().child(photoPath);
                                            UploadTask uploadTask = imagesRef.putFile(Uri.fromFile(photoFile));
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    counter.increment();
                                                    if (counter.value() == observation.getPhotoPaths().size()) {
                                                        savePublicObservation(observation, number, countAll);
                                                    }
                                                }
                                            });
                                        } else {
                                            markObservation(observation, SpecificConstants.FIREBASE_STATUS_INCOMPLETE);
                                            continueUpload(number, countAll);
                                            break;
                                        }
                                    }
                                } else {
                                    markObservation(observation, SpecificConstants.FIREBASE_STATUS_INCOMPLETE);
                                    continueUpload(number, countAll);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    private void savePublicObservation(Observation observation, Integer number, Integer countAll) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            observation.setStatus(SpecificConstants.FIREBASE_STATUS_REVIEW);

            // by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_PUBLIC)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child("" + observation.getDate().getTime())
                    .setValue(observation);

            // by plant
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_PUBLIC)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child("" + observation.getDate().getTime())
                    .setValue(observation);

            // update "published" to true
            // by user, by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .setValue(SpecificConstants.FIREBASE_STATUS_PUBLIC);
            // by user, by plant, by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .setValue(SpecificConstants.FIREBASE_STATUS_PUBLIC);

            continueUpload(number, countAll);
        }
    }

    private void continueUpload(Integer number, Integer countAll) {
        broadcastUpload(number, countAll);
        if (number + 1 <= countAll) {
            if (BaseApp.isConnectedToWifi(getApplicationContext())) {
                uploadOneObservation(number + 1, countAll);
            } else {
                revertStatus();
            }
        } else {
            revertStatus();
        }
    }

    private void markObservation(Observation observation, String status) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            // update "status" to "missing_data"
            // by user, by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .setValue(status);

            // by user, by plant, by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getPlant())
                    .child(observation.getId())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .setValue(status);
        }
    }

    private void broadcastUpload(Integer number, Integer countAll) {
        Intent localIntent = new Intent(AndroidConstants.BROADCAST_UPLOAD)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_ALL, countAll)
                .putExtra(AndroidConstants.EXTENDED_DATA_COUNT_SYNCHONIZED, number);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private void revertStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference mFirebaseRefObservations = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS
                    + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS
                    + AndroidConstants.SEPARATOR + currentUser.getUid() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE);
            mFirebaseRefObservations.keepSynced(true);
            final Query query = mFirebaseRefObservations.child(AndroidConstants.FIREBASE_DATA_LIST)
                    .orderByChild(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .equalTo(SpecificConstants.FIREBASE_STATUS_INCOMPLETE);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Observation observation = data.getValue(Observation.class);
                                    markObservation(observation, SpecificConstants.FIREBASE_STATUS_PRIVATE);
                                }
                            }
                            broadcastUpload(-1, -1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            broadcastUpload(-1, -1);
                        }
                    });
                }
            });
        }
    }
}
