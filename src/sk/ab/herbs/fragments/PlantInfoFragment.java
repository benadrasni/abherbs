package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlant;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 5.3.2013
 * Time: 21:27
 * To change this template use File | Settings | File Templates.
 */
public class PlantInfoFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.plant_info, null);
  }

  @Override
  public void onStart() {
    fillPlant();
    super.onStart();
  }

  private void fillPlant() {
    Plant plant = ((DisplayPlant)getActivity()).getPlant();

    TextView title = (TextView) getView().findViewById(R.id.plant_title_value);
    title.setText(plant.getTitle());

    TextView family = (TextView) getView().findViewById(R.id.plant_family_value);
    family.setText(plant.getFamily());
  }
}
