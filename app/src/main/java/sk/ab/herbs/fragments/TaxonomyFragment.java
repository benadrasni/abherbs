package sk.ab.herbs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantTaxonomy;
import sk.ab.herbs.R;
import sk.ab.herbs.TranslationSave;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.service.HerbCloudClient;
import sk.ab.herbs.tools.Keys;
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
            final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
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
                    getTaxonomy(displayPlantActivity.getPlant(), getView());
                }
            });
        }
    }

    private void getTaxonomy(Plant plant, View view) {
        final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.plant_taxonomy);

        if (layout.isShown() || layout.getChildCount() > 0) {
            return;
        }

        final HerbCloudClient herbCloudClient = new HerbCloudClient();

//        displayPlantActivity.startLoading();
//        displayPlantActivity.countButton.setVisibility(View.VISIBLE);
        herbCloudClient.getApiService().getTaxonomy("Familia", displayPlantActivity.getPlant().getFamily_latin(),
                Locale.getDefault().getLanguage()).enqueue(new Callback<PlantTaxonomy>() {
                    @Override
                    public void onResponse(Response<PlantTaxonomy> response) {
                        if (response != null) {


                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                    }
                });


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

//        TextView family = (TextView) view.findViewById(R.id.plant_family);
//        family.setText(plant.getFamily());
//        TextView family_latin = (TextView) view.findViewById(R.id.plant_family_latin);
//        family_latin.setText(plant.getFamily_latin());
//
//        TextView order = (TextView) view.findViewById(R.id.plant_order);
//        order.setText(plant.getOrder());
//        TextView order_latin = (TextView) view.findViewById(R.id.plant_order_latin);
//        order_latin.setText(plant.getOrder_latin());
//
//        TextView cls = (TextView) view.findViewById(R.id.plant_cls);
//        cls.setText(plant.getCls());
//        TextView cls_latin = (TextView) view.findViewById(R.id.plant_cls_latin);
//        cls_latin.setText(plant.getCls_latin());
//
//        TextView phylum = (TextView) view.findViewById(R.id.plant_phylum);
//        phylum.setText(plant.getPhylum());
//        TextView phylum_latin = (TextView) view.findViewById(R.id.plant_phylum_latin);
//        phylum_latin.setText(plant.getPhylum_latin());
//
//        TextView branch = (TextView) view.findViewById(R.id.plant_branch);
//        branch.setText(plant.getBranch());
//        TextView branch_latin = (TextView) view.findViewById(R.id.plant_branch_latin);
//        branch_latin.setText(plant.getBranch_latin());
//
//        TextView line = (TextView) view.findViewById(R.id.plant_line);
//        line.setText(plant.getLine());
//        TextView line_latin = (TextView) view.findViewById(R.id.plant_line_latin);
//        line_latin.setText(plant.getLine_latin());
//
//        TextView subKingdom = (TextView) view.findViewById(R.id.plant_subkingdom);
//        subKingdom.setText(plant.getSubkingdom());
//        TextView subKingdom_latin = (TextView) view.findViewById(R.id.plant_subkingdom_latin);
//        subKingdom_latin.setText(plant.getSubkingdom_latin());
//
//        TextView kingdom = (TextView) view.findViewById(R.id.plant_kingdom);
//        kingdom.setText(plant.getKingdom());
//        TextView kingdom_latin = (TextView) view.findViewById(R.id.plant_kingdom_latin);
//        kingdom_latin.setText(plant.getKingdom_latin());
//
//        TextView domain = (TextView) view.findViewById(R.id.plant_domain);
//        domain.setText(plant.getDomain());
//        TextView domain_latin = (TextView) view.findViewById(R.id.plant_domain_latin);
//        domain_latin.setText(plant.getDomain_latin());
    }
}

