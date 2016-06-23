package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit.Call;
import sk.ab.herbs.backend.Constants;
import sk.ab.herbs.backend.entity.DetailRequest;
import sk.ab.herbs.backend.entity.Plant;
import sk.ab.herbs.backend.service.HerbClient;
import sk.ab.herbs.backend.service.HerbCloudClient;

/**
 *
 * Created by adrian on 4.5.2016.
 */
public class Synchronizer {

    public static void main(String[] params) {

        File file = new File("C:/Development/projects/abherbs/backend/Plants.csv");

        try {
            Scanner scan = new Scanner(file);

            final HerbCloudClient herbCloudClient = new HerbCloudClient();
            final HerbClient herbClient = new HerbClient();

            int i = 0;
            while(scan.hasNextLine()){
                i++;
                final String[] name = scan.nextLine().split(",");
                final Plant plant = new Plant();
                plant.setPlantId(Integer.parseInt(name[2]));
                plant.setName(name[0]);
                plant.setWikiName(name[1]);

                Call<Map<Integer,Map<String,List<String>>>> callSqlCloud = herbClient.getApiService().getDetail(
                        new DetailRequest(0, plant.getPlantId()));

                Map<Integer,Map<String,List<String>>> response = callSqlCloud.execute().body();

                for (Map.Entry<Integer,Map<String,List<String>>> entry : response.entrySet()) {

                    Map<String,List<String>> attributes = entry.getValue();

                    int rank = 0;
                    List<String> colors = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_COLOR + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_COLOR+"_"+rank);
                        colors.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterColor(colors);

                    rank = 0;
                    List<String> habitats = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_HABITAT + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_HABITAT+"_"+rank);
                        habitats.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterHabitat(habitats);

                    rank = 0;
                    List<String> petals = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_PETALS + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_PETALS+"_"+rank);
                        petals.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterPetal(petals);

                    rank = 0;
                    List<String> inflorences = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_INFLORESCENCE + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_INFLORESCENCE+"_"+rank);
                        inflorences.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterInflorescence(inflorences);

                    rank = 0;
                    List<String> sepals = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_SEPAL + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_SEPAL+"_"+rank);
                        sepals.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterSepal(sepals);

                    rank = 0;
                    List<String> stems = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_STEM + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_STEM+"_"+rank);
                        stems.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterStem(stems);

                    rank = 0;
                    List<String> leafShapes = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_LEAF_SHAPE + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_LEAF_SHAPE+"_"+rank);
                        leafShapes.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterLeafShape(sepals);

                    rank = 0;
                    List<String> leafMargin = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_LEAF_MARGIN + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_LEAF_MARGIN+"_"+rank);
                        leafMargin.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterLeafMargin(leafMargin);

                    rank = 0;
                    List<String> leafVenetation = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_LEAF_VENETATION + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_LEAF_VENETATION+"_"+rank);
                        leafVenetation.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterLeafVenetation(leafVenetation);

                    rank = 0;
                    List<String> leafArrangement = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_LEAF_ARRANGEMENT + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_LEAF_ARRANGEMENT+"_"+rank);
                        leafArrangement.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterLeafArrangement(leafArrangement);

                    rank = 0;
                    List<String> roots = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_FILTER_ROOT + "_" + rank)) {
                        List<String> values = attributes.get(""+Constants.PLANT_FILTER_ROOT+"_"+rank);
                        roots.add(values.get(0));
                        rank++;
                    }
                    plant.setFilterRoot(roots);

                    if (attributes.containsKey(""+Constants.PLANT_IMAGE_URL +"_0")) {
                        plant.setIllustrationUrl(attributes.get("" + Constants.PLANT_IMAGE_URL + "_0").get(0));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_TOXICITY_CLASS +"_0")) {
                        plant.setToxicityClass(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_TOXICITY_CLASS + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_FROM +"_0")) {
                        plant.setHeightFrom(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_FROM + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_HEIGHT_TO +"_0")) {
                        plant.setHeightTo(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_HEIGHT_TO + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_FROM +"_0")) {
                        plant.setFloweringFrom(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_FROM + "_0").get(0))));
                    }
                    if (attributes.containsKey(""+Constants.PLANT_FLOWERING_TO +"_0")) {
                        plant.setFloweringTo(((int) Float.parseFloat(attributes.get("" + Constants.PLANT_FLOWERING_TO + "_0").get(0))));
                    }

                    setTextInLanguage(Constants.LANGUAGE_EN, plant, attributes);

                    rank = 0;
                    List<String> photo_urls = new ArrayList<>();
                    while (attributes.containsKey("" + Constants.PLANT_PHOTO_URL + "_" + rank)) {
                        photo_urls.add(attributes.get(""+Constants.PLANT_PHOTO_URL+"_"+rank).get(0));
                        rank++;
                    }
                    plant.setPhotoUrls(photo_urls);
                }

                for (Map.Entry<Integer, String> lang : Constants.LANGUAGES.entrySet()) {
                    callSqlCloud = herbClient.getApiService().getDetail(new DetailRequest(lang.getKey(), plant.getPlantId()));

                    response = callSqlCloud.execute().body();

                    for (Map.Entry<Integer,Map<String,List<String>>> entry : response.entrySet()) {

                        Map<String, List<String>> attributes = entry.getValue();

                        setTextInLanguage(lang.getValue(), plant, attributes);
                    }
                }

                Call<Plant> callCloud =  herbCloudClient.getApiService().synchronizePlant(name[0], name[1], plant);

                callCloud.execute().body();

                System.out.println(plant.getName());

                if (i == 5) break;
            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setTextInLanguage(String language, Plant plant, Map<String, List<String>> attributes) {
        if (attributes.containsKey(""+Constants.PLANT_DESCRIPTION +"_0")) {
            String text = attributes.get("" + Constants.PLANT_DESCRIPTION + "_0").get(0);
            if (text != null && !text.equals(plant.getDescription().get(Constants.LANGUAGE_EN))) {
                plant.getDescription().put(language, text);
            }
        }
        if (attributes.containsKey("" + Constants.PLANT_FLOWER + "_0")) {
            String text = attributes.get("" + Constants.PLANT_FLOWER + "_0").get(0);
            if (text != null && !text.equals(plant.getFlower().get(Constants.LANGUAGE_EN))) {
                plant.getFlower().put(language, text);
            }
        }
        if (attributes.containsKey("" + Constants.PLANT_INFLORESCENCE + "_0")) {
            String text = attributes.get("" + Constants.PLANT_INFLORESCENCE + "_0").get(0);
            if (text != null && !text.equals(plant.getInflorescence().get(Constants.LANGUAGE_EN))) {
                plant.getInflorescence().put(language, text);
            }
        }
        if (attributes.containsKey("" + Constants.PLANT_FRUIT + "_0")) {
            String text = attributes.get("" + Constants.PLANT_FRUIT + "_0").get(0);
            if (text != null && !text.equals(plant.getFruit().get(Constants.LANGUAGE_EN))) {
                plant.getFruit().put(language, text);
            }
        }
        if (attributes.containsKey("" + Constants.PLANT_STEM+"_0")) {
            String text = attributes.get("" + Constants.PLANT_STEM + "_0").get(0);
            if (text != null && !text.equals(plant.getStem().get(Constants.LANGUAGE_EN))) {
                plant.getStem().put(language, text);
            }
        }
        if (attributes.containsKey("" + Constants.PLANT_LEAF + "_0")) {
            String text = attributes.get("" + Constants.PLANT_LEAF + "_0").get(0);
            if (text != null && !text.equals(plant.getLeaf().get(Constants.LANGUAGE_EN))) {
                plant.getLeaf().put(language, text);
            }
        }
        if (attributes.containsKey(""+Constants.PLANT_HABITAT+"_0")) {
            String text = attributes.get("" + Constants.PLANT_HABITAT + "_0").get(0);
            if (text != null && !text.equals(plant.getHabitat().get(Constants.LANGUAGE_EN))) {
                plant.getHabitat().put(language, text);
            }
        }
        if (attributes.containsKey(""+Constants.PLANT_TRIVIA+"_0")) {
            String text = attributes.get("" + Constants.PLANT_TRIVIA + "_0").get(0);
            if (text != null && !text.equals(plant.getTrivia().get(Constants.LANGUAGE_EN))) {
                plant.getTrivia().put(language, text);
            }
        }
        if (attributes.containsKey(""+Constants.PLANT_TOXICITY+"_0")) {
            String text = attributes.get("" + Constants.PLANT_TOXICITY + "_0").get(0);
            if (text != null && !text.equals(plant.getToxicity().get(Constants.LANGUAGE_EN))) {
                plant.getToxicity().put(language, text);
            }
        }
        if (attributes.containsKey(""+Constants.PLANT_HERBALISM+"_0")) {
            String text = attributes.get("" + Constants.PLANT_HERBALISM + "_0").get(0);
            if (text != null && !text.equals(plant.getHerbalism().get(Constants.LANGUAGE_EN))) {
                plant.getHerbalism().put(language, text);
            }
        }
        int rank = 0;
        List<String> source_urls = new ArrayList<>();
        while (attributes.containsKey(""+Constants.PLANT_SOURCE_URL+"_"+rank)) {
            String url = attributes.get(""+Constants.PLANT_SOURCE_URL+"_"+rank).get(0);
            String lang = attributes.get(""+Constants.PLANT_SOURCE_URL+"_"+rank).get(2);
            if (!url.contains("wikipedia") && (
                    (language.equals(Constants.LANGUAGE_EN) && Integer.parseInt(lang) == 0)
                            || (!language.equals(Constants.LANGUAGE_EN) && Integer.parseInt(lang) > 0))) {
                source_urls.add(url);
            }
            rank++;
        }
        plant.getSourceUrls().put(language, source_urls);
    }
}
