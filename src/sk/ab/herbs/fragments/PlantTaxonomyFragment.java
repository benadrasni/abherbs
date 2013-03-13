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
public class PlantTaxonomyFragment extends Fragment {

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.plant_taxonomy, null);
  }

  @Override
  public void onStart() {
    fillPlant();
    super.onStart();
  }

  private void fillPlant() {
    Plant plant = ((DisplayPlant)getActivity()).getPlant();

    TextView species = (TextView) getView().findViewById(R.id.plant_species_value);
    species.setText(plant.getSpeciesShort());
    TextView species_latin = (TextView) getView().findViewById(R.id.plant_species_value_latin);
    species_latin.setText(plant.getSpecies_latinShort());

    TextView genus = (TextView) getView().findViewById(R.id.plant_genus_value);
    genus.setText(plant.getGenus());
    TextView genus_latin = (TextView) getView().findViewById(R.id.plant_genus_value_latin);
    genus_latin.setText(plant.getGenus_latin());

    TextView family = (TextView) getView().findViewById(R.id.plant_family_value);
    family.setText(plant.getFamily());
    TextView family_latin = (TextView) getView().findViewById(R.id.plant_family_value_latin);
    family_latin.setText(plant.getFamily_latin());

    TextView order = (TextView) getView().findViewById(R.id.plant_order_value);
    order.setText(plant.getOrder());
    TextView order_latin = (TextView) getView().findViewById(R.id.plant_order_value_latin);
    order_latin.setText(plant.getOrder_latin());

    TextView cls = (TextView) getView().findViewById(R.id.plant_cls_value);
    cls.setText(plant.getCls());
    TextView cls_latin = (TextView) getView().findViewById(R.id.plant_cls_value_latin);
    cls_latin.setText(plant.getCls_latin());

    TextView phylum = (TextView) getView().findViewById(R.id.plant_phylum_value);
    phylum.setText(plant.getPhylum());
    TextView phylum_latin = (TextView) getView().findViewById(R.id.plant_phylum_value_latin);
    phylum_latin.setText(plant.getPhylum_latin());

    TextView branch = (TextView) getView().findViewById(R.id.plant_branch_value);
    branch.setText(plant.getBranch());
    TextView branch_latin = (TextView) getView().findViewById(R.id.plant_branch_value_latin);
    branch_latin.setText(plant.getBranch_latin());

    TextView line = (TextView) getView().findViewById(R.id.plant_line_value);
    line.setText(plant.getLine());
    TextView line_latin = (TextView) getView().findViewById(R.id.plant_line_value_latin);
    line_latin.setText(plant.getLine_latin());

    TextView subkingdom = (TextView) getView().findViewById(R.id.plant_subkingdom_value);
    subkingdom.setText(plant.getSubkingdom());
    TextView subkingdom_latin = (TextView) getView().findViewById(R.id.plant_subkingdom_value_latin);
    subkingdom_latin.setText(plant.getSubkingdom_latin());

    TextView kingdom = (TextView) getView().findViewById(R.id.plant_kingdom_value);
    kingdom.setText(plant.getKingdom());
    TextView kingdom_latin = (TextView) getView().findViewById(R.id.plant_kingdom_value_latin);
    kingdom_latin.setText(plant.getKingdom_latin());

    TextView domain = (TextView) getView().findViewById(R.id.plant_domain_value);
    domain.setText(plant.getDomain());
    TextView domain_latin = (TextView) getView().findViewById(R.id.plant_domain_value_latin);
    domain_latin.setText(plant.getDomain_latin());

  }
}
