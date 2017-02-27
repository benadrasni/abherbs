package sk.ab.herbsbase.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.ScrollView;

import java.util.HashMap;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.commons.BaseActivity;
import sk.ab.herbsbase.commons.PropertyListFragment;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.fragments.GalleryFragment;
import sk.ab.herbsbase.fragments.InfoFragment;
import sk.ab.herbsbase.fragments.SourcesFragment;
import sk.ab.herbsbase.fragments.TaxonomyFragment;

/**
 * Activity for displaying selected plant
 *
 */
public class DisplayPlantActivity extends BaseActivity {

    PlantParcel plantParcel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            plantParcel = savedInstanceState.getParcelable(AndroidConstants.STATE_PLANT);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
        } else {
            plantParcel = getIntent().getExtras().getParcelable("plant");
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
        }

        setContentView(R.layout.plant_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.GONE);
        }

        FragmentManager fm = getSupportFragmentManager();
        mPropertyMenu = (PropertyListFragment)fm.findFragmentById(R.id.menu_fragment);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        TaxonomyFragment taxonomyFragment = (TaxonomyFragment) fm.findFragmentByTag("Taxonomy");
        if (taxonomyFragment == null) {
            ft.replace(R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
            ft.replace(R.id.info_fragment, new InfoFragment(), "Info");
            ft.replace(R.id.gallery_fragment, new GalleryFragment(), "Gallery");
            ft.replace(R.id.sources_fragment, new SourcesFragment(), "Sources");
        }
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
}
