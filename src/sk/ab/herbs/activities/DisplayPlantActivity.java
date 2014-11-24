package sk.ab.herbs.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import sk.ab.herbs.Constants;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantCardsFragment;
import sk.ab.herbs.fragments.PlantListFragment;
import sk.ab.herbs.fragments.rest.HerbDetailResponderFragment;
import sk.ab.tools.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for displaying selected plant
 */
public class DisplayPlantActivity extends ActionBarActivity {
    private PlantHeader plantHeader;
    private Plant plant;
    private List<PlantHeader> plants;
    private PlantListFragment mResultMenu;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private HerbDetailResponderFragment detailResponder;

    private Button countButton;
    private AnimationDrawable loadingAnimation;


    /**
     * Called when the commons is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.plant_activity);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);
        mResultMenu = (PlantListFragment)fm.findFragmentById(R.id.list_fragment);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.list_info, R.string.list_info) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(plantHeader != null ? plantHeader.getTitle() : "");
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.list_info);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.openDrawer(Gravity.LEFT);
        getSupportActionBar().setTitle(R.string.list_info);

        detailResponder = (HerbDetailResponderFragment) fm.findFragmentByTag("RESTDetailResponder");
        if (detailResponder == null) {
            detailResponder = new HerbDetailResponderFragment();
            ft.add(detailResponder, "RESTDetailResponder");
        }

        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        plants = getIntent().getExtras().getParcelableArrayList("results");
        if (plants.size() > 0) {
            setPlantHeader(plants.get(0));
            getDetailResponder().getDetail();
        }

        mResultMenu.recreateList(plants);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter, menu);
        MenuItem item = menu.findItem(R.id.count);
        countButton = (Button) item.getActionView().findViewById(R.id.countButton);
        loadingAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.loading);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.clear:
                break;
            case R.id.en:
                Utils.changeLocale(this, Constants.LANGUAGE_EN);
                Toast.makeText(this, R.string.locale_en, Toast.LENGTH_LONG).show();
                break;
            case R.id.sk:
                Utils.changeLocale(this, Constants.LANGUAGE_SK);
                Toast.makeText(this, R.string.locale_sk, Toast.LENGTH_LONG).show();
                break;
            case R.id.about:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.about)
                        .setMessage(Html.fromHtml(getString(R.string.about_msg)))
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);
        Utils.changeLocale(this, language);
    }

    public PlantHeader getPlantHeader() {
        return plantHeader;
    }

    public void setPlantHeader(PlantHeader plantHeader) {
        this.plantHeader = plantHeader;
    }

    public List<PlantHeader> getPlants() {
        return plants;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
        PlantCardsFragment plantCardsFragment = new PlantCardsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.plant_content_frame, plantCardsFragment)
                .commit();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    public HerbDetailResponderFragment getDetailResponder() {
        return detailResponder;
    }
}
