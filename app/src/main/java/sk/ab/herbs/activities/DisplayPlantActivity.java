package sk.ab.herbs.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import sk.ab.commons.BaseActivity;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.GalleryFragment;
import sk.ab.herbs.fragments.InfoFragment;
import sk.ab.herbs.fragments.PlantListFragment;
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

    private Plant plant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            plant = savedInstanceState.getParcelable(STATE_PLANT);
        }

        setContentView(R.layout.plant_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onStart() {
        super.onStart();
        plant = getIntent().getExtras().getParcelable("plant");
        getSupportActionBar().setTitle(R.string.display_info);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.taxonomy_fragment, new TaxonomyFragment());
        ft.replace(R.id.info_fragment, new InfoFragment());
        ft.replace(R.id.gallery_fragment, new GalleryFragment());
        ft.replace(R.id.sources_fragment, new SourcesFragment());
        ft.commit();

        ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        scrollview.scrollTo(0, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(STATE_PLANT, plant);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading();
                ((HerbsApp)getApplication()).getFilter().clear();
                Intent intent = new Intent(DisplayPlantActivity.this, FilterPlantsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    public Plant getPlant() {
        return plant;
    }
}
