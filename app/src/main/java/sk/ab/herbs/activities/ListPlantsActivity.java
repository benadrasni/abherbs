package sk.ab.herbs.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.util.List;
import java.util.Locale;

import sk.ab.commons.BaseActivity;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantListFragment;
import sk.ab.herbs.fragments.rest.HerbDetailResponderFragment;

/**
 * User: adrian
 * Date: 10.1.2015
 * <p/>
 * Activity for displaying list of plants according to filter
 */
public class ListPlantsActivity extends BaseActivity {
    private List<PlantHeader> plants;

    private HerbDetailResponderFragment detailResponder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plants = getIntent().getExtras().getParcelableArrayList("results");

        setContentView(R.layout.list_activity);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.list_info, R.string.list_info) {
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        detailResponder = (HerbDetailResponderFragment) fm.findFragmentByTag("RESTDetailResponder");
        if (detailResponder == null) {
            detailResponder = new HerbDetailResponderFragment();
            ft.add(detailResponder, "RESTDetailResponder");
        }
        PlantListFragment plantListFragment = (PlantListFragment) fm.findFragmentByTag("PlantList");
        if (plantListFragment == null) {
            ft.replace(R.id.list_content, new PlantListFragment(), "PlantList");
        }
        ft.commit();

        getSupportActionBar().setTitle(R.string.list_info);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading();
                ((HerbsApp)getApplication()).getFilter().clear();
                Intent intent = new Intent(ListPlantsActivity.this, FilterPlantsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    public void selectPlant(int position) {
        loading();
        detailResponder.getDetail(plants.get(position).getPlantId());
    }

    public void setPlant(Plant plant) {
        Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
        intent.putExtra("plant", plant);
        startActivity(intent);
        invalidateOptionsMenu();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ((HerbsApp)getApplication()).setLoading(false);
    }

    public List<PlantHeader> getPlants() {
        return plants;
    }
}
