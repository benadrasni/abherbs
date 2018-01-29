package sk.ab.herbsplus.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import sk.ab.herbsplus.activities.DisplayPlantPlusActivity;
import sk.ab.herbsplus.activities.ObservationActivity;
import sk.ab.herbsplus.commons.ObservationHolder;
import sk.ab.herbsplus.entity.ObservationParcel;

public class ObservationFragment extends Fragment {
    private long mLastClickTime;

    private TextView noObservations;
    private PropertyAdapter adapter;

    private class PropertyAdapter extends FirebaseRecyclerAdapter<Observation, ObservationHolder> {

        PropertyAdapter(Class<Observation> modelClass, @LayoutRes int modelLayout, Class<ObservationHolder> viewHolderClass, Query dataRef) {
            super(modelClass, modelLayout, viewHolderClass, dataRef);
        }

        @Override
        protected void populateViewHolder(final ObservationHolder holder, final Observation observation, int position) {
            final DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();
            holder.getObservationDate().setText(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(),
                    AndroidConstants.DATE_SKELETON), observation.getDate()));

            holder.getEdit().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ObservationActivity.class);
                    intent.putExtra(AndroidConstants.STATE_OBSERVATION, new ObservationParcel(observation));
                    startActivity(intent);
                }
            });

            holder.getDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog dialogBox = DeleteConfirmationDialog(observation);
                    dialogBox.show();
                }
            });

            holder.initializeMapView(activity, observation.getLatitude(), observation.getLongitude());

            holder.getPhoto().setImageResource(android.R.color.transparent);
            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            int size = dm.widthPixels - Utils.convertDpToPx(40, dm);
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
                        displayPhoto(holder, observation);
                    }
                }
            });

            displayPhoto(holder, observation);

            if (observation.getNote() != null) {
                holder.getObservationNote().setText(observation.getNote());
            }
        }

        @Override
        protected void onDataChanged() {
            noObservationsMessage(getItemCount() == 0, R.string.no_observations);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plant_card_observations, null);
        noObservations = view.findViewById(R.id.no_observations);
        DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();

        if (activity.getCurrentUser() != null) {
            RecyclerView recyclerView = view.findViewById(R.id.observations);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference observationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS + AndroidConstants.SEPARATOR + activity.getCurrentUser().getUid()
                    + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT + AndroidConstants.SEPARATOR
                    + activity.getPlant().getName());

            adapter = new PropertyAdapter(Observation.class, R.layout.observation_row, ObservationHolder.class, observationsRef);
            recyclerView.setAdapter(adapter);
        } else {
            noObservationsMessage(true, R.string.no_observations_login);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.observations);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cleanup();
        }
    }

    private void deleteObservation(Observation observation) {
        DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();

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
                .child(activity.getCurrentUser().getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE)
                .child(observation.getId())
                .setValue(null);
        // by user, by plant, by date
        mFirebaseRef.child(AndroidConstants.FIREBASE_OBSERVATIONS)
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS)
                .child(activity.getCurrentUser().getUid())
                .child(AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT)
                .child(observation.getPlant())
                .child(observation.getId())
                .setValue(null);
    }

    private AlertDialog DeleteConfirmationDialog(final Observation observation) {
        return new AlertDialog.Builder(getActivity())
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

    private void noObservationsMessage(boolean show, int message) {
        if (show) {
            noObservations.setText(message);
            noObservations.setVisibility(View.VISIBLE);
        } else {
            noObservations.setVisibility(View.GONE);
        }
    }

    private void displayPhoto(ObservationHolder holder, Observation observation) {
        DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();

        holder.getPrevPhoto().setClickable(holder.getPhotoPosition() > 0);
        holder.getNextPhoto().setClickable(holder.getPhotoPosition() < observation.getPhotoPaths().size() -1);

        String counterText = (observation.getPhotoPaths().size() == 0 ? holder.getPhotoPosition() : holder.getPhotoPosition() + 1) + " / " + observation.getPhotoPaths().size();
        holder.getCounter().setText(counterText);

        if (holder.getPhotoPosition() >= 0 && holder.getPhotoPosition() < observation.getPhotoPaths().size()) {
            Utils.displayImage(activity.getApplicationContext().getFilesDir(), observation.getPhotoPaths().get(holder.getPhotoPosition()),
                    holder.getPhoto(), ((BaseApp) activity.getApplication()).getOptions());
        }
    }
}
