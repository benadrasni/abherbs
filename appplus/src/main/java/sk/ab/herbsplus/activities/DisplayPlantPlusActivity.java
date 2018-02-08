package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import sk.ab.common.entity.Observation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.fragments.GalleryFragment;
import sk.ab.herbsbase.fragments.InfoFragment;
import sk.ab.herbsbase.fragments.SourcesFragment;
import sk.ab.herbsbase.fragments.TaxonomyFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.entity.ObservationParcel;
import sk.ab.herbsplus.fragments.ObservationFragment;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantPlusActivity extends DisplayPlantBaseActivity {

    private FirebaseUser currentUser;
    private long mLastClickTime;
    private boolean shouldOpenObservation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        shouldOpenObservation = false;

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.VISIBLE);
            countButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClickOnObservation();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    getMenuFragment().manageUserSettings();
                    currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    refreshObservations();
                    saveToken();
                    if (shouldOpenObservation) {
                        handleClickOnObservation();
                        shouldOpenObservation = false;
                    }
                } else {
                    // Sign in failed, check response for error code
                    Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected PropertyListBaseFragment getNewMenuFragment() {
        return new PropertyListPlusFragment();
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected void addFragments(FragmentTransaction ft) {
        ft.replace(sk.ab.herbsbase.R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
        ft.replace(sk.ab.herbsbase.R.id.info_fragment, new InfoFragment(), "Info");
        ft.replace(sk.ab.herbsbase.R.id.gallery_fragment, new GalleryFragment(), "Gallery");
        ft.replace(sk.ab.herbsbase.R.id.observation_fragment, new ObservationFragment(), "Observation");
        ft.replace(sk.ab.herbsbase.R.id.sources_fragment, new SourcesFragment(), "Sources");
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    public void handleLogout() {
        currentUser = null;
        refreshObservations();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    private void handleClickOnObservation() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
            if (currentUser != null) {
                Date date =  new Date();
                Observation observation = new Observation();
                observation.setPublic(false);
                observation.setId(currentUser.getUid() + "_" + date.getTime());
                observation.setDate(date);
                observation.setPlant(getPlant().getName());
                observation.setPhotoPaths(new ArrayList<String>());

                Intent intent = new Intent(this, ObservationActivity.class);
                intent.putExtra(AndroidConstants.STATE_OBSERVATION, new ObservationParcel(observation));
                startActivity(intent);
            } else {
                shouldOpenObservation = true;
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        AndroidConstants.REQUEST_SIGN_IN);
            }
        }
    }

    private void saveToken() {
        String token = getSharedPreferences().getString(AndroidConstants.TOKEN_KEY, null);
        if (token != null) {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_USERS_TOKEN)
                    .setValue(token);
        }
    }

    private void refreshObservations() {
        Fragment observationFragment = getSupportFragmentManager().findFragmentByTag("Observation");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(observationFragment);
        ft.attach(observationFragment);
        ft.commit();
    }
}
