package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.herbs.TranslationSaveRequest;
import sk.ab.herbs.commons.BaseActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.GalleryFragment;
import sk.ab.herbs.fragments.InfoFragment;
import sk.ab.herbs.fragments.SourcesFragment;
import sk.ab.herbs.fragments.TaxonomyFragment;
import sk.ab.herbs.service.HerbCloudClient;
import sk.ab.herbs.tools.Keys;
import sk.ab.herbs.tools.TextWithLanguage;

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
    private int language;
    private boolean isTranslated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            plant = savedInstanceState.getParcelable(STATE_PLANT);
        } else {
            plant = getIntent().getExtras().getParcelable("plant");
        }

        builder.set("plant", ""+getPlant().getPlantId());
        ((HerbsApp)getApplication()).getTracker().send(builder.build());

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String sLanguage = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        language = Constants.ORIGINAL_LANGUAGE;
        isTranslated = getPlant().isTranslated(Constants.getLanguage(sLanguage));

        setContentView(R.layout.plant_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.plant_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.display_info, R.string.display_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.display_info);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        if (scrollview != null) {
            scrollview.scrollTo(0, 0);
        }
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

    public void getTranslation() {
        if (language == Constants.ORIGINAL_LANGUAGE) {
            language = Constants.getLanguage(Locale.getDefault().getLanguage());
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
                if (!plant.getTrivia().isText(language)) {
                    textWithLanguages.add(plant.getTrivia());
                }
                if (!plant.getToxicity().isText(language)) {
                    textWithLanguages.add(plant.getToxicity());
                }
                if (!plant.getHerbalism().isText(language)) {
                    textWithLanguages.add(plant.getHerbalism());
                }

                if (textWithLanguages.size() > 0) {
                    getTranslation(Constants.LANGUAGE_EN, Locale.getDefault().getLanguage(), textWithLanguages);
                } else {
                    setInfo();
                }
            } else {
                setInfo();
            }
        } else {
            language = Constants.ORIGINAL_LANGUAGE;
            setInfo();
        }
    }

    public void proposeTranslation() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constants.EMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getEmailSubject());
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getEmailBody()));
        startActivity(Intent.createChooser(emailIntent, getEmailSubject()));
    }

    public Plant getPlant() {
        return plant;
    }

    public int getLanguage() {
        return language;
    }

    public boolean isTranslated() {
        return isTranslated;
    }


    private void setInfo() {
        InfoFragment infoFragment = (InfoFragment) getSupportFragmentManager().findFragmentByTag("Info");
        infoFragment.setInfo(plant, language);
    }

    private String getEmailSubject() {
        return getString(R.string.email_subject_prefix) + plant.getSpecies_latin();
    }

    private String getEmailBody() {
        int language = Constants.getLanguage(Locale.getDefault().getLanguage());

        final StringBuilder text = new StringBuilder();

        text.append(plant.getSpecies());
        text.append("<br/>");
        text.append(plant.getNames());
        text.append("<br/><br/>");
        if (language > 1) {
            text.append(Locale.ENGLISH.getDisplayLanguage());
            text.append("<br/><br/>");
            text.append(getPlantInLanguage(0));
            text.append("<br/><br/>");
        }
        text.append(Locale.getDefault().getDisplayLanguage());
        text.append("<br/><br/>");
        text.append(getPlantInLanguage(language));

        return text.toString();
    }

    private String getPlantInLanguage(int language) {
        String[][] sections = { {"", plant.getDescription().getText(language)},
                {getString(R.string.plant_flowers), plant.getFlower().getText(language)},
                {getString(R.string.plant_inflorescences), plant.getInflorescence().getText(language)},
                {getString(R.string.plant_fruits), plant.getFruit().getText(language)},
                {getString(R.string.plant_leaves), plant.getLeaf().getText(language)},
                {getString(R.string.plant_stem), plant.getStem().getText(language)},
                {getString(R.string.plant_habitat), plant.getHabitat().getText(language)},
                {getString(R.string.plant_trivia), plant.getTrivia().getText(language)},
                {getString(R.string.plant_toxicity), plant.getToxicity().getText(language)},
                {getString(R.string.plant_herbalism), plant.getHerbalism().getText(language)}
        };

        final StringBuilder text = new StringBuilder(plant.getSpecies());
        for(int i = 0; i < sections.length; i++ ) {
            text.append("<b>" + sections[i][0] + "</b>");
            text.append(": ");
            text.append(sections[i][1]);
            text.append(" ");
            text.append("<br/>");
        }

        return text.toString();
    }

    private void getTranslation(String source, final String target, List<TextWithLanguage> textWithLanguages) {
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
                setInfo();


                HerbCloudClient herbCloudClient = new HerbCloudClient();
                TranslationSaveRequest translationSaveRequest = new TranslationSaveRequest(plant.getPlantId(),
                        Constants.getLanguage(target), translatedTexts);

                herbCloudClient.getApiService().saveTranslation(translationSaveRequest).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Response<Boolean> response) {
                        if (response != null) {
                            Toast.makeText(getApplicationContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
                    }
                });

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });
    }

    private void setTranslation(List<String> translatedTexts) {
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
        if (translatedTexts.size() > i && !plant.getTrivia().isText(language)) {
            plant.getTrivia().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getToxicity().isText(language)) {
            plant.getToxicity().add(language, translatedTexts.get(i));
            i++;
        }
        if (translatedTexts.size() > i && !plant.getHerbalism().isText(language)) {
            plant.getHerbalism().add(language, translatedTexts.get(i));
        }
    }

}
