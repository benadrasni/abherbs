package sk.ab.herbsplus.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
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
import sk.ab.herbsplus.HerbsApp;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.DisplayPlantPlusActivity;
import sk.ab.herbsplus.commons.ObservationHolder;

public class ObservationFragment extends Fragment {
    private long mLastClickTime;

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

            if (observation.getPhotoPaths() != null && observation.getPhotoPaths().size() > 0) {
                Utils.displayImage(activity.getApplicationContext().getFilesDir(), observation.getPhotoPaths().get(0),
                        holder.getPhoto(), ((BaseApp) activity.getApplication()).getOptions());
            } else {
                Crashlytics.log("Empty photoPaths: " + activity.getPlant().getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plant_card_observations, null);
        DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();

        RecyclerView recyclerView = view.findViewById(R.id.observations);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference observationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.FIREBASE_SEPARATOR
                + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS + AndroidConstants.FIREBASE_SEPARATOR + activity.getCurrentUser().getUid()
                + AndroidConstants.FIREBASE_SEPARATOR + AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT + AndroidConstants.FIREBASE_SEPARATOR
                + activity.getPlant().getName());

        adapter = new PropertyAdapter(Observation.class, R.layout.observation, ObservationHolder.class, observationsRef);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.observations);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }
}
