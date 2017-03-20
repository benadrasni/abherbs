package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.HashMap;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.fragments.PlantListFragment;

/**
 * Activity for displaying list of plants according to filter
 *
 */
public abstract class ListPlantsBaseActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
        } else {
            count = getIntent().getExtras().getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
        }

        setContentView(R.layout.list_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startLoading();
                Intent intent = new Intent(ListPlantsBaseActivity.this, FilterPlantsBaseActivity.class);
                intent.putExtra(AndroidConstants.STATE_FILTER_CLEAR, "true");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });

        countText = (TextView) findViewById(R.id.countText);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            ViewTreeObserver vto = mDrawerLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    mDrawerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    stopLoading();
                    setCountButton();
                }
            });


            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.list_info, R.string.list_info) {
            };

            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        PlantListFragment plantListFragment = (PlantListFragment) fm.findFragmentByTag("PlantList");
        if (plantListFragment == null) {
            ft.replace(R.id.list_content, new PlantListFragment(), "PlantList");
        }
        mPropertyMenu = getMenuFragment();
        ft.replace(R.id.menu_content, mPropertyMenu);
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.list_info);
        }

        stopLoading();
        setCountButton();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);

        super.onSaveInstanceState(savedInstanceState);
    }

    public abstract String getFilterString();

    public abstract void selectPlant(String plantName);
}
