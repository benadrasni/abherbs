package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;

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
    Plant plant = ((DisplayPlantActivity)getActivity()).getPlant();

    ImageView background = (ImageView) getView().findViewById(R.id.plant_background);
    if (plant.getBack_url() != null) {
      //((DisplayPlantActivity) getActivity()).getDrawableManager().fetchDrawableOnThread(plant.getBack_url(), background);
      background.setImageResource(R.drawable.background);
    }

    TextView title = (TextView) getView().findViewById(R.id.plant_species_value);
    title.setText(plant.getSpecies());

    TextView title_latin = (TextView) getView().findViewById(R.id.plant_species_latin_value);
    title_latin.setText(plant.getSpecies_latin());

    TextView flowers = (TextView) getView().findViewById(R.id.plant_flowers);
    flowers.setText(Html.fromHtml(plant.getDescWithHighlight(plant.getFlower())));

    TextView stem = (TextView) getView().findViewById(R.id.plant_stem);
    stem.setText(Html.fromHtml(plant.getDescWithHighlight(plant.getStem())));

    TextView leaves = (TextView) getView().findViewById(R.id.plant_leaves);
    leaves.setText(Html.fromHtml(plant.getDescWithHighlight(plant.getLeaf())));

    TextView habitat = (TextView) getView().findViewById(R.id.plant_habitat);
    habitat.setText(Html.fromHtml(plant.getDescWithHighlight(plant.getHabitat())));

  }
}
