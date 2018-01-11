package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.entity.PlantTranslationParcel;
import sk.ab.herbsbase.fragments.PlantListFragment;
import sk.ab.herbsbase.tools.SynchronizedCounter;

/**
 * Activity for displaying list of plants according to filter
 *
 */
public abstract class ListPlantsBaseActivity extends BaseActivity {

    private final static int API_CALLS = 4;

    protected String listPath;

    private boolean fromNotification;
    private FirebasePlant plant;
    private PlantTranslation translationInLanguage;
    private PlantTranslation translationInLanguageGT;
    private PlantTranslation translationInEnglish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            fromNotification = savedInstanceState.getBoolean(AndroidConstants.STATE_FROM_NOTIFICATION);
            count = savedInstanceState.getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)savedInstanceState.getSerializable(AndroidConstants.STATE_FILTER);
            listPath = savedInstanceState.getString(AndroidConstants.STATE_LIST_PATH);
        } else {
            fromNotification = getIntent().getExtras().getBoolean(AndroidConstants.STATE_FROM_NOTIFICATION);
            count = getIntent().getExtras().getInt(AndroidConstants.STATE_PLANT_LIST_COUNT);
            filter = (HashMap<String, String>)getIntent().getExtras().getSerializable(AndroidConstants.STATE_FILTER);
            listPath = getIntent().getStringExtra(AndroidConstants.STATE_LIST_PATH);
        }

        setContentView(R.layout.list_activity);

        overlay = findViewById(R.id.overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startLoading();
                Intent intent = new Intent(ListPlantsBaseActivity.this, getFilterPlantActivityClass());
                intent.putExtra(AndroidConstants.STATE_FILTER_CLEAR, "true");
                intent.putExtra(AndroidConstants.STATE_FILTER, filter);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });

        countText = (TextView) findViewById(R.id.countText);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            ViewTreeObserver vto = mDrawerLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    mDrawerLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    stopLoading();
                    setCountButton();
                }
            });


            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.list_info, R.string.list_info) {
            };

            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        PlantListFragment plantListFragment = (PlantListFragment) fm.findFragmentByTag("PlantList");
        if (plantListFragment == null) {
            ft.replace(R.id.list_content, new PlantListFragment(), "PlantList");
        }
        mPropertyMenu = getMenuFragment();
        ft.replace(R.id.menu_content, mPropertyMenu);
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getSupportActionBar() != null) {
            if (fromNotification) {
                String taxonName = listPath.substring(0, listPath.lastIndexOf(AndroidConstants.FIREBASE_SEPARATOR));
                taxonName = taxonName.substring(taxonName.lastIndexOf(AndroidConstants.FIREBASE_SEPARATOR)+1);
                getSupportActionBar().setTitle(taxonName);
            } else {
                getSupportActionBar().setTitle(R.string.list_info);
            }
        }

        stopLoading();
        setCountButton();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(AndroidConstants.STATE_FROM_NOTIFICATION, fromNotification);
        savedInstanceState.putInt(AndroidConstants.STATE_PLANT_LIST_COUNT, count);
        savedInstanceState.putSerializable(AndroidConstants.STATE_FILTER, filter);
        savedInstanceState.putString(AndroidConstants.STATE_LIST_PATH, listPath);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (fromNotification) {
            Intent intent = new Intent(this, getFilterPlantActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public String getListPath() {
        return listPath;
    };

    public void selectPlant(final String plantName) {
        if (!getApp().isOffline() && !BaseApp.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
            return;
        }

        startLoading();

        final SynchronizedCounter counter = new SynchronizedCounter();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // load non-translatable attributes
        DatabaseReference mPlantRef = database.getReference(AndroidConstants.FIREBASE_PLANTS + AndroidConstants.FIREBASE_SEPARATOR + plantName);
        mPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebasePlant plant = dataSnapshot.getValue(FirebasePlant.class);

                setPlant(new PlantParcel(plant));

                counter.increment();

                if (counter.value() == API_CALLS) {
                    displayPlant();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
                setCountButton();
            }
        });

        // load translations in language
        DatabaseReference mTranslationRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.FIREBASE_SEPARATOR
                + Locale.getDefault().getLanguage());
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
                    displayPlant();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(this.getClass().getName(), databaseError.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                stopLoading();
                setCountButton();
            }
        });

        if (!AndroidConstants.LANGUAGE_EN.equals(Locale.getDefault().getLanguage()) && !AndroidConstants.LANGUAGE_SK.equals(Locale.getDefault().getLanguage())) {
            // load translation in English, for Czech in Slovak
            String baseLanguage = AndroidConstants.LANGUAGE_CS.equals(Locale.getDefault().getLanguage()) ? AndroidConstants.LANGUAGE_SK : AndroidConstants.LANGUAGE_EN;
            DatabaseReference mTranslationEnRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.FIREBASE_SEPARATOR
                    + baseLanguage + AndroidConstants.FIREBASE_SEPARATOR + plantName);
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
                        displayPlant();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(this.getClass().getName(), databaseError.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                    stopLoading();
                    setCountButton();
                }
            });

            // load translation in language (GT)
            DatabaseReference mTranslationGTRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS + AndroidConstants.FIREBASE_SEPARATOR
                    + Locale.getDefault().getLanguage() + AndroidConstants.LANGUAGE_GT_SUFFIX);

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
                        displayPlant();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(this.getClass().getName(), databaseError.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to load data. Check your internet settings.", Toast.LENGTH_SHORT).show();
                    stopLoading();
                    setCountButton();
                }
            });
        } else {
            setTranslationInEnglish(null);
            setTranslationInLanguageGT(null);

            counter.increment();
            counter.increment();

            if (counter.value() == API_CALLS) {
                displayPlant();
            }
        }
    }

    private void displayPlant() {
        Intent intent = getDisplayPlantActivityIntent();
        intent.putExtra(AndroidConstants.STATE_FROM_NOTIFICATION, false);
        intent.putExtra(AndroidConstants.STATE_PLANT, new PlantParcel(getPlant()));
        intent.putExtra(AndroidConstants.STATE_FILTER, filter);
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
        stopLoading();
        setCountButton();
    }

    protected abstract Intent getDisplayPlantActivityIntent();

    protected abstract Class getFilterPlantActivityClass();

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
}
