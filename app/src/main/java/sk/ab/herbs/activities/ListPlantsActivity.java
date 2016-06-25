package sk.ab.herbs.activities;

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

import retrofit.Callback;
import retrofit.Response;
import sk.ab.common.entity.Plant;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.commons.BaseActivity;
import sk.ab.herbs.fragments.PlantListFragment;

/**
 * User: adrian
 * Date: 10.1.2015
 * <p/>
 * Activity for displaying list of plants according to filter
 */
public class ListPlantsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final HerbsApp app = (HerbsApp) getApplication();

        app.getTracker().send(builder.build());

        setContentView(R.layout.list_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startLoading();
                app.getFilter().clear();
                Intent intent = new Intent(ListPlantsActivity.this, FilterPlantsActivity.class);
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
        ft.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.list_info);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        stopLoading();
        setCountButton();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    public void selectPlant(int position) {
        final HerbsApp app = (HerbsApp) getApplication();
        startLoading();

        app.getHerbCloudClient().getApiService().getDetail(app.getPlantList().get(position).getId()).enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Response<Plant> response) {
                app.setPlant(response.body());
                Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
                startActivity(intent);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                stopLoading();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });

    }
}
