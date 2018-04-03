package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

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
import sk.ab.herbsplus.util.UtilsPlus;

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

        countButton = findViewById(R.id.countButton);
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
                    UtilsPlus.saveToken(this);
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

    @Override
    protected void showWizard() {
        final SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard1 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, false);
        Boolean showWizard2 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_3_1, false);
        Boolean showWizard3 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0_a, false);
        Boolean showWizard4 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0_b, false);

        if (showWizard1) {
            showWizardIllustration(editor);
        } else if (showWizard2) {
            showWizardTaxonomy(editor);
        } else if (showWizard3) {
            showWizardObservation(editor);
        } else if (showWizard4) {
            showWizardSwitch(editor);
        }
    }

    @Override
    protected void showSpecificWizard(SharedPreferences.Editor editor) {
        showWizardObservation(editor);
    }

    protected void showWizardObservation(final SharedPreferences.Editor editor) {
        ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(countButton))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_observation_title)
                .setContentText(R.string.showcase_observation_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showWizardSwitch(editor);
                    }
                })
                .build();

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        showcaseView.setButtonPosition(lps);
        editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0_a, true);
        editor.apply();
    }

    protected void showWizardSwitch(final SharedPreferences.Editor editor) {
        if (currentUser != null) {
            final ScrollView scrollView = findViewById(R.id.scrollview);
            final FrameLayout observationLayout = findViewById(R.id.observation_fragment);
            final SwitchCompat switchView = findViewById(R.id.private_public_switch_button);
            scrollView.post(new Runnable() {
                  @Override
                  public void run() {
                      scrollView.scrollTo(0, observationLayout.getTop());
                      ShowcaseView showcaseView = new ShowcaseView.Builder(DisplayPlantPlusActivity.this)
                              .withMaterialShowcase()
                              .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                              .setTarget(new ViewTarget(switchView))
                              .hideOnTouchOutside()
                              .setContentTitle(R.string.showcase_switch_title)
                              .setContentText(R.string.showcase_switch_message)
                              .build();

                      RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                              ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                      lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                      lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                      int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
                      lps.setMargins(margin, margin, margin, margin);
                      showcaseView.setButtonPosition(lps);
                  }
              });

            editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + SpecificConstants.VERSION_2_0_0_b, true);
            editor.apply();
        }
    }

    private void handleClickOnObservation() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
            if (currentUser != null) {
                Date date =  new Date();
                Observation observation = new Observation();
                observation.setStatus(SpecificConstants.FIREBASE_STATUS_PRIVATE);
                observation.setDate(date);
                observation.setPlant(getPlant().getName());
                observation.setPhotoPaths(new ArrayList<String>());

                Intent intent = new Intent(this, ObservationActivity.class);
                intent.putExtra(AndroidConstants.STATE_OBSERVATION, new ObservationParcel(observation));
                startActivity(intent);
            } else {
                AlertDialog dialogBox = UtilsPlus.LoginDialog(this);
                dialogBox.show();
                shouldOpenObservation = true;
            }
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
