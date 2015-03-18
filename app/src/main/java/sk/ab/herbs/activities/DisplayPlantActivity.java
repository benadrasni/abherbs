package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import sk.ab.commons.BaseActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.GalleryFragment;
import sk.ab.herbs.fragments.InfoFragment;
import sk.ab.herbs.fragments.SourcesFragment;
import sk.ab.herbs.fragments.TaxonomyFragment;
import sk.ab.herbs.fragments.rest.TranslateResponderFragment;
import sk.ab.tools.TextWithLanguage;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for displaying selected plant
 */
public class DisplayPlantActivity extends BaseActivity {
    static final String STATE_PLANT = "plant";

    private TranslateResponderFragment translateResponder;
    private Plant plant;
    private String sLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        sLanguage = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);

        if (savedInstanceState != null) {
            plant = savedInstanceState.getParcelable(STATE_PLANT);
        } else {
            plant = getIntent().getExtras().getParcelable("plant");
        }

        setContentView(R.layout.plant_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        translateResponder = (TranslateResponderFragment) fm.findFragmentByTag("RESTDetailResponder");
        if (translateResponder == null) {
            translateResponder = new TranslateResponderFragment();
            ft.add(translateResponder, "RESTTranslateResponder");
        }

        TaxonomyFragment taxonomyFragment = (TaxonomyFragment) fm.findFragmentByTag("Taxonomy");
        if (taxonomyFragment == null) {
            ft.replace(R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
            ft.replace(R.id.info_fragment, new InfoFragment(), "Info");
            ft.replace(R.id.gallery_fragment, new GalleryFragment(), "Gallery");
            ft.replace(R.id.sources_fragment, new SourcesFragment(), "Sources");
        }
        ft.commit();

        getSupportActionBar().setTitle(R.string.display_info);
    }

    @Override
    public void onStart() {
        super.onStart();

        ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        scrollview.scrollTo(0, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(STATE_PLANT, plant);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        countButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading();
                ((HerbsApp)getApplication()).getFilter().clear();
                Intent intent = new Intent(DisplayPlantActivity.this, FilterPlantsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    public void getTranslation(View v) {
        if (!sLanguage.equals(Locale.getDefault().getLanguage())) {
            int language = Constants.getLanguage(Locale.getDefault().getLanguage());

            List<TextWithLanguage> textWithLanguages = new ArrayList<>();

            if (!plant.getDescription().isText(language)) {
                textWithLanguages.add(plant.getDescription());
            }
            if (!plant.getFlower().isText(language)) {
                textWithLanguages.add(plant.getFlower());
            }
            if (!plant.getInflorescence().isText(language)) {
                textWithLanguages.add(plant.getInflorescence());
            }
            if (!plant.getFruit().isText(language)) {
                textWithLanguages.add(plant.getFruit());
            }
            if (!plant.getLeaf().isText(language)) {
                textWithLanguages.add(plant.getLeaf());
            }
            if (!plant.getStem().isText(language)) {
                textWithLanguages.add(plant.getStem());
            }
            if (!plant.getHabitat().isText(language)) {
                textWithLanguages.add(plant.getHabitat());
            }

            if (textWithLanguages.size() > 0) {
                translateResponder.getTranslation(sLanguage, Locale.getDefault().getLanguage(), textWithLanguages);
            } else {
                InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
                infoFragment.setInfo(plant, language);
                sLanguage = Locale.getDefault().getLanguage();
            }
        } else {
            SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
            String prefLanguage = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);

            if (!sLanguage.equals(prefLanguage)) {
                InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
                infoFragment.setInfo(plant, Constants.getLanguage(prefLanguage));
                sLanguage = prefLanguage;
            }
        }
    }

    public void setTranslation(List<String> translatedTexts) {
        int language = Constants.getLanguage(Locale.getDefault().getLanguage());

        int i = 0;
        if (translatedTexts.size() > i && !plant.getDescription().isText(language)) {
            plant.getDescription().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getFlower().isText(language)) {
            plant.getFlower().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getInflorescence().isText(language)) {
            plant.getInflorescence().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getFruit().isText(language)) {
            plant.getFruit().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getLeaf().isText(language)) {
            plant.getLeaf().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getStem().isText(language)) {
            plant.getStem().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getHabitat().isText(language)) {
            plant.getHabitat().add(language, translatedTexts.get(i));
            i++;
        }

        InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
        infoFragment.setInfo(plant, language);
        sLanguage = Locale.getDefault().getLanguage();
    }

    public Plant getPlant() {
        return plant;
    }
}
