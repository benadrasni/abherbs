package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.entity.PlantTranslationParcel;

/**
 * Activity for displaying selected plant
 *
 */
public abstract class DisplayPlantBaseActivity extends BaseActivity {

    private boolean fromNotification;
    private PlantParcel plantParcel;
    private PlantTranslationParcel plantTranslationParcel;
    private PlantTranslationParcel plantTranslationEnParcel;
    private PlantTranslationParcel plantTranslationGTParcel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            fromNotification = savedInstanceState.getBoolean(AndroidConstants.STATE_FROM_NOTIFICATION);
            plantParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_PLANT);
            plantTranslationParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE);
            plantTranslationGTParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT);
            plantTranslationEnParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
        } else if (getIntent().getExtras() != null) {
            fromNotification = getIntent().getExtras().getBoolean(AndroidConstants.STATE_FROM_NOTIFICATION);
            plantParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_PLANT);
            plantTranslationParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE);
            plantTranslationGTParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT);
            plantTranslationEnParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH);
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
        }

        if (plantParcel != null) {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_LOCATION_ID, Locale.getDefault().getLanguage());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, plantParcel.getName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }

        overlay = findViewById(R.id.overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        countButton = findViewById(R.id.countButton);
        countText = findViewById(R.id.countText);

        mDrawerLayout = findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mPropertyMenu = getNewMenuFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fm.getFragments().size() == 0) {
            addFragments(ft);
        }
        ft.replace(R.id.menu_content, mPropertyMenu);
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.display_info);
        }

        ScrollView scrollview = findViewById(R.id.scrollview);
        if (scrollview != null) {
            scrollview.scrollTo(0, 0);
        }

        showWizard();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(AndroidConstants.STATE_FROM_NOTIFICATION, fromNotification);
        savedInstanceState.putParcelable(AndroidConstants.STATE_PLANT, plantParcel);
        savedInstanceState.putParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE, plantTranslationParcel);
        savedInstanceState.putParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT, plantTranslationGTParcel);
        savedInstanceState.putParcelable(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH, plantTranslationEnParcel);
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    @Override
    public void onBackPressed() {
        if (fromNotification) {
            Intent intent = new Intent(this, getFilterPlantActivityClass());
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.plant_activity;
    }

    public FirebasePlant getPlant() {
        return plantParcel;
    }

    public PlantTranslation getPlantTranslation() {
        return plantTranslationParcel;
    }

    public PlantTranslation getPlantTranslationGT() {
        return plantTranslationGTParcel;
    }

    public void setPlantTranslationGT(PlantTranslation plantTranslation) {
        plantTranslationGTParcel = new PlantTranslationParcel(plantTranslation);
    }

    public PlantTranslationParcel getPlantTranslationEn() {
        return plantTranslationEnParcel;
    }

    protected abstract Class getFilterPlantActivityClass();

    protected abstract void addFragments(FragmentTransaction ft);

    protected abstract void showWizard();

    protected void showSpecificWizard(SharedPreferences.Editor editor) {

    };

    protected void showWizardIllustration(final SharedPreferences.Editor editor) {
        final ImageView drawing = findViewById(sk.ab.herbsbase.R.id.plant_background);
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(drawing))
                .hideOnTouchOutside()
                .setContentTitle(sk.ab.herbsbase.R.string.showcase_fullscreen_title)
                .setContentText(sk.ab.herbsbase.R.string.showcase_fullscreen_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showWizardTaxonomy(editor);
                    }
                })
                .build();
        editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, true);
        editor.apply();
    }

    protected void showWizardTaxonomy(final SharedPreferences.Editor editor) {
        final ImageView taxonomyView = findViewById(sk.ab.herbsbase.R.id.taxonomy);
        new ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setStyle(sk.ab.herbsbase.R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(taxonomyView))
                .hideOnTouchOutside()
                .setContentTitle(sk.ab.herbsbase.R.string.showcase_taxonomy_title)
                .setContentText(sk.ab.herbsbase.R.string.showcase_taxonomy_message)
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showSpecificWizard(editor);
                    }
                })
                .build();
        editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_3_1, true);
        editor.apply();
    }
}
