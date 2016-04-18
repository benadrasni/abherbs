package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class TaxonomyFragment extends Fragment {

    private ImageView toxicityClass1;
    private ImageView toxicityClass2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity().getBaseContext(), R.layout.plant_card_taxonomy, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {
            toxicityClass1 = (ImageView) getView().findViewById(R.id.plant_toxicity_class1);
            toxicityClass2 = (ImageView) getView().findViewById(R.id.plant_toxicity_class2);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
            switch (displayPlantActivity.getPlant().getToxicity_class()) {
                case 1:
                    toxicityClass1.setVisibility(View.VISIBLE);
                    toxicityClass2.setVisibility(View.GONE);
                    break;
                case 2:
                    toxicityClass1.setVisibility(View.GONE);
                    toxicityClass2.setVisibility(View.VISIBLE);
                    break;
                default:
                    toxicityClass1.setVisibility(View.GONE);
                    toxicityClass2.setVisibility(View.GONE);
            }

            setTaxonomy(displayPlantActivity.getPlant(), getView());
            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.setVisibility(v, R.id.plant_taxonomy);
                }
            });
        }
    }

    private void setTaxonomy(Plant plant, View view) {
        TextView species = (TextView) view.findViewById(R.id.plant_species);
        species.setText(plant.getSpecies());
        TextView species_latin = (TextView) view.findViewById(R.id.plant_species_latin);
        species_latin.setText(plant.getSpecies_latin());
        TextView namesView = (TextView) view.findViewById(R.id.plant_alt_names);
        StringBuilder names = new StringBuilder();
        for(String name: plant.getNames()) {
            names.append(", ");
            names.append(name);
        }
        if (names.length() > 0) {
            namesView.setText(names.toString().substring(2));
        } else {
            namesView.setVisibility(View.GONE);
        }

        TextView family = (TextView) view.findViewById(R.id.plant_family);
        family.setText(plant.getFamily());
        TextView family_latin = (TextView) view.findViewById(R.id.plant_family_latin);
        family_latin.setText(plant.getFamily_latin());

        TextView order = (TextView) view.findViewById(R.id.plant_order);
        order.setText(plant.getOrder());
        TextView order_latin = (TextView) view.findViewById(R.id.plant_order_latin);
        order_latin.setText(plant.getOrder_latin());

        TextView cls = (TextView) view.findViewById(R.id.plant_cls);
        cls.setText(plant.getCls());
        TextView cls_latin = (TextView) view.findViewById(R.id.plant_cls_latin);
        cls_latin.setText(plant.getCls_latin());

        TextView phylum = (TextView) view.findViewById(R.id.plant_phylum);
        phylum.setText(plant.getPhylum());
        TextView phylum_latin = (TextView) view.findViewById(R.id.plant_phylum_latin);
        phylum_latin.setText(plant.getPhylum_latin());

        TextView branch = (TextView) view.findViewById(R.id.plant_branch);
        branch.setText(plant.getBranch());
        TextView branch_latin = (TextView) view.findViewById(R.id.plant_branch_latin);
        branch_latin.setText(plant.getBranch_latin());

        TextView line = (TextView) view.findViewById(R.id.plant_line);
        line.setText(plant.getLine());
        TextView line_latin = (TextView) view.findViewById(R.id.plant_line_latin);
        line_latin.setText(plant.getLine_latin());

        TextView subKingdom = (TextView) view.findViewById(R.id.plant_subkingdom);
        subKingdom.setText(plant.getSubkingdom());
        TextView subKingdom_latin = (TextView) view.findViewById(R.id.plant_subkingdom_latin);
        subKingdom_latin.setText(plant.getSubkingdom_latin());

        TextView kingdom = (TextView) view.findViewById(R.id.plant_kingdom);
        kingdom.setText(plant.getKingdom());
        TextView kingdom_latin = (TextView) view.findViewById(R.id.plant_kingdom_latin);
        kingdom_latin.setText(plant.getKingdom_latin());

        TextView domain = (TextView) view.findViewById(R.id.plant_domain);
        domain.setText(plant.getDomain());
        TextView domain_latin = (TextView) view.findViewById(R.id.plant_domain_latin);
        domain_latin.setText(plant.getDomain_latin());
    }
}

