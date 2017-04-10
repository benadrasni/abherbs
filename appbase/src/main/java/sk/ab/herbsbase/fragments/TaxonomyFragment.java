package sk.ab.herbsbase.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTaxon;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.tools.Utils;

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

        final DisplayPlantBaseActivity displayPlantActivity = (DisplayPlantBaseActivity) getActivity();
        final SharedPreferences preferences = displayPlantActivity.getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_3_1, false)
                && preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, false);
        final TextView nameView = (TextView) getView().findViewById(R.id.plant_species);

        if (showWizard) {
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(nameView))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_taxonomy_title)
                    .setContentText(R.string.showcase_taxonomy_message)
                    .build();
            editor.putBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_3_1, true);
            editor.apply();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() != null) {
            setNames(getView());

            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTaxonomy(getView());
                    Utils.setVisibility(v, R.id.plant_taxonomy);
                    Utils.setVisibility(v, R.id.agpiii);
                }
            });
        }
    }

    private void getTaxonomy(View view) {
        final DisplayPlantBaseActivity displayPlantActivity = (DisplayPlantBaseActivity) getActivity();
        final FirebasePlant plant = displayPlantActivity.getPlant();
        final LinearLayout layout = (LinearLayout) view.findViewById(R.id.plant_taxonomy);

        if (layout.isShown() || layout.getChildCount() > 0) {
            return;
        }

        displayPlantActivity.startLoading();
        displayPlantActivity.countButton.setVisibility(View.VISIBLE);

        final List<String> sortedKeys = new ArrayList<>(plant.getTaxonomy().keySet());
        Collections.sort(sortedKeys, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((String)o2).compareTo((String)o1);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference taxonomyRef = database.getReference(AndroidConstants.FIREBASE_APG_III);

        taxonomyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> taxonomy = (HashMap<String, Object>) dataSnapshot.getValue();

                List<PlantTaxon> taxons = new ArrayList<>();
                for (String key : sortedKeys) {
                    String value = plant.getTaxonomy().get(key);
                    PlantTaxon taxon = new PlantTaxon();
                    taxons.add(0, taxon);

                    taxonomy = (HashMap<String, Object>) taxonomy.get(value);
                    taxon.setType((String) taxonomy.get(AndroidConstants.FIREBASE_APGIII_TYPE));
                    taxon.setLatinName((List<String>) ((HashMap<String, Object>)taxonomy.get(AndroidConstants.FIREBASE_APGIII_NAMES)).get(Constants.LANGUAGE_LA));
                    taxon.setName((List<String>) ((HashMap<String, Object>)taxonomy.get(AndroidConstants.FIREBASE_APGIII_NAMES)).get(Locale.getDefault().getLanguage()));
                }

                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for(PlantTaxon taxon : taxons) {
                    View view = inflater.inflate(R.layout.taxon, null);
                    TextView textType = (TextView)view.findViewById(R.id.taxonType);
                    textType.setText(Utils.getId(AndroidConstants.RES_TAXONOMY_PREFIX + taxon.getType().toLowerCase(),R.string.class));

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

                    if (AndroidConstants.TAXON_ORDO.equals(taxon.getType()) || AndroidConstants.TAXON_FAMILIA.equals(taxon.getType())) {
                        textName.setTypeface(Typeface.DEFAULT_BOLD);
                    }

                    layout.addView(view);
                }
                displayPlantActivity.stopLoading();
                displayPlantActivity.countButton.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                displayPlantActivity.stopLoading();
                displayPlantActivity.countButton.setVisibility(View.GONE);
            }
        });
    }

    private void setNames(View view) {
        FirebasePlant plant = getPlant();
        PlantTranslation plantTranslation = getPlantTranslation();

        Integer toxicityClass = plant.getToxicityClass();
        if (toxicityClass == null) {
            toxicityClass = 0;
        }
        switch (toxicityClass) {
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

        boolean isLatinName = false;
        String label = null;
        if (plantTranslation != null) {
            label = plantTranslation.getLabel();
        }
        if (label == null) {
            label = plant.getName();
            isLatinName = true;
        }

        TextView species = (TextView) view.findViewById(R.id.plant_species);
        species.setText(label);
        if (!isLatinName) {
            TextView species_latin = (TextView) view.findViewById(R.id.plant_species_latin);
            species_latin.setText(plant.getName());
        }
        TextView namesView = (TextView) view.findViewById(R.id.plant_alt_names);

        if (plantTranslation != null) {
            List<String> names = plantTranslation.getNames();
            if (names != null) {
                int i = 0;
                StringBuilder namesText = new StringBuilder();
                for (String name : names) {
                    if (namesText.length() > 0) {
                        namesText.append(", ");
                    }
                    namesText.append(name);
                    i++;
                    if (i > Constants.NAMES_TO_DISPLAY) {
                        break;
                    }
                }
                if (namesText.length() > 0) {
                    namesView.setText(namesText.toString());
                } else {
                    namesView.setVisibility(View.GONE);
                }
            } else {
                namesView.setVisibility(View.GONE);
            }
        } else {
            namesView.setVisibility(View.GONE);
        }
    }

    private FirebasePlant getPlant() {
        return ((DisplayPlantBaseActivity)getActivity()).getPlant();
    }

    private PlantTranslation getPlantTranslation() {
        return ((DisplayPlantBaseActivity)getActivity()).getPlantTranslation();
    }

    private PlantTranslation getPlantTranslationGT() {
        return ((DisplayPlantBaseActivity)getActivity()).getPlantTranslationGT();
    }

    private PlantTranslation getPlantTranslationEn() {
        return ((DisplayPlantBaseActivity)getActivity()).getPlantTranslationEn();
    }
}

