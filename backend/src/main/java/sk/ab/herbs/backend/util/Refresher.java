package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Response;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.service.FirebaseClient;

/**
 *
 * Created by adrian on 12/26/2017.
 */

public class Refresher {

    public static String PATH = "c:/Dev/Projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv"; //"sample.csv";

    public static String CELL_DELIMITER = ";";
    public static String FILTER_DELIMITER = "\\_";

    private static String[] filters = {
        "__",
        "__4 or less",
        "__5",
        "__Bisymetric",
        "__Many",
        "_gardens or fields_",
        "_gardens or fields_4 or less",
        "_gardens or fields_5",
        "_gardens or fields_Bisymetric",
        "_gardens or fields_Many",
        "_meadows or grassland_",
        "_meadows or grassland_4 or less",
        "_meadows or grassland_5",
        "_meadows or grassland_Bisymetric",
        "_meadows or grassland_Many",
        "_moorlands or wetlands_",
        "_moorlands or wetlands_4 or less",
        "_moorlands or wetlands_5",
        "_moorlands or wetlands_Bisymetric",
        "_moorlands or wetlands_Many",
        "_rocks or mountains_",
        "_rocks or mountains_4 or less",
        "_rocks or mountains_5",
        "_rocks or mountains_Bisymetric",
        "_rocks or mountains_Many",
        "_trees or bushes_",
        "_trees or bushes_4 or less",
        "_trees or bushes_5",
        "_trees or bushes_Bisymetric",
        "_trees or bushes_Many",
        "_woodlands or forests_",
        "_woodlands or forests_4 or less",
        "_woodlands or forests_5",
        "_woodlands or forests_Bisymetric",
        "_woodlands or forests_Many",
        "blue__",
        "blue__4 or less",
        "blue__5",
        "blue__Bisymetric",
        "blue__Many",
        "blue_gardens or fields_",
        "blue_gardens or fields_4 or less",
        "blue_gardens or fields_5",
        "blue_gardens or fields_Bisymetric",
        "blue_gardens or fields_Many",
        "blue_meadows or grassland_",
        "blue_meadows or grassland_4 or less",
        "blue_meadows or grassland_5",
        "blue_meadows or grassland_Bisymetric",
        "blue_meadows or grassland_Many",
        "blue_moorlands or wetlands_",
        "blue_moorlands or wetlands_4 or less",
        "blue_moorlands or wetlands_5",
        "blue_moorlands or wetlands_Bisymetric",
        "blue_moorlands or wetlands_Many",
        "blue_rocks or mountains_",
        "blue_rocks or mountains_4 or less",
        "blue_rocks or mountains_5",
        "blue_rocks or mountains_Bisymetric",
        "blue_rocks or mountains_Many",
        "blue_trees or bushes_",
        "blue_trees or bushes_4 or less",
        "blue_trees or bushes_5",
        "blue_trees or bushes_Bisymetric",
        "blue_trees or bushes_Many",
        "blue_woodlands or forests_",
        "blue_woodlands or forests_4 or less",
        "blue_woodlands or forests_5",
        "blue_woodlands or forests_Bisymetric",
        "blue_woodlands or forests_Many",
        "green__",
        "green__4 or less",
        "green__5",
        "green__Bisymetric",
        "green__Many",
        "green_gardens or fields_",
        "green_gardens or fields_4 or less",
        "green_gardens or fields_5",
        "green_gardens or fields_Bisymetric",
        "green_gardens or fields_Many",
        "green_meadows or grassland_",
        "green_meadows or grassland_4 or less",
        "green_meadows or grassland_5",
        "green_meadows or grassland_Bisymetric",
        "green_meadows or grassland_Many",
        "green_moorlands or wetlands_",
        "green_moorlands or wetlands_4 or less",
        "green_moorlands or wetlands_5",
        "green_moorlands or wetlands_Bisymetric",
        "green_moorlands or wetlands_Many",
        "green_rocks or mountains_",
        "green_rocks or mountains_4 or less",
        "green_rocks or mountains_5",
        "green_rocks or mountains_Bisymetric",
        "green_rocks or mountains_Many",
        "green_trees or bushes_",
        "green_trees or bushes_4 or less",
        "green_trees or bushes_5",
        "green_trees or bushes_Bisymetric",
        "green_trees or bushes_Many",
        "green_woodlands or forests_",
        "green_woodlands or forests_4 or less",
        "green_woodlands or forests_5",
        "green_woodlands or forests_Bisymetric",
        "green_woodlands or forests_Many",
        "red__",
        "red__4 or less",
        "red__5",
        "red__Bisymetric",
        "red__Many",
        "red_gardens or fields_",
        "red_gardens or fields_4 or less",
        "red_gardens or fields_5",
        "red_gardens or fields_Bisymetric",
        "red_gardens or fields_Many",
        "red_meadows or grassland_",
        "red_meadows or grassland_4 or less",
        "red_meadows or grassland_5",
        "red_meadows or grassland_Bisymetric",
        "red_meadows or grassland_Many",
        "red_moorlands or wetlands_",
        "red_moorlands or wetlands_4 or less",
        "red_moorlands or wetlands_5",
        "red_moorlands or wetlands_Bisymetric",
        "red_moorlands or wetlands_Many",
        "red_rocks or mountains_",
        "red_rocks or mountains_4 or less",
        "red_rocks or mountains_5",
        "red_rocks or mountains_Bisymetric",
        "red_rocks or mountains_Many",
        "red_trees or bushes_",
        "red_trees or bushes_4 or less",
        "red_trees or bushes_5",
        "red_trees or bushes_Bisymetric",
        "red_trees or bushes_Many",
        "red_woodlands or forests_",
        "red_woodlands or forests_4 or less",
        "red_woodlands or forests_5",
        "red_woodlands or forests_Bisymetric",
        "red_woodlands or forests_Many",
        "white__",
        "white__4 or less",
        "white__5",
        "white__Bisymetric",
        "white__Many",
        "white_gardens or fields_",
        "white_gardens or fields_4 or less",
        "white_gardens or fields_5",
        "white_gardens or fields_Bisymetric",
        "white_gardens or fields_Many",
        "white_meadows or grassland_",
        "white_meadows or grassland_4 or less",
        "white_meadows or grassland_5",
        "white_meadows or grassland_Bisymetric",
        "white_meadows or grassland_Many",
        "white_moorlands or wetlands_",
        "white_moorlands or wetlands_4 or less",
        "white_moorlands or wetlands_5",
        "white_moorlands or wetlands_Bisymetric",
        "white_moorlands or wetlands_Many",
        "white_rocks or mountains_",
        "white_rocks or mountains_4 or less",
        "white_rocks or mountains_5",
        "white_rocks or mountains_Bisymetric",
        "white_rocks or mountains_Many",
        "white_trees or bushes_",
        "white_trees or bushes_4 or less",
        "white_trees or bushes_5",
        "white_trees or bushes_Bisymetric",
        "white_trees or bushes_Many",
        "white_woodlands or forests_",
        "white_woodlands or forests_4 or less",
        "white_woodlands or forests_5",
        "white_woodlands or forests_Bisymetric",
        "white_woodlands or forests_Many",
        "yellow__",
        "yellow__4 or less",
        "yellow__5",
        "yellow__Bisymetric",
        "yellow__Many",
        "yellow_gardens or fields_",
        "yellow_gardens or fields_4 or less",
        "yellow_gardens or fields_5",
        "yellow_gardens or fields_Bisymetric",
        "yellow_gardens or fields_Many",
        "yellow_meadows or grassland_",
        "yellow_meadows or grassland_4 or less",
        "yellow_meadows or grassland_5",
        "yellow_meadows or grassland_Bisymetric",
        "yellow_meadows or grassland_Many",
        "yellow_moorlands or wetlands_",
        "yellow_moorlands or wetlands_4 or less",
        "yellow_moorlands or wetlands_5",
        "yellow_moorlands or wetlands_Bisymetric",
        "yellow_moorlands or wetlands_Many",
        "yellow_rocks or mountains_",
        "yellow_rocks or mountains_4 or less",
        "yellow_rocks or mountains_5",
        "yellow_rocks or mountains_Bisymetric",
        "yellow_rocks or mountains_Many",
        "yellow_trees or bushes_",
        "yellow_trees or bushes_4 or less",
        "yellow_trees or bushes_5",
        "yellow_trees or bushes_Bisymetric",
        "yellow_trees or bushes_Many",
        "yellow_woodlands or forests_",
        "yellow_woodlands or forests_4 or less",
        "yellow_woodlands or forests_5",
        "yellow_woodlands or forests_Bisymetric",
        "yellow_woodlands or forests_Many"
    };

    public static void main(String[] params) {

        countAndList();
        search();
    }

    private static void countAndList() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<String, Boolean>> lists = new HashMap<>();

        for (String filter : filters) {
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

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = plantCall.execute().body();

                for (String filter : filters) {

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

    private static void search() {
        // {"cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};
        String[] languages = {"cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            for (String language : languages) {
                System.out.println(language);

                Map<String, Map<String, Boolean>> searchMap = new HashMap<>();

                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> translation = translationCall.execute().body();

                for (Map.Entry<String, Object> entry : translation.entrySet()) {
                    String plantName = entry.getKey();

                    Map<String, Object> plant = (Map<String, Object>) entry.getValue();

                    Map<String, Boolean> searchForLabel = null;
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
                        searchForLabel.put(plantName, true);
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
                            searchForLabel.put(plantName, true);
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
            Map<String, Map<String, Boolean>> searchMap = new HashMap<>();

            File file = new File(PATH + PLANTS_FILE);
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                System.out.println(plantLine[0]);

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = plantCall.execute().body();

                Map<String, Boolean> searchForLabel = null;
                String label = plant.getName();
                if (label != null) {
                    label = label.toLowerCase();

                    searchForLabel = searchMap.get(label);
                    if (searchForLabel == null) {
                        searchForLabel = new HashMap<>();
                        searchMap.put(label, searchForLabel);
                    }
                    searchForLabel.put(plantLine[0], true);
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
                        searchForLabel.put(plantLine[0], true);
                    }
                }
            }

            Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch("la", searchMap);
            Response<Object> response = callFirebaseSearch.execute();
        } catch (IOException ex) {

        }

    }

}
