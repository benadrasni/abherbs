package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.SearchBaseActivity;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.commons.ObservationAdapter;
import sk.ab.herbsplus.commons.ObservationHolder;
import sk.ab.herbsplus.util.UtilsPlus;

/**
 * Show private/public observations
 *
 * Created by adrian on 2/10/2018.
 */

public class ListObservationsActivity extends SearchBaseActivity {

    private RecyclerView recyclerView;
    private ObservationAdapter adapterPrivate;
    private ObservationAdapter adapterPublic;
    private TextView noObservations;

    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(R.string.plant_observations);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.observations);
        noObservations = findViewById(R.id.no_observations);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference privateObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                + AndroidConstants.FIREBASE_OBSERVATIONS_BY_USERS + AndroidConstants.SEPARATOR
                + currentUser.getUid() + AndroidConstants.SEPARATOR
                + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE + AndroidConstants.SEPARATOR
                + AndroidConstants.FIREBASE_DATA_LIST);

        FirebaseRecyclerOptions<Observation> options = new FirebaseRecyclerOptions.Builder<Observation>()
                .setQuery(privateObservationsRef, Observation.class)
                .build();
        adapterPrivate = new ObservationAdapter(this, noObservations, options, true, true);
        recyclerView.setAdapter(adapterPrivate);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterPrivate.startListening();
        if (adapterPublic != null) {
            adapterPublic.startListening();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_observations, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_switch);
        menuItem.setActionView(R.layout.switch_layout);
        SwitchCompat privatePublicSwitch = menuItem.getActionView().findViewById(R.id.private_public_switch_button);
        privatePublicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (isMonthlySubscribed() || isYearlySubscribed()) {
                        if (adapterPublic == null) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference publicObservationsRef = database.getReference(AndroidConstants.FIREBASE_OBSERVATIONS + AndroidConstants.SEPARATOR
                                    + AndroidConstants.FIREBASE_OBSERVATIONS_PUBLIC + AndroidConstants.SEPARATOR
                                    + AndroidConstants.FIREBASE_OBSERVATIONS_BY_DATE + AndroidConstants.SEPARATOR
                                    + AndroidConstants.FIREBASE_DATA_LIST);
                            FirebaseRecyclerOptions<Observation> options = new FirebaseRecyclerOptions.Builder<Observation>()
                                    .setQuery(publicObservationsRef, Observation.class)
                                    .build();
                            adapterPublic = new ObservationAdapter(ListObservationsActivity.this, noObservations, options, true, false);
                            adapterPublic.startListening();
                        }
                        recyclerView.swapAdapter(adapterPublic, true);
                        adapterPublic.onDataChanged();
                    } else {
                        AlertDialog dialogBox = UtilsPlus.SubscriptionDialog(ListObservationsActivity.this);
                        dialogBox.show();
                        buttonView.setChecked(false);
                    }
                } else {
                    recyclerView.swapAdapter(adapterPrivate, true);
                    adapterPrivate.onDataChanged();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapterPrivate != null) {
            adapterPrivate.stopListening();
        }
        if (adapterPublic != null) {
            adapterPublic.stopListening();
        }
    }

    @Override
    protected Class getFilterPlantsActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected Class getListPlantsActivityClass() {
        return ListPlantsPlusActivity.class;
    }

    @Override
    protected Class getDisplayPlantActivityClass() {
        return DisplayPlantPlusActivity.class;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.list_observations_activity;
    };
}
