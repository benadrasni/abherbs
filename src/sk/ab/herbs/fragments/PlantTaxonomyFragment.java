package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sk.ab.herbs.R;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 5.3.2013
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class PlantTaxonomyFragment extends Fragment {

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.plant_taxonomy, null);
  }
}
