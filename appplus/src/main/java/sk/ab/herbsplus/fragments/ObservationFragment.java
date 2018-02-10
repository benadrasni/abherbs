package sk.ab.herbsplus.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.DisplayPlantPlusActivity;
import sk.ab.herbsplus.activities.ListObservationsActivity;
import sk.ab.herbsplus.commons.ObservationAdapter;
import sk.ab.herbsplus.commons.ObservationHolder;

public class ObservationFragment extends Fragment {

    private TextView noObservations;
    private ObservationAdapter adapterPrivate;
    private ObservationAdapter adapterPublic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plant_card_observations, null);
        TextView noObservations = view.findViewById(R.id.no_observations);

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (getActivity() instanceof DisplayPlantPlusActivity) {
                final DisplayPlantPlusActivity activity = (DisplayPlantPlusActivity) getActivity();
                final RecyclerView recyclerView = view.findViewById(R.id.observations);
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

                adapterPrivate = new ObservationAdapter(activity, noObservations, Observation.class,
                        R.layout.observation_row, ObservationHolder.class, privateObservationsRef, false, true);
                adapterPublic = new ObservationAdapter(activity, noObservations, Observation.class,
                        R.layout.observation_row, ObservationHolder.class, publicObservationsRef, false, false);
                recyclerView.setAdapter(adapterPrivate);

                Switch privatePublicSwitch = view.findViewById(R.id.private_public_switch);
                privatePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (sk.ab.herbsplus.util.Utils.isSubscription(currentUser)) {
                                recyclerView.swapAdapter(adapterPublic, true);
                                adapterPublic.onDataChanged();
                            } else {
                                AlertDialog dialogBox = SubscriptionDialog();
                                dialogBox.show();
                                buttonView.setChecked(false);
                            }
                        } else {
                            recyclerView.swapAdapter(adapterPrivate, true);
                            adapterPrivate.onDataChanged();
                        }
                    }
                });
            } else if (getActivity() instanceof ListObservationsActivity) {
                final ListObservationsActivity activity = (ListObservationsActivity) getActivity();
                final RecyclerView recyclerView = view.findViewById(R.id.observations);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference privateObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS + AndroidConstants.SEPARATOR
                        + currentUser.getUid() + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_DATA_LIST);
                final DatabaseReference publicObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_OBSERVATIONS_PUBLIC + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE + AndroidConstants.SEPARATOR
                        + AndroidConstants.FIREBASE_DATA_LIST);

                adapterPrivate = new ObservationAdapter(activity, noObservations, Observation.class,
                        R.layout.observation_row, ObservationHolder.class, privateObservationsRef, true, true);
                adapterPublic = new ObservationAdapter(activity, noObservations, Observation.class,
                        R.layout.observation_row, ObservationHolder.class, publicObservationsRef, true, false);
                recyclerView.setAdapter(adapterPrivate);

                Switch privatePublicSwitch = view.findViewById(R.id.private_public_switch);
                privatePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (sk.ab.herbsplus.util.Utils.isSubscription(currentUser)) {
                                recyclerView.swapAdapter(adapterPublic, true);
                                adapterPublic.onDataChanged();
                            } else {
                                AlertDialog dialogBox = SubscriptionDialog();
                                dialogBox.show();
                                buttonView.setChecked(false);
                            }
                        } else {
                            recyclerView.swapAdapter(adapterPrivate, true);
                            adapterPrivate.onDataChanged();
                        }
                    }
                });
            }
        } else {
            noObservations.setText(R.string.no_observations_login);
            noObservations.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            RecyclerView recyclerView = getView().findViewById(R.id.observations);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            if (getActivity() instanceof DisplayPlantPlusActivity) {
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            } else {
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            }
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
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

    private AlertDialog SubscriptionDialog() {
        return new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle(R.string.subscription)
                .setMessage(R.string.subscription_info)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })

                .create();
    }
}
