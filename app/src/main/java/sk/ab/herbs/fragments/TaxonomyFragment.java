package sk.ab.herbs.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantTaxon;
import sk.ab.herbs.R;
import sk.ab.herbs.Taxonomy;
import sk.ab.herbs.activities.DisplayPlantActivity;
import sk.ab.herbs.service.HerbCloudClient;
import sk.ab.herbs.tools.Utils;


/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class TaxonomyFragment extends Fragment {

    private static String TAXON_LANGUAGE = "la";
    private static String TAXON_GENUS = "Genus";
    private static String TAXON_ORDO = "Ordo";
    private static String TAXON_FAMILIA = "Familia";

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
                    getTaxonomy(displayPlantActivity.getPlant(), getView());
                    Utils.setVisibility(v, R.id.plant_taxonomy);
                }
            });
        }
    }

    private void getTaxonomy(Plant plant, View view) {
        final DisplayPlantActivity displayPlantActivity = (DisplayPlantActivity) getActivity();
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.plant_taxonomy);

        if (layout.isShown() || layout.getChildCount() > 0) {
            return;
        }

        final HerbCloudClient herbCloudClient = new HerbCloudClient();

//        displayPlantActivity.startLoading();
//        displayPlantActivity.countButton.setVisibility(View.VISIBLE);
        String[] latinName = displayPlantActivity.getPlant().getSpecies_latin().split(" ");
        herbCloudClient.getApiService().getTaxonomy(TAXON_LANGUAGE, TAXON_GENUS, latinName[0], Locale.getDefault().getLanguage())
                .enqueue(new Callback<Taxonomy>() {
                    @Override
                    public void onResponse(Response<Taxonomy> response) {
                        if (response != null && response.body() != null) {
                            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            for(PlantTaxon taxon : response.body().getItems()) {
                                View view = inflater.inflate(R.layout.taxon, null);
                                TextView textType = (TextView)view.findViewById(R.id.taxonType);
                                textType.setText(taxon.getType());

                                TextView textName = (TextView)view.findViewById(R.id.taxonName);
                                StringBuilder sbName = new StringBuilder();
                                if (taxon.getName() != null) {
                                    for (String s : taxon.getName()) {
                                        if (sbName.length() > 0) {
                                            sbName.append(", ");
                                        }
                                        sbName.append(s);
                                    }
                                }

                                TextView textLatinName = (TextView)view.findViewById(R.id.taxonLatinName);
                                StringBuilder sbLatinName = new StringBuilder();
                                if (taxon.getLatinName() != null) {
                                    for (String s : taxon.getLatinName()) {
                                        if (sbLatinName.length() > 0) {
                                            sbLatinName.append(", ");
                                        }
                                        sbLatinName.append(s);
                                    }
                                }

                                if (sbName.length() > 0) {
                                    textName.setText(sbName.toString());
                                    if (sbLatinName.length() > 0) {
                                        textLatinName.setText(sbLatinName.toString());
                                    } else {
                                        textLatinName.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (sbLatinName.length() > 0) {
                                        textName.setText(sbLatinName.toString());
                                    } else {
                                        textName.setVisibility(View.GONE);
                                    }
                                    textLatinName.setVisibility(View.GONE);
                                }

                                if (taxon.getType().equals(TAXON_ORDO) || taxon.getType().equals(TAXON_FAMILIA)) {
                                    textName.setTypeface(Typeface.DEFAULT_BOLD);
                                }

                                layout.addView(view);
                            }

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

