package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbs.billingmodule.BasePlayActivity;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.entity.PlantTranslationParcel;
import sk.ab.herbsbase.tools.SynchronizedCounter;
import sk.ab.herbsbase.tools.Utils;

/**
 *
 * Created by adrian on 23. 3. 2017.
 */

public abstract class SearchBaseActivity extends BasePlayActivity {
    private final static int API_CALLS = 4;

    private FirebasePlant plant;
    private PlantTranslation translationInLanguage;
    private PlantTranslation translationInLanguageGT;
    private PlantTranslation translationInEnglish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeLocale();
    }

    public void callListActivity(String listPath, int count, boolean fromNotification) {
        Intent intent = new Intent(getBaseContext(), getListPlantsActivityClass());
        intent.putExtra(AndroidConstants.STATE_FROM_NOTIFICATION, fromNotification);
        intent.putExtra(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        intent.putExtra(AndroidConstants.STATE_LIST_PATH, listPath);
        startActivity(intent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void callDetailActivity(final String plantName, final boolean fromNotification) {
        if (!((BaseApp)getApplication()).isOffline() && !BaseApp.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        final SynchronizedCounter counter = new SynchronizedCounter();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // load non-translatable attributes
        DatabaseReference mPlantRef = database.getReference(AndroidConstants.FIREBASE_PLANTS + AndroidConstants.SEPARATOR + plantName);
        mPlantRef.keepSynced(true);
        mPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebasePlant plant = dataSnapshot.getValue(FirebasePlant.class);

                setPlant(new PlantParcel(plant));

                counter.increment();

                if (counter.value() == API_CALLS) {
                    displayPlant(fromNotification);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        });

        // load translations in language
        DatabaseReference mTranslationRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.SEPARATOR
                + Locale.getDefault().getLanguage());
        mTranslationRef.keepSynced(true);
        mTranslationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(plantName)) {
                    PlantTranslation plantTranslation = dataSnapshot.child(plantName).getValue(PlantTranslation.class);

                    if (plantTranslation != null) {
                        setTranslationInLanguage(new PlantTranslationParcel(plantTranslation));
                    } else {
                        setTranslationInLanguage(null);
                    }
                } else {
                    setTranslationInLanguage(null);
                }

                counter.increment();

                if (counter.value() == API_CALLS) {
                    displayPlant(fromNotification);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            }
        });

        if (!AndroidConstants.LANGUAGE_EN.equals(Locale.getDefault().getLanguage()) && !AndroidConstants.LANGUAGE_SK.equals(Locale.getDefault().getLanguage())) {
            // load translation in English, for Czech in Slovak
            String baseLanguage = AndroidConstants.LANGUAGE_CS.equals(Locale.getDefault().getLanguage()) ? AndroidConstants.LANGUAGE_SK : AndroidConstants.LANGUAGE_EN;
            DatabaseReference mTranslationEnRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.SEPARATOR
                    + baseLanguage + AndroidConstants.SEPARATOR + plantName);
            mTranslationEnRef.keepSynced(true);
            mTranslationEnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    PlantTranslation plantTranslation = dataSnapshot.getValue(PlantTranslation.class);

                    if (plantTranslation != null) {
                        setTranslationInEnglish(new PlantTranslationParcel(plantTranslation));
                    } else {
                        setTranslationInEnglish(null);
                    }

                    counter.increment();

                    if (counter.value() == API_CALLS) {
                        displayPlant(fromNotification);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(this.getClass().getName(), databaseError.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                }
            });

            // load translation in language (GT)
            DatabaseReference mTranslationGTRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.SEPARATOR
                    + Locale.getDefault().getLanguage() + AndroidConstants.LANGUAGE_GT_SUFFIX);
            mTranslationGTRef.keepSynced(true);
            mTranslationGTRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(plantName)) {
                        PlantTranslation plantTranslation = dataSnapshot.child(plantName).getValue(PlantTranslation.class);

                        if (plantTranslation != null) {
                            setTranslationInLanguageGT(new PlantTranslationParcel(plantTranslation));
                        } else {
                            setTranslationInLanguageGT(null);
                        }
                    } else {
                        setTranslationInLanguageGT(null);
                    }

                    counter.increment();

                    if (counter.value() == API_CALLS) {
                        displayPlant(fromNotification);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(this.getClass().getName(), databaseError.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            setTranslationInEnglish(null);
            setTranslationInLanguageGT(null);

            counter.increment();
            counter.increment();

            if (counter.value() == API_CALLS) {
                displayPlant(fromNotification);
            }
        }
    }

    private void displayPlant(boolean fromNotification) {
        Intent intent = new Intent(getBaseContext(), getDisplayPlantActivityClass());
        intent.putExtra(AndroidConstants.STATE_FROM_NOTIFICATION, fromNotification);
        intent.putExtra(AndroidConstants.STATE_PLANT, new PlantParcel(getPlant()));
        if (getTranslationInLanguage() != null) {
            intent.putExtra(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE, new PlantTranslationParcel(getTranslationInLanguage()));
        }
        if (getTranslationInEnglish() != null) {
            intent.putExtra(AndroidConstants.STATE_TRANSLATION_IN_ENGLISH, new PlantTranslationParcel(getTranslationInEnglish()));
        }
        if (getTranslationInLanguageGT() != null) {
            intent.putExtra(AndroidConstants.STATE_TRANSLATION_IN_LANGUAGE_GT, new PlantTranslationParcel(getTranslationInLanguageGT()));
        }
        startActivity(intent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public FirebasePlant getPlant() {
        return plant;
    }

    public void setPlant(FirebasePlant plant) {
        this.plant = plant;
    }

    public PlantTranslation getTranslationInLanguage() {
        return translationInLanguage;
    }

    public void setTranslationInLanguage(PlantTranslation translationInLanguage) {
        this.translationInLanguage = translationInLanguage;
    }

    public PlantTranslation getTranslationInLanguageGT() {
        return translationInLanguageGT;
    }

    public void setTranslationInLanguageGT(PlantTranslation translationInLanguageGT) {
        this.translationInLanguageGT = translationInLanguageGT;
    }

    public PlantTranslation getTranslationInEnglish() {
        return translationInEnglish;
    }

    public void setTranslationInEnglish(PlantTranslation translationInEnglish) {
        this.translationInEnglish = translationInEnglish;
    }

    protected void changeLocale() {
        SharedPreferences preferences = getSharedPreferences();
        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        if (!Locale.getDefault().getLanguage().equals(language)) {
            Utils.changeLocale(getBaseContext(), language);
        }
    }

    protected abstract Class getFilterPlantsActivityClass();

    protected abstract Class getListPlantsActivityClass();

    protected abstract Class getDisplayPlantActivityClass();

    public abstract SharedPreferences getSharedPreferences();
}
