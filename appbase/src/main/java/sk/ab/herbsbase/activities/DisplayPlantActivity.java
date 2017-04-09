package sk.ab.herbsbase.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;

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
public class DisplayPlantActivity extends BaseActivity {

    private PlantParcel plantParcel;
    private PlantTranslationParcel plantTranslationParcel;
    private PlantTranslationParcel plantTranslationEnParcel;
    private PlantTranslationParcel plantTranslationGTParcel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            plantParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_PLANT);
            plantTranslationParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE);
            plantTranslationGTParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT);
            plantTranslationEnParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
        } else {
            plantParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_PLANT);
            plantTranslationParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE);
            plantTranslationGTParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT);
            plantTranslationEnParcel = getIntent().getExtras().getParcelable(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH);
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
        }

        setContentView(R.layout.plant_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.GONE);
        }

        countText = (TextView) findViewById(R.id.countText);
        if (countText != null) {
            countText.setVisibility(View.GONE);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mPropertyMenu = getMenuFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TaxonomyFragment taxonomyFragment = (TaxonomyFragment) fm.findFragmentByTag("Taxonomy");
        if (taxonomyFragment == null) {
            ft.replace(R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
            ft.replace(R.id.info_fragment, new InfoFragment(), "Info");
            ft.replace(R.id.gallery_fragment, new GalleryFragment(), "Gallery");
            ft.replace(R.id.sources_fragment, new SourcesFragment(), "Sources");
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

    public PlantParcel getPlant() {
        return plantParcel;
    }

    public PlantTranslationParcel getPlantTranslation() {
        return plantTranslationParcel;
    }

    public PlantTranslationParcel getPlantTranslationGT() {
        return plantTranslationGTParcel;
    }

    public PlantTranslationParcel getPlantTranslationEn() {
        return plantTranslationEnParcel;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(AndroidConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
