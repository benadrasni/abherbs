package sk.ab.herbs.backend.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Response;
import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantFilter;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.util.Utils;

/**
 *
 * Created by adrian on 12/26/2017.
 */

public class Refresher {

    private static String PATH = "c:/Dev/Projects/abherbs/backend/txt/";
    private static String PLANTS_FILE = "plants.csv";

    private static String CELL_DELIMITER = ";";
    private static String FILTER_DELIMITER = "\\_";

    private static final String[] COLORS = {null, "white", "yellow", "red", "blue", "green"};
    private static final String[] COLORS_IDS = {null, "1", "2", "3", "4", "5"};
    private static final String[] HABITATS = {null, "meadows or grassland", "gardens or fields", "moorlands or wetlands", "woodlands or forests", "rocks or mountains", "trees or bushes"};
    private static final String[] HABITATS_IDS = {null, "1", "2", "3", "4", "5", "6"};
    private static final String[] PETALS = {null,"4 or less", "5", "Many", "Bisymetric"};
    private static final String[] PETALS_IDS = {null,"1", "2", "3", "4"};
    private static final String[] DISTRIBUTION = {null, "10", "11", "12", "13", "14", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "40", "41", "42", "43", "50", "51", "60", "61", "62", "63", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "90", "91"};

    private static final String[] FILTER_3_ATTRIBUTES = {Constants.COLOR_OF_FLOWERS, Constants.HABITAT, Constants.NUMBER_OF_PETALS};
    private static final String[] FILTER_4_ATTRIBUTES = {Constants.COLOR_OF_FLOWERS, Constants.HABITAT, Constants.NUMBER_OF_PETALS, Constants.DISTRIBUTION};

    public static void main(String[] params) {

        //countAndList3();
        //countAndList4();
        //countAndList4Ids();
        search();
        //photoSearch();
    }

    private static void countAndList3() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<String, Boolean>> lists = new HashMap<>();

        List<String> filtersWith3Attributes = generateCountsAndLists3();

        for (String filter : filtersWith3Attributes) {
            counts.put(filter, 0);
            lists.put(filter, new HashMap<String, Boolean>());
        }

        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                //System.out.println(plantLine[0]);

                Call<PlantFilter> plantCall = firebaseClient.getApiService().getPlantFilter(plantLine[0]);
                PlantFilter plant = plantCall.execute().body();

                for (String filter : filtersWith3Attributes) {

                    String[] filterParts = filter.split(FILTER_DELIMITER, -1);

                    if ((filterParts[0].isEmpty() || plant.getFilterColor().contains(filterParts[0]))
                            && (filterParts[1].isEmpty() || plant.getFilterHabitat().contains(filterParts[1]))
                            && (filterParts[2].isEmpty() || plant.getFilterPetal().contains(filterParts[2]))) {
                        counts.put(filter, counts.get(filter) + 1);
                        lists.get(filter).put(plantLine[0], true);
                    }
                }

            }

            Call<Map> callFirebaseCount = firebaseClient.getApiService().saveCount(counts);
            callFirebaseCount.execute().body();

            Call<Map> callFirebaseList = firebaseClient.getApiService().saveList(lists);
            callFirebaseList.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void countAndList4() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<String, Boolean>> lists = new HashMap<>();

        List<String> filtersWith4Attributes = generateCountsAndLists4();

        for (String filter : filtersWith4Attributes) {
            counts.put(filter, 0);
            lists.put(filter, new HashMap<String, Boolean>());
        }

        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                System.out.println(plantLine[0]);

                Call<PlantFilter> plantCall = firebaseClient.getApiService().getPlantFilter(plantLine[0]);
                PlantFilter plant = plantCall.execute().body();

                for (String filter : filtersWith4Attributes) {

                    String[] filterParts = filter.split(FILTER_DELIMITER, -1);

                    if ((filterParts[0].isEmpty() || plant.getFilterColor().contains(filterParts[0]))
                            && (filterParts[1].isEmpty() || plant.getFilterHabitat().contains(filterParts[1]))
                            && (filterParts[2].isEmpty() || plant.getFilterPetal().contains(filterParts[2]))
                            && (filterParts[3].isEmpty() || plant.getFilterDistribution().contains(Integer.parseInt(filterParts[3])))) {
                        counts.put(filter, counts.get(filter) + 1);
                        lists.get(filter).put(plantLine[0], true);
                    }
                }

            }
            scan.close();

            Call<Map> callFirebaseCount = firebaseClient.getApiService().saveCount(counts);
            callFirebaseCount.execute().body();

            Call<Map> callFirebaseList = firebaseClient.getApiService().saveList(lists);
            callFirebaseList.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void countAndList4Ids() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<Integer, Integer>> lists = new HashMap<>();

        List<String> filtersWith4Attributes = generateCountsAndLists4Ids();

        for (String filter : filtersWith4Attributes) {
            counts.put(filter, 0);
            lists.put(filter, new HashMap<Integer, Integer>());
        }

        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);
                Integer id = Integer.parseInt(plantLine[2]) - 1;

                System.out.println(id + ": " + plantLine[0]);

                Call<PlantHeader> plantCall = firebaseClient.getApiService().getPlantHeader(id);
                PlantHeader plant = plantCall.execute().body();

                for (String filter : filtersWith4Attributes) {

                    String[] filterParts = filter.split(FILTER_DELIMITER, -1);

                    if ((filterParts[0].isEmpty() || plant.getFilterColor().contains(Integer.parseInt(filterParts[0])))
                            && (filterParts[1].isEmpty() || plant.getFilterHabitat().contains(Integer.parseInt(filterParts[1])))
                            && (filterParts[2].isEmpty() || plant.getFilterPetal().contains(Integer.parseInt(filterParts[2])))
                            && (filterParts[3].isEmpty() || plant.getFilterDistribution().contains(Integer.parseInt(filterParts[3])))) {
                        counts.put(filter, counts.get(filter) + 1);
                        lists.get(filter).put(id, 1);
                    }
                }

            }
            scan.close();

            Call<Map> callFirebaseCount = firebaseClient.getApiService().saveCount(counts);
            callFirebaseCount.execute().body();

            Call<Map> callFirebaseList = firebaseClient.getApiService().saveListIds(lists);
            callFirebaseList.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void search() {
        // {"cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};
        String[] languages = {"cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            File file = new File(PATH + PLANTS_FILE);
            Map<String, Integer> plants =  new HashMap<>();
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);
                plants.put(plantLine[0], Integer.parseInt(plantLine[2])-1);
            }
            scan.close();


            for (String language : languages) {
                System.out.println(language);

                Map<String, Map<Integer, Integer>> searchMap = new HashMap<>();

                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> translation = translationCall.execute().body();

                for (Map.Entry<String, Object> entry : translation.entrySet()) {
                    Integer plantName = plants.get(entry.getKey());

                    Map<String, Object> plant = (Map<String, Object>) entry.getValue();

                    Map<Integer, Integer> searchForLabel = null;
                    String label = (String)plant.get("label");
                    if (label != null) {
                        label = label.toLowerCase();
                        if (label.isEmpty() || label.contains(".") || label.contains("/") || label.contains("#") || label.contains("$") || label.contains("[") || label.contains("]")) {
                            System.out.println(plantName);
                        }

                        searchForLabel = searchMap.get(label);
                        if (searchForLabel == null) {
                            searchForLabel = new HashMap<>();
                            searchMap.put(label, searchForLabel);
                        }
                        searchForLabel.put(plantName, 1);
                    }

                    List<String> names = (List<String>)plant.get("names");
                    if (names != null) {
                        for (String name : names) {
                            if (name.isEmpty() || name.contains(".") || name.contains("/") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]")) {
                                System.out.println(plantName);
                            }

                            name = name.toLowerCase();
                            searchForLabel = searchMap.get(name);
                            if (searchForLabel == null) {
                                searchForLabel = new HashMap<>();
                                searchMap.put(name, searchForLabel);
                            }
                            searchForLabel.put(plantName, 1);
                        }
                    }
                }

                // name
//                for (Map.Entry<String, Map<String,Boolean>> entry : searchMap.entrySet()) {
//                    String key = entry.getKey();
//                    Map<String,Boolean> value = entry.getValue();
//
//                    System.out.println(key);
//                    Call<Object> callFirebaseSearch = firebaseClient.getApiService().savePartialSearch(language, key, value);
//                    Response<Object> response = callFirebaseSearch.execute();
//                    if (response.errorBody() != null) {
//                        System.out.println(response.errorBody().string());
//                    }
//                }
                Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch(language, searchMap);
                Response<Object> response = callFirebaseSearch.execute();
                if (response.errorBody() != null) {
                    System.out.println(response.errorBody().string());
                }
            }

            // latin
            Map<String, Map<Integer, Integer>> searchMap = new HashMap<>();

            scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);
                Integer plantName = plants.get(plantLine[0]);

                //System.out.println(plantLine[0]);

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = plantCall.execute().body();

                Map<Integer, Integer> searchForLabel = null;
                String label = plant.getName();
                if (label != null) {
                    label = label.toLowerCase();

                    searchForLabel = searchMap.get(label);
                    if (searchForLabel == null) {
                        searchForLabel = new HashMap<>();
                        searchMap.put(label, searchForLabel);
                    }
                    searchForLabel.put(plantName, 1);
                }

                List<String> names = plant.getSynonyms();
                if (names != null) {
                    for (String name : names) {

                        if (name.isEmpty()) {
                            System.out.println("Empty: " + plant.getName());
                        }

                        if (name.contains(".")) {
                            continue;
                        }

                        name = name.toLowerCase();
                        searchForLabel = searchMap.get(name);
                        if (searchForLabel == null) {
                            searchForLabel = new HashMap<>();
                            searchMap.put(name, searchForLabel);
                        }
                        searchForLabel.put(plantName, 1);
                    }
                }
            }
            scan.close();

            Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch("la", searchMap);
            Response<Object> response = callFirebaseSearch.execute();
        } catch (IOException ex) {

        }

    }

    private static List<String> generateCountsAndLists4() {
        List<String> result = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();

        for (String color : COLORS) {
            filter.put(Constants.COLOR_OF_FLOWERS, color);

            for (String habitat : HABITATS) {
                filter.put(Constants.HABITAT, habitat);

                for (String petal : PETALS) {
                    filter.put(Constants.NUMBER_OF_PETALS, petal);

                    for (String distribution : DISTRIBUTION) {
                        filter.put(Constants.DISTRIBUTION, distribution);
                        result.add(Utils.getFilterKey(filter, FILTER_4_ATTRIBUTES));

                    }
                    filter.remove(Constants.DISTRIBUTION);
                }
                filter.remove(Constants.NUMBER_OF_PETALS);
            }
            filter.remove(Constants.HABITAT);
        }

        return result;
    }

    private static List<String> generateCountsAndLists4Ids() {
        List<String> result = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();

        for (String color : COLORS_IDS) {
            filter.put(Constants.COLOR_OF_FLOWERS, color);

            for (String habitat : HABITATS_IDS) {
                filter.put(Constants.HABITAT, habitat);

                for (String petal : PETALS_IDS) {
                    filter.put(Constants.NUMBER_OF_PETALS, petal);

                    for (String distribution : DISTRIBUTION) {
                        filter.put(Constants.DISTRIBUTION, distribution);
                        result.add(Utils.getFilterKey(filter, FILTER_4_ATTRIBUTES));

                    }
                    filter.remove(Constants.DISTRIBUTION);
                }
                filter.remove(Constants.NUMBER_OF_PETALS);
            }
            filter.remove(Constants.HABITAT);
        }

        return result;
    }

    private static List<String> generateCountsAndLists3() {
        List<String> result = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();

        for (String color : COLORS) {
            filter.put(Constants.COLOR_OF_FLOWERS, color);

            for (String habitat : HABITATS) {
                filter.put(Constants.HABITAT, habitat);

                for (String petal : PETALS) {
                    filter.put(Constants.NUMBER_OF_PETALS, petal);
                    result.add(Utils.getFilterKey(filter, FILTER_3_ATTRIBUTES));

                }
                filter.remove(Constants.NUMBER_OF_PETALS);
            }
            filter.remove(Constants.HABITAT);
        }

        return result;
    }

    private static void photoSearch() {
        try {
            final FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map<String, Object>> apgivCall = firebaseClient.getApiService().getAPGIV2();
            Map<String, Object> apgiv = apgivCall.execute().body();
            Map<String, Map<String, Object>> photoSearch = new HashMap<>();

            parseAPGIV("", apgiv, photoSearch, "APG IV_v2/");

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().create();
            System.out.println(gson.toJson(photoSearch));

//            Call<Object> callFirebaseSearch = firebaseClient.getApiService().savePhotoSearch(photoSearch);
//            Response<Object> response = callFirebaseSearch.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void parseAPGIV(String taxon, Map<String, Object> apgiv, Map<String, Map<String, Object>> photoSearch, String path) {

        Map<String, Object> item = new HashMap<>();
        boolean isDesiredType = false;
        for (String key : apgiv.keySet()) {

            if ("type".equals(key)) {
                String type = (String)apgiv.get(key);
                isDesiredType = "Genus".equals(type) || "Familia".equals(type);
            } else if ("count".equals(key)) {
                String count = String.format("%.0f", apgiv.get(key));
                item.put("count", Integer.parseInt(count));
            } else if ("list".equals(key)) {
                item.put("path", path + "list");
            } else {
                parseAPGIV(key, (Map<String, Object>)apgiv.get(key), photoSearch, path + key + "/");
            }
        }

        if (isDesiredType) {
            photoSearch.put(taxon.toLowerCase(), item);

            final FirebaseClient firebaseClient = new FirebaseClient();
            Call<List<String>> translationTaxonomyCall = firebaseClient.getApiService().getTranslationTaxonomy("en", taxon);
            try {
                List<String> enNames = translationTaxonomyCall.execute().body();
                if (enNames != null) {
                    for (String name : enNames) {
                        photoSearch.put(name.toLowerCase(), item);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
