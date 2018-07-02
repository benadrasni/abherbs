package sk.ab.herbsplus.services;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsplus.SpecificConstants;

/**
 * Service to synchronize observations
 *
 * Created by adrian on 5/30/2018.
 */

public class ObservationService extends JobIntentService {

    public static final int JOB_ID = 2;

    private FirebaseDatabase database;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ObservationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@Nullable Intent intent) {
        if (!BaseApp.isConnectedToWifi(getApplicationContext()) || intent == null) {
            return;
        }

        boolean isSubscribed = intent.getBooleanExtra(AndroidConstants.STATE_IS_SUBSCRIBED, false);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && isSubscribed) {
            database = FirebaseDatabase.getInstance();
            DatabaseReference mFirebaseRefObservations = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS
                    + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS
                    + AndroidConstants.SEPARATOR + currentUser.getUid() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE);
            mFirebaseRefObservations.keepSynced(true);
            final Query queryPrivate = mFirebaseRefObservations.child(AndroidConstants.FIREBASE_DATA_LIST)
                    .orderByChild(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .equalTo(AndroidConstants.FIREBASE_STATUS_PRIVATE);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    queryPrivate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                uploadOneObservation(1, (int) dataSnapshot.getChildrenCount());
                            } else {
                                revertStatus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            revertStatus();
                        }
                    });
                }
            });
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
                    .equalTo(AndroidConstants.FIREBASE_STATUS_PRIVATE)
                    .limitToFirst(1);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                                            markObservation(observation, AndroidConstants.FIREBASE_STATUS_INCOMPLETE);
                                            continueUpload(number, countAll);
                                            break;
                                        }
                                    }
                                } else {
                                    markObservation(observation, AndroidConstants.FIREBASE_STATUS_INCOMPLETE);
                                    continueUpload(number, countAll);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    private void savePublicObservation(Observation observation, Integer number, Integer countAll) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {

            observation.setStatus(AndroidConstants.FIREBASE_STATUS_REVIEW);

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
                    .setValue(AndroidConstants.FIREBASE_STATUS_PUBLIC);
            // by user, by plant, by date
            database.getReference().child(AndroidConstants.FIREBASE_OBSERVATIONS)
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                    .child(observation.getPlant())
                    .child(AndroidConstants.FIREBASE_DATA_LIST)
                    .child(observation.getId())
                    .child(AndroidConstants.FIREBASE_OBSERVATIONS_STATUS)
                    .setValue(AndroidConstants.FIREBASE_STATUS_PUBLIC);

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
                    .equalTo(AndroidConstants.FIREBASE_STATUS_INCOMPLETE);
            mFirebaseRefObservations.child("refreshMock").setValue("mock", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Observation observation = data.getValue(Observation.class);
                                    markObservation(observation, AndroidConstants.FIREBASE_STATUS_PRIVATE);
                                }
                            }
                            broadcastUpload(-1, -1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            broadcastUpload(-1, -1);
                        }
                    });
                }
            });
        }
    }
}
