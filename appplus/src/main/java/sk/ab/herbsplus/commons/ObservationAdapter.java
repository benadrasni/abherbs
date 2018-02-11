package sk.ab.herbsplus.commons;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.util.Locale;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.tools.Utils;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.ObservationActivity;
import sk.ab.herbsplus.entity.ObservationParcel;

/**
 *
 * Created by adrian on 2/10/2018.
 */

public class ObservationAdapter extends FirebaseRecyclerAdapter<Observation, ObservationHolder> {
    private long mLastClickTime;

    private Activity activity;
    private TextView noObservations;
    private FirebaseUser currentUser;
    private boolean isPlant;
    private boolean isPrivate;

    public ObservationAdapter(Activity activity, TextView noObservations, Class<Observation> modelClass,
                              @LayoutRes int modelLayout, Class<ObservationHolder> viewHolderClass, Query dataRef,
                              boolean isPlant, boolean isPrivate) {
        super(modelClass, modelLayout, viewHolderClass, dataRef);
        this.activity = activity;
        this.isPlant = isPlant;
        this.isPrivate = isPrivate;
        this.noObservations = noObservations;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void populateViewHolder(final ObservationHolder holder, final Observation observation, int position) {
        if (isPlant) {
            holder.getPlantName().setText(observation.getPlant());
            holder.getPlantName().setVisibility(View.VISIBLE);
        } else {
            holder.getPlantName().setVisibility(View.GONE);
        }

        holder.setPhotoPosition(0);
        holder.getObservationDate().setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(),
                AndroidConstants.DATE_SKELETON), observation.getDate()));

        if (isPrivate) {
            holder.getEdit().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ObservationActivity.class);
                    intent.putExtra(AndroidConstants.STATE_OBSERVATION, new ObservationParcel(observation));
                    activity.startActivity(intent);
                }
            });
            holder.getEdit().setVisibility(View.VISIBLE);

            holder.getDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialogBox = DeleteConfirmationDialog(observation);
                    dialogBox.show();
                }
            });
            holder.getDelete().setVisibility(View.VISIBLE);
        } else {
            holder.getEdit().setVisibility(View.GONE);
            holder.getDelete().setVisibility(View.GONE);
        }

        holder.initializeMapView(activity, observation.getLatitude(), observation.getLongitude());

        holder.getPhoto().setImageResource(android.R.color.transparent);
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int size = dm.widthPixels - Utils.convertDpToPx(20, dm);
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            size = size / 2;
        }
        holder.getPhoto().getLayoutParams().width = size;
        holder.getPhoto().getLayoutParams().height = size;

        holder.getPrevPhoto().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    holder.decPosition();
                    if (holder.getPhotoPosition() < 0) {
                        holder.setPhotoPosition(observation.getPhotoPaths().size()-1);
                    }
                    displayPhoto(holder, observation);
                }
            }
        });

        holder.getNextPhoto().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    holder.incPosition();
                    if (holder.getPhotoPosition() == observation.getPhotoPaths().size()) {
                        holder.setPhotoPosition(0);
                    }
                    displayPhoto(holder, observation);
                }
            }
        });

        displayPhoto(holder, observation);

        if (isPrivate) {
            if (observation.getNote() != null) {
                holder.getObservationNote().setText(observation.getNote());
            }
            holder.getObservationNote().setVisibility(View.VISIBLE);
        } else {
            holder.getObservationNote().setVisibility(View.GONE);
        }
    }

    @Override
    public void onDataChanged() {
        noObservationsMessage(getItemCount() == 0, R.string.no_observations);
    }

    private AlertDialog DeleteConfirmationDialog(final Observation observation) {
        return new AlertDialog.Builder(activity)
                //set message, title, and icon
                .setTitle(R.string.observation_delete)
                .setMessage(R.string.observation_delete_question)
                .setIcon(R.drawable.ic_delete_black_24dp)

                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteObservation(observation);
                        dialog.dismiss();
                    }

                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    private void deleteObservation(Observation observation) {

        for (String photoPath : observation.getPhotoPaths()) {
            File photoFile = new File( activity.getApplicationContext().getFilesDir() + AndroidConstants.SEPARATOR + photoPath);
            if (photoFile.exists()) {
                photoFile.delete();
            }
        }
        DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        // by user, by date
        mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                .child(currentUser.getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                .child(AndroidConstants.FIREBASE_DATA_LIST)
                .child(observation.getId())
                .setValue(null);
        // by user, by plant, by date
        mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                .child(currentUser.getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                .child(observation.getPlant())
                .child(AndroidConstants.FIREBASE_DATA_LIST)
                .child(observation.getId())
                .setValue(null);
    }

    private void displayPhoto(ObservationHolder holder, Observation observation) {
        String counterText = (observation.getPhotoPaths().size() == 0 ? holder.getPhotoPosition() : holder.getPhotoPosition() + 1) + " / " + observation.getPhotoPaths().size();
        holder.getCounter().setText(counterText);

        if (holder.getPhotoPosition() >= 0 && holder.getPhotoPosition() < observation.getPhotoPaths().size()) {
            Utils.displayImage(activity.getApplicationContext().getFilesDir(), observation.getPhotoPaths().get(holder.getPhotoPosition()),
                    holder.getPhoto(), ((BaseApp) activity.getApplication()).getOptions());
        }
    }

    private void noObservationsMessage(boolean show, int message) {
        if (show) {
            noObservations.setText(message);
            noObservations.setVisibility(View.VISIBLE);
        } else {
            noObservations.setVisibility(View.GONE);
        }
    }
}

