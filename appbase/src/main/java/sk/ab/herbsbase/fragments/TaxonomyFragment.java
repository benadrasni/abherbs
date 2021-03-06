package sk.ab.herbsbase.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTaxon;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsbase.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class TaxonomyFragment extends Fragment {
    private static final String TAG = "TaxonomyFragment";

    private long mLastClickTime;

    private ImageView toxicityClass1;
    private ImageView toxicityClass2;

    private DisplayPlantBaseActivity displayPlantBaseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayPlantBaseActivity = (DisplayPlantBaseActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(displayPlantBaseActivity.getBaseContext(), R.layout.plant_card_taxonomy, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {
            toxicityClass1 = getView().findViewById(R.id.plant_toxicity_class1);
            toxicityClass1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(displayPlantBaseActivity, displayPlantBaseActivity.getResources().getText(R.string.toxicity1), Toast.LENGTH_LONG).show();
                }
            });
            toxicityClass2 = getView().findViewById(R.id.plant_toxicity_class2);
            toxicityClass2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(displayPlantBaseActivity, displayPlantBaseActivity.getResources().getText(R.string.toxicity2), Toast.LENGTH_LONG).show();
                }
            });
        }

        setHeader();

        final LinearLayout layout = getView().findViewById(R.id.plant_taxonomy);
        final ImageView taxonomyView = getView().findViewById(R.id.taxonomy);
        taxonomyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                    getTaxonomy(layout);
                    setAltNames(layout.isShown());
                    Utils.setVisibility(getView(), R.id.synonyms);
                    Utils.setVisibility(getView(), R.id.plant_taxonomy);
                    Utils.setVisibility(getView(), R.id.apg);
                }
            }
        });
    }

    private void getTaxonomy(final LinearLayout layout) {
        if (layout.isShown() || layout.getChildCount() > 0) {
            return;
        }
		
		final FirebasePlant plant = displayPlantBaseActivity.getPlant();

        displayPlantBaseActivity.startLoading();
        displayPlantBaseActivity.countButton.setVisibility(View.VISIBLE);

        final List<String> sortedKeys = new ArrayList<>(plant.getAPGIV().keySet());
        Collections.sort(sortedKeys, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareTo(s1);
            }
        });

        StringBuilder taxonomyPath = new StringBuilder();
		for (String taxon : sortedKeys) {
			taxonomyPath.append("/");
			taxonomyPath.append(plant.getAPGIV().get(taxon));
		}
		
		final SynchronizedCounter counter = new SynchronizedCounter();
		final int maxCounter = sortedKeys.size() * 2;
		
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		
		final List<PlantTaxon> taxons = new ArrayList<>();
		String taxonomy = taxonomyPath.toString();
		for (int i = sortedKeys.size() -1; i >=0; i--) {
			final PlantTaxon taxon = new PlantTaxon();
			taxon.setLatinName(plant.getAPGIV().get(sortedKeys.get(i)));
            taxons.add(taxon);
			
			// type
			DatabaseReference taxonTypeRef = database.getReference(AndroidConstants.FIREBASE_APG_IV + taxonomy + AndroidConstants.SEPARATOR + AndroidConstants.FIREBASE_APG_TYPE);
			taxonTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					Object type = dataSnapshot.getValue();
					if (type == null) {
						Crashlytics.log("Wrong APG IV type: " + plant.getName());
						taxon.setType(AndroidConstants.FIREBASE_APG_UNKNOWN_TYPE);
					} else {
						taxon.setType((String) type);
					}
				
					counter.increment();
				
					if (counter.value() == maxCounter) {
						printTaxonomy(layout, taxons);
					}
				}
				
				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.e(this.getClass().getName(), databaseError.getMessage());
					Toast.makeText(displayPlantBaseActivity.getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
					displayPlantBaseActivity.stopLoading();
					displayPlantBaseActivity.countButton.setVisibility(View.GONE);
				}
			});	
			
			// name
			DatabaseReference taxonNameRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS_TAXONOMY + AndroidConstants.SEPARATOR + Locale.getDefault().getLanguage() + AndroidConstants.SEPARATOR + taxon.getLatinName());
			taxonNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					taxon.setName((List<String>) dataSnapshot.getValue());
				
					counter.increment();
				
					if (counter.value() == maxCounter) {
						printTaxonomy(layout, taxons);
					}
				}
				
				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.e(this.getClass().getName(), databaseError.getMessage());
					Toast.makeText(displayPlantBaseActivity.getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
					displayPlantBaseActivity.stopLoading();
					displayPlantBaseActivity.countButton.setVisibility(View.GONE);
				}
			});	

			taxonomy = taxonomy.substring(0, taxonomy.lastIndexOf("/"));
		}
	}
	
	private void printTaxonomy(final LinearLayout layout, List<PlantTaxon> taxons) {
		LayoutInflater inflater = (LayoutInflater) displayPlantBaseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (PlantTaxon taxon : taxons) {
			View view = inflater.inflate(R.layout.taxon, null);
			TextView textType = view.findViewById(R.id.taxonType);
			textType.setText(Utils.getId(AndroidConstants.RES_TAXONOMY_PREFIX + taxon.getType().toLowerCase(), R.string.class));

			TextView textName = view.findViewById(R.id.taxonName);
			StringBuilder sbName = new StringBuilder();
			if (taxon.getName() != null) {
				for (String s : taxon.getName()) {
					if (sbName.length() > 0) {
						sbName.append(", ");
					}
					sbName.append(s);
				}
			}

			TextView textLatinName = view.findViewById(R.id.taxonLatinName);
			if (sbName.length() > 0) {
				textName.setText(sbName.toString());
    			textLatinName.setText(taxon.getLatinName());
			} else {
				textName.setText(taxon.getLatinName());
				textLatinName.setVisibility(View.GONE);
			}

			if (AndroidConstants.TAXON_ORDO.equals(taxon.getType()) || AndroidConstants.TAXON_FAMILIA.equals(taxon.getType())) {
				textName.setTypeface(Typeface.DEFAULT_BOLD);
			}

			layout.addView(view);
		}
		displayPlantBaseActivity.stopLoading();
		displayPlantBaseActivity.countButton.setVisibility(View.GONE);	
	}

    private void setHeader() {
        FirebasePlant plant = getPlant();
        if (plant == null) {
            return;
        }
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

        TextView species = getView().findViewById(R.id.plant_species);
        species.setText(label);
        if (!isLatinName) {
            TextView speciesLatin = getView().findViewById(R.id.plant_species_latin);
            speciesLatin.setText(plant.getName());
        }

        setAltNames(false);

        TextView synonymsView = getView().findViewById(R.id.synonyms);
        List<String> synonyms = plant.getSynonyms();
        if (synonyms != null) {
            StringBuilder synonymsText = new StringBuilder();
            for (String synonym : synonyms) {
                if (synonymsText.length() > 0) {
                    synonymsText.append(", ");
                }
                synonymsText.append(synonym);
            }
            if (synonymsText.length() > 0) {
                synonymsView.setText("(" + synonymsText.toString() + ")");
            } else {
                synonymsView.setVisibility(View.GONE);
            }
        } else {
            synonymsView.setVisibility(View.GONE);
        }
    }

    private void setAltNames(boolean all) {
        PlantTranslation plantTranslation = getPlantTranslation();
        TextView namesView = getView().findViewById(R.id.plant_alt_names);
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
                    if (!all && i > Constants.NAMES_TO_DISPLAY) {
                        if (names.size() > i) {
                           namesText.append("...");
                        }
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
        return displayPlantBaseActivity.getPlant();
    }

    private PlantTranslation getPlantTranslation() {
        return displayPlantBaseActivity.getPlantTranslation();
    }
}
