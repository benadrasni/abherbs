package sk.ab.herbs.backend.util;

import com.google.appengine.api.datastore.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.herbs.backend.Constants;
import sk.ab.herbs.backend.entity.DetailRequest;
import sk.ab.herbs.backend.entity.Plant;
import sk.ab.herbs.backend.service.HerbClient;
import sk.ab.herbs.backend.service.HerbCloudClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Synchronizer {

    public static void main(String[] params) {

        File file = new File("C:/Development/projects/abherbs/backend/Plants.csv");

        try {
            Scanner scan = new Scanner(file);

            final HerbCloudClient herbCloudClient = new HerbCloudClient();
            final HerbClient herbClient = new HerbClient();

            while(scan.hasNextLine()){
                final String[] name = scan.nextLine().split(",");
                final Plant plant = new Plant();

                herbClient.getApiService().getDetail(
                        new DetailRequest(0, Integer.parseInt(name[2]))).enqueue(new Callback<Map<Integer, Map<String, List<String>>>>() {
                    @Override
                    public void onResponse(Response<Map<Integer,Map<String,List<String>>>> response) {

                        for (Map.Entry<Integer,Map<String,List<String>>> entry : response.body().entrySet()) {

                            Map<String,List<String>> attributes = entry.getValue();

                            int rank = 0;
                            List<String> colors = new ArrayList<>();
                            while (attributes.containsKey("" + Constants.PLANT_FILTER_COLOR + "_" + rank)) {
                                List<String> values = attributes.get(""+Constants.PLANT_FILTER_COLOR+"_"+rank);
                                colors.add(values.get(0));
                                rank++;
                            }
                            plant.setFilterColor(colors);


//                    String name = attributes.get(""+ Constants.PLANT_NAME+"_0").get(0);
//                    result.setTitle(name);
//                    result.setSpecies(name);
//
//                    if (attributes.containsKey(""+Constants.PLANT_FAMILY +"_0")) {
//                        result.setFamily(attributes.get("" + Constants.PLANT_FAMILY + "_0").get(0));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_IMAGE_URL +"_0")) {
//                        result.setBack_url(attributes.get("" + Constants.PLANT_IMAGE_URL + "_0").get(0));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_TOXICITY_CLASS +"_0")) {
//                        result.setToxicity_class(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_TOXICITY_CLASS + "_0").get(0))));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_FROM +"_0")) {
//                        result.setHeightFrom(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_FROM + "_0").get(0))));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_TO +"_0")) {
//                        result.setHeightTo(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_TO + "_0").get(0))));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_FROM +"_0")) {
//                        result.setFloweringFrom(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_FROM + "_0").get(0))));
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_TO +"_0")) {
//                        result.setFloweringTo(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_TO + "_0").get(0))));
//                    }
//
//                    if (attributes.containsKey(""+Constants.PLANT_DESCRIPTION +"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(0));
//                        result.setDescription(texts);
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_FLOWER + "_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_FLOWER + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_FLOWER + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_FLOWER + "_0").get(0));
//                        result.setFlower(texts);
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_INFLORESCENCE + "_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(0));
//                        result.setInflorescence(texts);
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_FRUIT + "_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_FRUIT + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_FRUIT + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_FRUIT + "_0").get(0));
//                        result.setFruit(texts);
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_STEM+"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_STEM + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_STEM + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_STEM + "_0").get(0));
//                        result.setStem(texts);
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_LEAF + "_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_LEAF + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_LEAF + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_LEAF + "_0").get(0));
//                        result.setLeaf(texts);
//                    }
//                    if (attributes.containsKey(""+Constants.PLANT_HABITAT+"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_HABITAT + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_HABITAT + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_HABITAT + "_0").get(0));
//                        result.setHabitat(texts);
//                    }
//                    result.setTrivia(new TextWithLanguage());
//                    if (attributes.containsKey(""+Constants.PLANT_TRIVIA+"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(0));
//                        result.setTrivia(texts);
//                    }
//                    result.setToxicity(new TextWithLanguage());
//                    if (attributes.containsKey(""+Constants.PLANT_TOXICITY+"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(0));
//                        result.setToxicity(texts);
//                    }
//                    result.setHerbalism(new TextWithLanguage());
//                    if (attributes.containsKey(""+Constants.PLANT_HERBALISM+"_0")) {
//                        TextWithLanguage texts = new TextWithLanguage();
//                        texts.add(Constants.getLanguage(Integer.parseInt(attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(2))),
//                                attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(0));
//                        texts.add(Constants.ORIGINAL_LANGUAGE,
//                                attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(0));
//                        result.setHerbalism(texts);
//                    }
//
//                    if (attributes.containsKey("" + Constants.PLANT_SPECIES_LATIN + "_0")) {
//                        result.setSpecies_latin(attributes.get(""+Constants.PLANT_SPECIES_LATIN+"_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_DOMAIN + "_0")) {
//                        result.setDomain(attributes.get(""+Constants.PLANT_DOMAIN+"_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_KINGDOM + "_0")) {
//                        result.setKingdom(attributes.get(""+Constants.PLANT_KINGDOM+"_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_SUBKINGDOM + "_0")) {
//                        result.setSubkingdom(attributes.get("" + Constants.PLANT_SUBKINGDOM + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_LINE + "_0")) {
//                        result.setLine(attributes.get("" + Constants.PLANT_LINE + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_BRANCH + "_0")) {
//                        result.setBranch(attributes.get("" + Constants.PLANT_BRANCH + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_PHYLUM + "_0")) {
//                        result.setPhylum(attributes.get("" + Constants.PLANT_PHYLUM + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_CLS + "_0")) {
//                        result.setCls(attributes.get("" + Constants.PLANT_CLS + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_ORDER + "_0")) {
//                        result.setOrder(attributes.get("" + Constants.PLANT_ORDER + "_0").get(0));
//                    }
//                    if (attributes.containsKey("" + Constants.PLANT_GENUS + "_0")) {
//                        result.setGenus(attributes.get("" + Constants.PLANT_GENUS + "_0").get(0));
//                    }
//
//                    SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
//                    String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
//
//                    int rank = 0;
//                    List<String> names = new ArrayList<>();
//                    while (attributes.containsKey("" + Constants.PLANT_ALT_NAMES + "_" + rank)) {
//                        List<String> values = attributes.get(""+Constants.PLANT_ALT_NAMES+"_"+rank);
//                        if (Integer.parseInt(values.get(2)) == Constants.getLanguage(language)) {
//                            names.add(values.get(0));
//                        }
//                        rank++;
//                    }
//                    result.setNames(names);
//
//                    rank = 0;
//                    List<String> photo_urls = new ArrayList<>();
//                    while (attributes.containsKey("" + Constants.PLANT_PHOTO_URL + "_" + rank)) {
//                        photo_urls.add(attributes.get(""+Constants.PLANT_PHOTO_URL+"_"+rank).get(0));
//                        rank++;
//                    }
//                    result.setPhotoUrls(photo_urls);
//
//                    rank = 0;
//                    List<String> source_urls = new ArrayList<>();
//                    while (attributes.containsKey(""+Constants.PLANT_SOURCE_URL+"_"+rank)) {
//                        source_urls.add(attributes.get(""+Constants.PLANT_SOURCE_URL+"_"+rank).get(0));
//                        rank++;
//                    }
//                    result.setSourceUrls(source_urls);


                        }

                        herbCloudClient.getApiService().synchronizePlant(name[0], name[1], plant)
                                .enqueue(new Callback<Plant>() {
                                    @Override
                                    public void onResponse(Response<Plant> response) {

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {

                                    }
                                });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.print(t);
                    }
                });

                break;
            }
            scan.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }

    private void getPlantFromResource(final Entity plantEntity, final Integer plantId) {
        final HerbClient herbClient = new HerbClient();
    }

}
