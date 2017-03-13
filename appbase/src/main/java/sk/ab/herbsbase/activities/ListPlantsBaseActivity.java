package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sk.ab.common.entity.Plant;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.entity.PlantHeaderParcel;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.fragments.PlantListFragment;

/**
 * Activity for displaying list of plants according to filter
 *
 */
public abstract class ListPlantsBaseActivity extends BaseActivity {

    private ArrayList<PlantHeaderParcel> plantList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            plantList = savedInstanceState.getParcelableArrayList(AndroidConstants.STATE_PLANT_LIST);
            count = savedInstanceState.getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
        } else {
            plantList = getIntent().getExtras().getParcelableArrayList(AndroidConstants.STATE_PLANT_LIST);
            count = getIntent().getExtras().getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
        }

        final BaseApp app = (BaseApp) getApplication();

        app.getTracker().send(builder.build());

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
        savedInstanceState.putParcelableArrayList(AndroidConstants.STATE_PLANT_LIST, plantList);
        savedInstanceState.putInt(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);

        super.onSaveInstanceState(savedInstanceState);
    }

    public List<PlantHeaderParcel> getPlantList() {
        return plantList;
    }

    public void selectPlant(int position) {
        startLoading();

        getApp().getHerbCloudClient().getApiService().getDetail(getPlantList().get(position).getId()).enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {
                Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
                intent.putExtra(AndroidConstants.STATE_PLANT, new PlantParcel(response.body()));
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                startActivity(intent);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                stopLoading();
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
            }
        });

    }
}
