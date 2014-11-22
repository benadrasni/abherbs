package sk.ab.herbs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import sk.ab.herbs.Constants;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantListFragment;
import sk.ab.herbs.fragments.rest.HerbDetailResponderFragment;
import sk.ab.tools.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 28.2.2013
 * Time: 18:00
 * <p/>
 * Display list of plants which satisfied a filter
 */
public class ListPlantsActivity extends Activity {
    private PlantListFragment plantsFragment;
    private List<PlantHeader> plants;
    private PlantHeader plantHeader;

    private HerbDetailResponderFragment detailResponder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        setContentView(R.layout.list_frame);
        plantsFragment = new PlantListFragment();

        ft.replace(R.id.list_frame, plantsFragment);

        detailResponder = (HerbDetailResponderFragment) fm.findFragmentByTag("RESTDetailResponder");
        if (detailResponder == null) {
            detailResponder = new HerbDetailResponderFragment();
            ft.add(detailResponder, "RESTDetailResponder");
        }

        ft.commit();

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        plants = getIntent().getExtras().getParcelableArrayList("results");

        plantsFragment.recreateList(plants);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.count);
        Button b = (Button) item.getActionView().findViewById(R.id.countButton);
        b.setText("" + plants.size());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    public List<PlantHeader> getPlants() {
        return plants;
    }

    public PlantHeader getPlantHeader() {
        return plantHeader;
    }

    public void setPlantHeader(PlantHeader plantHeader) {
        this.plantHeader = plantHeader;
    }

    public void setPlant(Plant plant) {
        Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
        intent.putExtra("plant", plant);
        startActivity(intent);
    }

    public HerbDetailResponderFragment getDetailResponder() {
        return detailResponder;
    }
}
