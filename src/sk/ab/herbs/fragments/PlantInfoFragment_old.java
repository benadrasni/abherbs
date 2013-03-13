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
public class PlantInfoFragment_old extends Fragment {

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

    TextView inflorescence = (TextView) getView().findViewById(R.id.plant_inflorescence_value);
    inflorescence.setText(plant.getInflorescence());

    TextView color_of_flower = (TextView) getView().findViewById(R.id.plant_color_of_flower_value);
    color_of_flower.setText(plant.getFlower_color());

    TextView number_of_petals = (TextView) getView().findViewById(R.id.plant_numbers_of_petals_value);
    number_of_petals.setText(plant.getNumber_of_petals());

    TextView sepal = (TextView) getView().findViewById(R.id.plant_sepal_value);
    sepal.setText(plant.getSepal());

    TextView leaf_shape = (TextView) getView().findViewById(R.id.plant_leaf_shape_value);
    leaf_shape.setText(plant.getLeaf_shape());

    TextView leaf_margin = (TextView) getView().findViewById(R.id.plant_leaf_margin_value);
    leaf_margin.setText(plant.getLeaf_margin());

    TextView leaf_venation = (TextView) getView().findViewById(R.id.plant_leaf_venation_value);
    leaf_venation.setText(plant.getLeaf_venation());

    TextView leaf_arrangement = (TextView) getView().findViewById(R.id.plant_leaf_arrangement_value);
    leaf_arrangement.setText(plant.getLeaf_arrangement());

    TextView stem = (TextView) getView().findViewById(R.id.plant_stem_value);
    stem.setText(plant.getStem());

    TextView root = (TextView) getView().findViewById(R.id.plant_root_value);
    root.setText(plant.getRoot());

    TextView habitat = (TextView) getView().findViewById(R.id.plant_habitat_value);
    habitat.setText(plant.getHabitat());
  }
}
