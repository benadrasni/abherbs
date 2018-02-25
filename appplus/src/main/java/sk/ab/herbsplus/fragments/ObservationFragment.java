package sk.ab.herbsplus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.DisplayPlantPlusActivity;
import sk.ab.herbsplus.commons.ObservationAdapter;
import sk.ab.herbsplus.commons.ObservationHolder;
import sk.ab.herbsplus.util.UtilsPlus;

public class ObservationFragment extends Fragment {

    RecyclerView recyclerView;
    private ObservationAdapter adapterPrivate;
    private ObservationAdapter adapterPublic;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plant_card_observations, null);
        final TextView noObservations = view.findViewById(R.id.no_observations);
        recyclerView = view.findViewById(R.id.observations);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            final DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();
            assert activity != null;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference privateObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS + AndroidConstants.SEPARATOR
                    + currentUser.getUid() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT + AndroidConstants.SEPARATOR
                    + activity.getPlant().getName() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_DATA_LIST);
            final DatabaseReference publicObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_PUBLIC + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_PLANT + AndroidConstants.SEPARATOR
                    + activity.getPlant().getName() + AndroidConstants.SEPARATOR
                    + AndroidConstants.FIREBASE_DATA_LIST);

            adapterPublic = new ObservationAdapter(activity, noObservations, Observation.class,
                    R.layout.observation_row, ObservationHolder.class, publicObservationsRef, false, false);
            adapterPrivate = new ObservationAdapter(activity, noObservations, Observation.class,
                    R.layout.observation_row, ObservationHolder.class, privateObservationsRef, false, true);
            recyclerView.setAdapter(adapterPrivate);

            SwitchCompat privatePublicSwitch = view.findViewById(R.id.private_public_switch_button);
            privatePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (activity.isMonthlySubscribed() || activity.isYearlySubscribed()) {
                            recyclerView.swapAdapter(adapterPublic, true);
                            adapterPublic.onDataChanged();
                        } else {
                            AlertDialog dialogBox = UtilsPlus.SubscriptionDialog(getActivity());
                            dialogBox.show();
                            buttonView.setChecked(false);
                        }
                    } else {
                        recyclerView.swapAdapter(adapterPrivate, true);
                        adapterPrivate.onDataChanged();
                    }
                }
            });
        } else {
            LinearLayout linearLayout = view.findViewById(R.id.private_public_switch);
            linearLayout.setVisibility(View.GONE);
            noObservations.setText(R.string.no_observations_login);
            noObservations.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapterPrivate != null) {
            adapterPrivate.cleanup();
        }
        if (adapterPublic != null) {
            adapterPublic.cleanup();
        }
    }
}
