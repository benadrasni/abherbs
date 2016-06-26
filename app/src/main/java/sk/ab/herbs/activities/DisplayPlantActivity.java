package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;

import java.util.Locale;

import sk.ab.common.entity.Plant;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.commons.BaseActivity;
import sk.ab.herbs.fragments.GalleryFragment;
import sk.ab.herbs.fragments.InfoFragment;
import sk.ab.herbs.fragments.SourcesFragment;
import sk.ab.herbs.fragments.TaxonomyFragment;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for displaying selected plant
 */
public class DisplayPlantActivity extends BaseActivity {
    static final String STATE_PLANT = "plant";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState != null) {
//            plant = savedInstanceState.getParcelable(STATE_PLANT);
//        } else {
//            plant = getIntent().getExtras().getParcelable("plant");
//        }

//        builder.set("plant", ""+getPlant().getPlantId());
//        ((HerbsApp)getApplication()).getTracker().send(builder.build());


        setContentView(R.layout.plant_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        if (countButton != null) {
            countButton.setVisibility(View.GONE);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        TaxonomyFragment taxonomyFragment = (TaxonomyFragment) fm.findFragmentByTag("Taxonomy");
        if (taxonomyFragment == null) {
            ft.replace(R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
            ft.replace(R.id.info_fragment, new InfoFragment(), "Info");
            ft.replace(R.id.gallery_fragment, new GalleryFragment(), "Gallery");
            ft.replace(R.id.sources_fragment, new SourcesFragment(), "Sources");
        }
        ft.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.display_info);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        if (scrollview != null) {
            scrollview.scrollTo(0, 0);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putParcelable(STATE_PLANT, plant);
//
//        super.onSaveInstanceState(savedInstanceState);
//    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }
}
