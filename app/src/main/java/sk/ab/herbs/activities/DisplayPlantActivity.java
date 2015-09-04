package sk.ab.herbs.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.commons.BaseActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.GalleryFragment;
import sk.ab.herbs.fragments.InfoFragment;
import sk.ab.herbs.fragments.SourcesFragment;
import sk.ab.herbs.fragments.TaxonomyFragment;
import sk.ab.tools.Keys;
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

    private Plant plant;
    private boolean isOriginal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isOriginal = true;

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
                ((HerbsApp) getApplication()).getFilter().clear();
                Intent intent = new Intent(DisplayPlantActivity.this, FilterPlantsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    public void getTranslation(View v) {
        if (isOriginal) {
            int language = Constants.getLanguage(Locale.getDefault().getLanguage());
            if (!plant.isTranslated(language)) {

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
                    getTranslation(Constants.LANGUAGE_EN, Locale.getDefault().getLanguage(), textWithLanguages);
                } else {
                    InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
                    infoFragment.setInfo(plant, language);
                }
            } else {
                InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
                infoFragment.setInfo(plant, language);
            }
        } else {
            InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
            infoFragment.setInfo(plant, Constants.ORIGINAL_LANGUAGE);
        }
        isOriginal = !isOriginal;
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
        }

        InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
        infoFragment.setInfo(plant, language);
    }

    public Plant getPlant() {
        return plant;
    }

    private void getTranslation(String source, String target, List<TextWithLanguage> textWithLanguages) {
        int language = Constants.getLanguage(source);
        List<String> qs = new ArrayList<>();
        for (TextWithLanguage textWithLanguage : textWithLanguages) {
            qs.add(textWithLanguage.getText(language));
        }

        ((HerbsApp)getApplication()).getGoogleClient().getApiService().translate(
                Keys.TRANSLATE_API_KEY,
                source,
                target,
                qs).enqueue(new Callback<Map<String, Map<String, List<Map<String, String>>>>>() {
            @Override
            public void onResponse(Response<Map<String, Map<String, List<Map<String, String>>>>> response) {
                Map<String, Map<String, List<Map<String, String>>>> data = response.body();

                List<String> translatedTexts = new ArrayList<>();
                List<Map<String, String>> texts = data.get("data").get("translations");
                for (Map<String, String> text : texts) {
                    translatedTexts.add(text.get("translatedText"));
                }

                setTranslation(translatedTexts);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });
    }
}
