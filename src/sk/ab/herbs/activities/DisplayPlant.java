package sk.ab.herbs.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantGalleryFragment;
import sk.ab.herbs.fragments.PlantInfoFragment;
import sk.ab.herbs.fragments.PlantTaxonomyFragment;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class DisplayPlant extends SherlockFragmentActivity implements ActionBar.TabListener {
  private Plant plant;
  private PlantInfoFragment infoFragment;
  private PlantGalleryFragment galleryFragment;
  private PlantTaxonomyFragment taxonomyFragment;

  /**
   * Called when the commons is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // set the Content View
    setContentView(R.layout.plant_content_frame);

    getSupportActionBar().setDisplayShowHomeEnabled(false);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

    ActionBar.Tab tab = getSupportActionBar().newTab();
    tab.setText(R.string.plant_info);
    tab.setTabListener(this);
    getSupportActionBar().addTab(tab);

    tab = getSupportActionBar().newTab();
    tab.setText(R.string.plant_gallery);
    tab.setTabListener(this);
    getSupportActionBar().addTab(tab);

    tab = getSupportActionBar().newTab();
    tab.setText(R.string.plant_taxonomy);
    tab.setTabListener(this);
    getSupportActionBar().addTab(tab);
  }

  @Override
  public void onStart() {
    PlantHeader plantHeader = getIntent().getExtras().getParcelable("plantHeader");
    setPlant(new Plant(plantHeader));
    super.onStart();
  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
    if(getResources().getString(R.string.plant_info).equals(tab.getText())) {
      transaction.replace(R.id.plant_content_frame, getInfoFragment());
    } else if(getResources().getString(R.string.plant_gallery).equals(tab.getText())) {
      transaction.replace(R.id.plant_content_frame, getGalleryFragment());
    } else if(getResources().getString(R.string.plant_taxonomy).equals(tab.getText())) {
      transaction.replace(R.id.plant_content_frame, getTaxonomyFragment());
    }
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {
  }

  private PlantInfoFragment getInfoFragment() {
    if (infoFragment == null) {
      infoFragment = new PlantInfoFragment();
    }
    return infoFragment;
  }

  private PlantGalleryFragment getGalleryFragment() {
    if (galleryFragment == null) {
      galleryFragment = new PlantGalleryFragment();
    }
    return galleryFragment;
  }

  private PlantTaxonomyFragment getTaxonomyFragment() {
    if (taxonomyFragment == null) {
      taxonomyFragment = new PlantTaxonomyFragment();
    }
    return taxonomyFragment;
  }

  private void setPlant(Plant plant) {
    this.plant = plant;
  }

  public Plant getPlant() {
    return plant;
  }
}
