package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.commons.BaseActivity;
import sk.ab.herbs.Constants;
import sk.ab.herbs.DetailRequest;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.Plant;
import sk.ab.herbs.PlantHeader;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantListFragment;
import sk.ab.tools.TextWithLanguage;

/**
 * User: adrian
 * Date: 10.1.2015
 * <p/>
 * Activity for displaying list of plants according to filter
 */
public class ListPlantsActivity extends BaseActivity {
    private List<PlantHeader> plants;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plants = getIntent().getExtras().getParcelableArrayList("results");

        ((HerbsApp)getApplication()).getTracker().send(builder.build());

        setContentView(R.layout.list_activity);

        countButton = (FloatingActionButton) findViewById(R.id.countButton);
        countButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                loading();
                ((HerbsApp) getApplication()).getFilter().clear();
                Intent intent = new Intent(ListPlantsActivity.this, FilterPlantsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.list_info, R.string.list_info) {
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        PlantListFragment plantListFragment = (PlantListFragment) fm.findFragmentByTag("PlantList");
        if (plantListFragment == null) {
            ft.replace(R.id.list_content, new PlantListFragment(), "PlantList");
        }
        ft.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.list_info);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setCountButton();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        recreate();
    }

    public void selectPlant(int position) {
        loading();
        getDetail(plants.get(position).getPlantId());
    }

    public void setPlant(Plant plant) {
        Intent intent = new Intent(getBaseContext(), DisplayPlantActivity.class);
        intent.putExtra("plant", plant);
        startActivity(intent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ((HerbsApp)getApplication()).setLoading(false);
    }

    public List<PlantHeader> getPlants() {
        return plants;
    }

    private void getDetail(int plantId) {
        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        ((HerbsApp)getApplication()).getHerbClient().getApiService().getDetail(
                new DetailRequest(Constants.getLanguage(language),
                        plantId)).enqueue(new Callback<Map<Integer, Map<String, List<String>>>>() {
            @Override
            public void onResponse(Response<Map<Integer,Map<String,List<String>>>> response) {
                Plant result = null;

                for (Map.Entry<Integer,Map<String,List<String>>> entry : response.body().entrySet()) {
                    result = new Plant(entry.getKey());

                    Map<String,List<String>> attributes = entry.getValue();
                    String name = attributes.get(""+Constants.PLANT_NAME+"_0").get(0);
                    result.setTitle(name);
                    result.setSpecies(name);

                    if (attributes.containsKey(""+Constants.PLANT_FAMILY +"_0")) {
                        result.setFamily(attributes.get("" + Constants.PLANT_FAMILY + "_0").get(0));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_IMAGE_URL +"_0")) {
                        result.setBack_url(attributes.get("" + Constants.PLANT_IMAGE_URL + "_0").get(0));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_TOXICITY_CLASS +"_0")) {
                        result.setToxicity_class(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_TOXICITY_CLASS + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_FROM +"_0")) {
                        result.setHeight_from(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_FROM + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_TO +"_0")) {
                        result.setHeight_to(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_TO + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_FROM +"_0")) {
                        result.setFlowering_from(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_FROM + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_TO +"_0")) {
                        result.setFlowering_to(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_TO + "_0").get(0))));
                    }

                    if (attributes.containsKey(""+Constants.PLANT_DESCRIPTION +"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(0));
                        result.setDescription(texts);
                    }
                    if (attributes.containsKey("" + Constants.PLANT_FLOWER + "_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_FLOWER + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_FLOWER + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_FLOWER + "_0").get(0));
                        result.setFlower(texts);
                    }
                    if (attributes.containsKey("" + Constants.PLANT_INFLORESCENCE + "_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(0));
                        result.setInflorescence(texts);
                    }
                    if (attributes.containsKey("" + Constants.PLANT_FRUIT + "_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_FRUIT + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_FRUIT + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_FRUIT + "_0").get(0));
                        result.setFruit(texts);
                    }
                    if (attributes.containsKey("" + Constants.PLANT_STEM+"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_STEM + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_STEM + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_STEM + "_0").get(0));
                        result.setStem(texts);
                    }
                    if (attributes.containsKey("" + Constants.PLANT_LEAF + "_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_LEAF + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_LEAF + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_LEAF + "_0").get(0));
                        result.setLeaf(texts);
                    }
                    if (attributes.containsKey(""+Constants.PLANT_HABITAT+"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_HABITAT + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_HABITAT + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_HABITAT + "_0").get(0));
                        result.setHabitat(texts);
                    }
                    result.setTrivia(new TextWithLanguage());
                    if (attributes.containsKey(""+Constants.PLANT_TRIVIA+"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(0));
                        result.setTrivia(texts);
                    }
                    result.setToxicity(new TextWithLanguage());
                    if (attributes.containsKey(""+Constants.PLANT_TOXICITY+"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(0));
                        result.setToxicity(texts);
                    }
                    result.setHerbalism(new TextWithLanguage());
                    if (attributes.containsKey(""+Constants.PLANT_HERBALISM+"_0")) {
                        TextWithLanguage texts = new TextWithLanguage();
                        texts.add(Integer.parseInt(attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(2)),
                                attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(0));
                        texts.add(Constants.ORIGINAL_LANGUAGE,
                                attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(0));
                        result.setHerbalism(texts);
                    }

                    if (attributes.containsKey("" + Constants.PLANT_SPECIES_LATIN + "_0")) {
                        result.setSpecies_latin(attributes.get(""+Constants.PLANT_SPECIES_LATIN+"_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_DOMAIN + "_0")) {
                        result.setDomain(attributes.get(""+Constants.PLANT_DOMAIN+"_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_KINGDOM + "_0")) {
                        result.setKingdom(attributes.get(""+Constants.PLANT_KINGDOM+"_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_SUBKINGDOM + "_0")) {
                        result.setSubkingdom(attributes.get("" + Constants.PLANT_SUBKINGDOM + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_LINE + "_0")) {
                        result.setLine(attributes.get("" + Constants.PLANT_LINE + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_BRANCH + "_0")) {
                        result.setBranch(attributes.get("" + Constants.PLANT_BRANCH + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_PHYLUM + "_0")) {
                        result.setPhylum(attributes.get("" + Constants.PLANT_PHYLUM + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_CLS + "_0")) {
                        result.setCls(attributes.get("" + Constants.PLANT_CLS + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_ORDER + "_0")) {
                        result.setOrder(attributes.get("" + Constants.PLANT_ORDER + "_0").get(0));
                    }
                    if (attributes.containsKey("" + Constants.PLANT_GENUS + "_0")) {
                        result.setGenus(attributes.get("" + Constants.PLANT_GENUS + "_0").get(0));
                    }

                    SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
                    String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

                    int rank = 0;
                    List<String> names = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_ALT_NAMES + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_ALT_NAMES+"_"+rank);
                        if (Integer.parseInt(values.get(2)) == Constants.getLanguage(language)) {
                            names.add(values.get(0));
                        }
                        rank++;
                    }
                    result.setNames(names);

                    rank = 0;
                    List<String> photo_urls = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_PHOTO_URL + "_" + rank)) {
                        photo_urls.add(attributes.get(""+Constants.PLANT_PHOTO_URL+"_"+rank).get(0));
                        rank++;
                    }
                    result.setPhoto_urls(photo_urls);

                    rank = 0;
                    List<String> source_urls = new ArrayList<>();
                    while (attributes.containsKey(""+Constants.PLANT_SOURCE_URL+"_"+rank)) {
                        source_urls.add(attributes.get(""+Constants.PLANT_SOURCE_URL+"_"+rank).get(0));
                        rank++;
                    }
                    result.setSource_urls(source_urls);


                }

                setPlant(result);
            }

            @Override
            public void onFailure(Throwable t) {
                    Log.e(this.getClass().getName(), "Failed to load data. Check your internet settings.", t);
            }
        });
    }
}
