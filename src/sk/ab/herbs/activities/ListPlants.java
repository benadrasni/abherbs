package sk.ab.herbs.activities;

import android.os.Bundle;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import sk.ab.herbs.*;
import sk.ab.herbs.fragments.PlantListFragment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 28.2.2013
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
public class ListPlants extends SherlockFragmentActivity {
  private PlantListFragment plantsFragment;

  private List<PlantHeader> plants;

  /**
   * Called when the commons is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // set the Content View
    setContentView(R.layout.result_list_frame);
    plantsFragment = new PlantListFragment();
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.result_list_frame, plantsFragment)
        .commit();

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
    getSupportMenuInflater().inflate(R.menu.result_list, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    MenuItem item = menu.findItem(R.id.count);
    Button b = (Button)item.getActionView().findViewById(R.id.countButton);
    b.setText(""+plants.size());

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
}
