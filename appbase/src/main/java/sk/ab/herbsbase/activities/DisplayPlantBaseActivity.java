package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.entity.PlantTranslationParcel;
import sk.ab.herbsbase.fragments.GalleryFragment;
import sk.ab.herbsbase.fragments.InfoFragment;
import sk.ab.herbsbase.fragments.SourcesFragment;
import sk.ab.herbsbase.fragments.TaxonomyFragment;

/**
 * Activity for displaying selected plant
 *
 */
public abstract class DisplayPlantBaseActivity extends BaseActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

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
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_LOCATION_ID, Locale.getDefault().getLanguage());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, plantParcel.getName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }

        setContentView(R.layout.plant_activity);

        overlay = findViewById(R.id.overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countText = (TextView) findViewById(R.id.countText);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

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

        ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        if (scrollview != null) {
            scrollview.scrollTo(0, 0);
        }
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
}
