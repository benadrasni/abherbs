package sk.ab.herbs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantListFragment;
import sk.ab.herbs.fragments.rest.HerbDetailResponderFragment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 28.2.2013
 * Time: 18:00
 * <p/>
 * Display list of plants which satisfied a filter
 */
public class ListPlantsActivity extends ActionBarActivity {
    private PlantListFragment plantsFragment;
    private List<PlantHeader> plants;
    private PlantHeader plantHeader;

    private HerbDetailResponderFragment detailResponder;


    /**
     * Called when the commons is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
