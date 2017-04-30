package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import retrofit2.Call;
import sk.ab.common.Constants;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.entity.PlantList;
import sk.ab.common.entity.Taxon;
import sk.ab.common.entity.request.ListRequest;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.service.HerbCloudClient;
import sk.ab.common.util.Utils;

/**
 *
 * Created by adrian on 4.5.2016.
 */
public class Firebase {
    private static String PATH_TO_PLANTS = "C:/Dev/Projects/abherbs/backend/txt/plants.csv";
    private static String PATH_TO_FAMILIES = "C:/Dev/Projects/abherbs/backend/txt/Families.csv";
    private static String PATH_TO_STORAGE = "C:/Dev/Storage/";

    private static String GOOGLE_STORAGE_URL = "https://storage.googleapis.com/abherbs";

//    private static final List<String> LANGUAGES = new ArrayList<>(Arrays.asList("cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "la", "lv", "lt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"));
    private static final List<String> LANGUAGES = new ArrayList<>(Arrays.asList("ja", "lv", "lt", "et"));

    private static final String[] COLORS = {"white", "yellow", "red", "blue", "green"};
    private static final String[] HABITATS = {"meadows or grassland", "gardens or fields", "moorlands or wetlands", "woodlands or forests", "rocks or mountains", "trees or bushes"};
    private static final String[] PETALS = {"4 or less", "5", "Many", "Bisymetric"};

    private static final String[] FILTER_ATTRIBUTES = {Constants.COLOR_OF_FLOWERS, Constants.HABITAT, Constants.NUMBER_OF_PETALS};


    public static void main(String[] params) {

        firebaseSynchronization();
    }

    private static void firebaseSynchronization() {

        final HerbCloudClient herbCloudClient = new HerbCloudClient();
        final FirebaseClient firebaseClient = new FirebaseClient();

//        synchronizeCountsAndLists(herbCloudClient, firebaseClient);

        synchronizeDetailsAndNames(herbCloudClient, firebaseClient);
//
//        downloadFamilyIcons();
//
//        downloadPhotos(herbCloudClient);
    }

    private static void downloadFamilyIcons() {
        File file = new File(PATH_TO_FAMILIES);

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                final String[] row = scan.nextLine().split(",");
                System.out.println(row[1]);
                try {
                    Utils.downloadFromUrl(new URL(GOOGLE_STORAGE_URL + "/.families/family_" + row[0] + ".webp"), PATH_TO_STORAGE + "families/" + row[1] + ".webp");
                } catch (IOException e) {
                }

            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadPhotos(HerbCloudClient herbCloudClient) {
        File file = new File(PATH_TO_PLANTS);

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                final String[] plantLine = scan.nextLine().split(";");
                System.out.println(plantLine[0]);

                Call<Plant> callCloudPlant = herbCloudClient.getApiService().getDetail(plantLine[0]);
                Plant plant = callCloudPlant.execute().body();

                try {
                    Utils.downloadFromUrl(new URL(plant.getIllustrationUrl()), PATH_TO_STORAGE + plant.getIllustrationUrl().substring(GOOGLE_STORAGE_URL.length()));

                    for(String photoUrl : plant.getPhotoUrls()) {
                        Utils.downloadFromUrl(new URL(photoUrl), PATH_TO_STORAGE + "photos/" + photoUrl.substring(GOOGLE_STORAGE_URL.length()));
                        String thumbnailUrl = photoUrl.substring(0, photoUrl.lastIndexOf("/")) + "/.thumbnails" + photoUrl.substring(photoUrl.lastIndexOf("/"));
                        Utils.downloadFromUrl(new URL(thumbnailUrl), PATH_TO_STORAGE + "photos/" + thumbnailUrl.substring(GOOGLE_STORAGE_URL.length()));
                    }

                } catch (IOException e) {
                }

            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void synchronizeCountsAndLists(HerbCloudClient herbCloudClient, FirebaseClient firebaseClient) {
        try {
            Map<String, String> filter = new HashMap<>();
            getAndSave(herbCloudClient, firebaseClient, filter);

            for (String color : COLORS) {
                filter.put(Constants.COLOR_OF_FLOWERS, color);
                getAndSave(herbCloudClient, firebaseClient, filter);

                for (String habitat : HABITATS) {
                    filter.put(Constants.HABITAT, habitat);
                    getAndSave(herbCloudClient, firebaseClient, filter);

                    for (String petal : PETALS) {
                        filter.put(Constants.NUMBER_OF_PETALS, petal);
                        getAndSave(herbCloudClient, firebaseClient, filter);

                    }
                    filter.remove(Constants.NUMBER_OF_PETALS);
                }
                filter.remove(Constants.HABITAT);
            }

            filter.clear();
            for (String color : COLORS) {
                filter.put(Constants.COLOR_OF_FLOWERS, color);

                for (String petal : PETALS) {
                    filter.put(Constants.NUMBER_OF_PETALS, petal);
                    getAndSave(herbCloudClient, firebaseClient, filter);

                }
            }

            filter.clear();
            for (String habitat : HABITATS) {
                filter.put(Constants.HABITAT, habitat);
                getAndSave(herbCloudClient, firebaseClient, filter);

                for (String petal : PETALS) {
                    filter.put(Constants.NUMBER_OF_PETALS, petal);
                    getAndSave(herbCloudClient, firebaseClient, filter);

                }
                filter.remove(Constants.NUMBER_OF_PETALS);
            }

            filter.clear();
            for (String petal : PETALS) {
                filter.put(Constants.NUMBER_OF_PETALS, petal);
                getAndSave(herbCloudClient, firebaseClient, filter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void synchronizeDetailsAndNames(HerbCloudClient herbCloudClient, FirebaseClient firebaseClient) {
        try {
            File file = new File(PATH_TO_PLANTS);

            Map<String, Map<String, Map<String, Boolean>>> names = new HashMap<>();
            Object apgiii = new HashMap<String, Object>();
            Object translations = new HashMap<String, Object>();

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(";");
                System.out.println(plantLine[0]);

                Call<Plant> callCloudPlant = herbCloudClient.getApiService().getDetail(plantLine[0]);
                Plant plant = callCloudPlant.execute().body();

//                plant.setIllustrationUrl(plant.getIllustrationUrl().substring(plant.getIllustrationUrl().indexOf("abherbs")+8));
//                for (int i = 0; i < plant.getPhotoUrls().size(); i++) {
//                    plant.getPhotoUrls().set(i, plant.getPhotoUrls().get(i).substring(plant.getPhotoUrls().get(i).indexOf("abherbs")+8));
//                }

//                FirebasePlant firebasePlant = new FirebasePlant(plant);
//                Call<FirebasePlant> callFirebase = firebaseClient.getApiService().savePlant(plantLine[0], firebasePlant);
//                callFirebase.execute().body();

//                updateTranslations(translations, plant);

                Call<PlantHeader> callCloudPlantHeader = herbCloudClient.getApiService().getHeader(plantLine[0]);
                PlantHeader plantHeader = callCloudPlantHeader.execute().body();

//                updateTaxonomy(herbCloudClient, apgiii, plant.getTaxonomy(), plantHeader);

                //labels
                for (Map.Entry<String, String> entry : plant.getLabel().entrySet()) {
                String language = entry.getKey();
                if (!LANGUAGES.contains(language)) {
                    continue;
                }
                String plantNameInLanguage = entry.getValue();

                Map<String, Map<String, Boolean>> namesInLanguage = names.get(language);
                if (namesInLanguage == null) {
                    namesInLanguage = new HashMap<>();
                    names.put(language, namesInLanguage);
                }

                processName(namesInLanguage, plantHeader, plantNameInLanguage);
            }

            // synonyms
            for (String synonym : plant.getSynonyms()) {
                String language = "la";
                if (!LANGUAGES.contains(language)) {
                    continue;
                }

                Map<String, Map<String, Boolean>> namesInLanguage = names.get(language);
                if (namesInLanguage == null) {
                    namesInLanguage = new HashMap<>();
                    names.put(language, namesInLanguage);
                }

                processName(namesInLanguage, plantHeader, synonym);
            }

            // aliases
            for (Map.Entry<String, ArrayList<String>> entry : plant.getNames().entrySet()) {
                String language = entry.getKey();
                if (!LANGUAGES.contains(language)) {
                    continue;
                }
                ArrayList<String> plantNamesInLanguage = entry.getValue();

                Map<String, Map<String, Boolean>> namesInLanguage = names.get(language);
                if (namesInLanguage == null) {
                    namesInLanguage = new HashMap<>();
                    names.put(language, namesInLanguage);
                }

                for (String realName : plantNamesInLanguage) {
                    processName(namesInLanguage, plantHeader, realName);
                }
            }
        }

        for (Map.Entry<String, Map<String, Map<String, Boolean>>> entry : names.entrySet()) {
            String language = entry.getKey();
            Map<String, Map<String, Boolean>> singleName = entry.getValue();

            System.out.println("Language: " + language);

            for (Map.Entry<String, Map<String, Boolean>> entryName : singleName.entrySet()) {
                String name = entryName.getKey();
                Map<String, Boolean> plantList = entryName.getValue();

                System.out.println("Name: " + name);

                // name
                Call<Map<String, Boolean>> callFirebaseList = firebaseClient.getApiService().saveName(language, name, plantList);
                callFirebaseList.execute().body();
            }
        }

//            Call<Object> callFirebaseAPG = firebaseClient.getApiService().saveAPGIII(apgiii);
//            callFirebaseAPG.execute().body();

//            Call<Object> callFirebaseTranslations = firebaseClient.getApiService().saveTranslations(translations);
//            callFirebaseTranslations.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processName(Map<String, Map<String, Boolean>> namesInLanguage,
                             PlantHeader plantHeader, String realName) {
        String name = realName.toLowerCase();
        Map<String, Boolean> existingNames = namesInLanguage.get(name);
        if (existingNames == null) {
            existingNames = new HashMap<>();
            namesInLanguage.put(name, existingNames);
        }
        existingNames.put(plantHeader.getId(), true);

//        String[] parts = name.split(" ");
//        if (parts.length > 1) {
//            for (int i = 0; i < parts.length; i++) {
//                if (parts[i].length() > 3) {
//                    existingNames = namesInLanguage.get(parts[i]);
//                    if (existingNames == null) {
//                        existingNames = new HashMap<>();
//                        namesInLanguage.put(parts[i], existingNames);
//                    }
//                    existingNames.put(plantHeader.getId(), true);
//                }
//            }
//        }
//        parts = name.split("-");
//        if (parts.length > 1) {
//            for (int i = 0; i < parts.length; i++) {
//                if (parts[i].length() > 3) {
//                    existingNames = namesInLanguage.get(parts[i]);
//                    if (existingNames == null) {
//                        existingNames = new HashMap<>();
//                        namesInLanguage.put(parts[i], existingNames);
//                    }
//                    existingNames.put(plantHeader.getId(), true);
//                }
//            }
//        }
    }

    private static void getAndSave(HerbCloudClient herbCloudClient,
                                   FirebaseClient firebaseClient,
                                   Map<String, String> filter) throws IOException {

        String filterKey = Utils.getFilterKey(filter, FILTER_ATTRIBUTES);
        System.out.println(filterKey);

        // count
        Call<Count> callCloudCount = herbCloudClient.getApiService().getCount(new ListRequest(Constants.PLANT, filter));
        Count count = callCloudCount.execute().body();
        Map<String, Integer> filterCount = new HashMap<>();
        filterCount.put(filterKey, count.getCount());
        Call<Map> callFirebaseCount = firebaseClient.getApiService().saveCount(filterCount);
        callFirebaseCount.execute().body();

        // list
        if (filter.size() == 3 || count.getCount() <= Constants.LIST_THRESHOLD) {
            Call<PlantList> callCloudList = herbCloudClient.getApiService().getList(new ListRequest(Constants.PLANT, filter));
            PlantList list = callCloudList.execute().body();

            if (list.getItems() != null) {
                Map<String, Boolean> plantList = new HashMap<>();
                for (PlantHeader plantHeader : list.getItems()) {
                    plantList.put(plantHeader.getId(), true);
                }

                Map<String, Map<String, Boolean>> filterList = new HashMap<>();
                filterList.put(filterKey, plantList);
                Call<Map> callFirebaseList = firebaseClient.getApiService().saveList(filterList);
                callFirebaseList.execute().body();
            }
        }
    }

    private static void updateTaxonomy(HerbCloudClient herbCloudClient, Object apgiii,
                                       HashMap<String, String> taxonomy,
                                       PlantHeader plantHeader) throws IOException {
        boolean savePlantHeader = false;
        Object iter = apgiii;

        List<String> sortedKeys=new ArrayList<>(taxonomy.keySet());
        Collections.sort(sortedKeys, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((String)o2).compareTo((String)o1);
            }
        });

        for (String key : sortedKeys) {
            String value = taxonomy.get(key);
            String taxonType = key.substring(key.indexOf("_")+1);

            Object child = ((Map<String, Object>) iter).get(value);
            if (child == null) {
                child = new HashMap<String, Object>();
                ((Map<String, Object>) iter).put(value, child);
                ((Map<String, Object>) child).put("type", taxonType);

                Call<Taxon> callCloudTaxon = herbCloudClient.getApiService().getTaxon(taxonType, value);
                Taxon taxon = callCloudTaxon.execute().body();

                if (taxon.getNames() == null) {
                    System.out.println("!!! Wrong taxon: " + taxonType + " - " + value);
                } else {
                    ((Map<String, Object>) child).put("names", taxon.getNames());
                }
            }

            if ("Ordo".equals(taxonType)) {
                savePlantHeader = true;
            }

            if (savePlantHeader) {
                Map<String, Boolean> plants = (Map<String, Boolean>)((Map<String, Object>)child).get("list");
                if (plants == null) {
                    plants = new HashMap<>();
                    ((Map<String, Object>)child).put("list", plants);
                }
                plants.put(plantHeader.getId(), true);
                ((Map<String, Object>)child).put("count", plants.size());
            }

            iter = child;
        }
    }

    private static void updateTranslations(Object translations, Plant plant) throws IOException {
        updateTranslationString(translations, plant.getName(), plant.getLabel(), "label");
        updateTranslationArray(translations, plant.getName(), plant.getNames(), "names");
        updateTranslationString(translations, plant.getName(), plant.getDescription(), "description");
        updateTranslationString(translations, plant.getName(), plant.getFlower(), "flower");
        updateTranslationString(translations, plant.getName(), plant.getInflorescence(), "inflorescence");
        updateTranslationString(translations, plant.getName(), plant.getFruit(), "fruit");
        updateTranslationString(translations, plant.getName(), plant.getStem(), "stem");
        updateTranslationString(translations, plant.getName(), plant.getLeaf(), "leaf");
        updateTranslationString(translations, plant.getName(), plant.getHabitat(), "habitat");
        updateTranslationString(translations, plant.getName(), plant.getToxicity(), "toxicity");
        updateTranslationString(translations, plant.getName(), plant.getHerbalism(), "herbalism");
        updateTranslationString(translations, plant.getName(), plant.getWikilinks(), "wikipedia");
        updateTranslationArray(translations, plant.getName(), plant.getSourceUrls(), "sourceUrls");

    }

    private static void updateTranslationString(Object translations, String name, HashMap<String, String> map, String key) {
        for (String language : map.keySet()) {
            if ("la".equals(language)) {
                continue;
            }
            Object lang = ((Map<String, Object>) translations).get(language);
            if (lang == null) {
                lang = new HashMap<String, Object>();
                ((Map<String, Object>) translations).put(language, lang);
            }
            Object plantObject = ((Map<String, Object>) lang).get(name);
            if (plantObject == null) {
                plantObject = new HashMap<String, Object>();
                ((Map<String, Object>) lang).put(name, plantObject);
            }
            ((Map<String, Object>)plantObject).put(key, map.get(language));

        }
    }

    private static void updateTranslationArray(Object translations, String name, HashMap<String, ArrayList<String>> map, String key) {
        for (String language : map.keySet()) {
            if ("la".equals(language)) {
                continue;
            }
            Object lang = ((Map<String, Object>) translations).get(language);
            if (lang == null) {
                lang = new HashMap<String, Object>();
                ((Map<String, Object>) translations).put(language, lang);
            }
            Object plantObject = ((Map<String, Object>) lang).get(name);
            if (plantObject == null) {
                plantObject = new HashMap<String, Object>();
                ((Map<String, Object>) lang).put(name, plantObject);
            }
            ((Map<String, Object>)plantObject).put(key, map.get(language));

        }
    }

}
