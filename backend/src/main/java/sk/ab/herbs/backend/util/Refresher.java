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

    private static String CELL_DELIMITER = ";";
    private static String FILTER_DELIMITER = "\\_";

    private static final String[] COLORS = {null, "white", "yellow", "red", "blue", "green"};
    private static final String[] COLORS_IDS = {null, "1", "2", "3", "4", "5"};
    private static final String[] HABITATS = {null, "meadows or grassland", "gardens or fields", "moorlands or wetlands", "woodlands or forests", "rocks or mountains", "trees or bushes"};
    private static final String[] HABITATS_IDS = {null, "1", "2", "3", "4", "5", "6"};
    private static final String[] PETALS = {null,"4 or less", "5", "Many", "Bisymetric"};
    private static final String[] PETALS_IDS = {null,"1", "2", "3", "4"};
    private static final String[] DISTRIBUTION = {null, "10", "11", "12", "13", "14", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "40", "41", "42", "43", "50", "51", "60", "61", "62", "63", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "90", "91"};

    private static final String[] FILTER_4_ATTRIBUTES = {Constants.COLOR_OF_FLOWERS, Constants.HABITAT, Constants.NUMBER_OF_PETALS, Constants.DISTRIBUTION};

    public static void main(String[] params) {
        countAndList4Ids();
        search();
        photoSearch();
    }

    private static void countAndList4Ids() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<Integer, Integer>> lists = new HashMap<>();

        List<String> filtersWith4Attributes = generateCountsAndLists4Ids();

        for (String filter : filtersWith4Attributes) {
            counts.put(filter, 0);
            lists.put(filter, new HashMap<Integer, Integer>());
        }

        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Call<List<String>> plantListCall = firebaseClient.getApiService().getPlantsToUpdate();
            List<String> plantsList = plantListCall.execute().body();

            int i = 0;
            for (String plantName : plantsList) {

                System.out.println(i + ": " + plantName);

                Call<PlantHeader> plantCall = firebaseClient.getApiService().getPlantHeader(i);
                PlantHeader plant = plantCall.execute().body();

                for (String filter : filtersWith4Attributes) {

                    String[] filterParts = filter.split(FILTER_DELIMITER, -1);

                    if ((filterParts[0].isEmpty() || plant.getFilterColor().contains(Integer.parseInt(filterParts[0])))
                            && (filterParts[1].isEmpty() || plant.getFilterHabitat().contains(Integer.parseInt(filterParts[1])))
                            && (filterParts[2].isEmpty() || plant.getFilterPetal().contains(Integer.parseInt(filterParts[2])))
                            && (filterParts[3].isEmpty() || plant.getFilterDistribution().contains(Integer.parseInt(filterParts[3])))) {
                        counts.put(filter, counts.get(filter) + 1);
                        lists.get(filter).put(i, 1);
                    }
                }
                i++;
            }

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
        String[] languages = {"bg", "cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "ko", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk", "zh"};
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Map<String, Integer> plants =  new HashMap<>();

            Call<List<String>> plantListCall = firebaseClient.getApiService().getPlantsToUpdate();
            List<String> plantsList = plantListCall.execute().body();

            int i = 0;
            for (String plantName : plantsList) {
                plants.put(plantName, i++);
            }

            for (String language : languages) {
                System.out.println(language);

                Map<String, Map<String, Object>> searchMap = new HashMap<>();

                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> translation = translationCall.execute().body();

                for (Map.Entry<String, Object> entry : translation.entrySet()) {
                    Integer plantName = plants.get(entry.getKey());
                    Map<String, Object> plant;
                    try {
                        plant = (Map<String, Object>) entry.getValue();
                    } catch (Exception ex) {
                        System.out.println(plantName);
                        continue;
                    }

                    Map<String, Object> searchForLabel = null;
                    String label = (String)plant.get("label");
                    if (label != null) {
                        label = label.toLowerCase();
                        if (label.isEmpty() || label.contains(".") || label.contains("/") || label.contains("#") || label.contains("$") || label.contains("[") || label.contains("]")) {
                            System.out.println(plantName);
                        }

                        searchForLabel = searchMap.get(label);
                        if (searchForLabel == null) {
                            searchForLabel = new HashMap<>();
                            searchForLabel.put("is_label", true);
                            searchMap.put(label, searchForLabel);
                        }
                        Map<Integer, Integer> plantList = (Map<Integer, Integer>)searchForLabel.get("list");
                        if (plantList == null) {
                            plantList = new HashMap<>();
                            searchForLabel.put("list", plantList);
                        }
                        plantList.put(plantName, 1);
                    }

                    List<String> names = (List<String>)plant.get("names");
                    if (names != null) {
                        for (String name : names) {
                            name = name.toLowerCase();
                            if (name.isEmpty() || name.contains(".") || name.contains("/") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]")) {
                                System.out.println(plantName);
                            }

                            searchForLabel = searchMap.get(name);
                            if (searchForLabel == null) {
                                searchForLabel = new HashMap<>();
                                searchMap.put(name, searchForLabel);
                            }
                            Map<Integer, Integer> plantList = (Map<Integer, Integer>)searchForLabel.get("list");
                            if (plantList == null) {
                                plantList =  new HashMap();
                                searchForLabel.put("list", plantList);
                            }
                            plantList.put(plantName, 1);
                        }
                    }
                }

                Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch(language, searchMap);
                Response<Object> response = callFirebaseSearch.execute();
                if (response.errorBody() != null) {
                    System.out.println(response.errorBody().string());
                }
            }

            // latin
            Map<String, Map<String, Object>> searchMap = new HashMap<>();

            for (String plantName : plantsList) {

                Integer plantId = plants.get(plantName);

                //System.out.println(plantLine[0]);

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantName);
                FirebasePlant plant = plantCall.execute().body();

                Map<String, Object> searchForLabel = null;
                String label = plant.getName();
                if (label != null) {
                    label = label.toLowerCase();

                    searchForLabel = searchMap.get(label);
                    if (searchForLabel == null) {
                        searchForLabel = new HashMap<>();
                        searchForLabel.put("is_label", true);
                        searchMap.put(label, searchForLabel);
                    }
                    Map<Integer, Integer> plantList = (Map<Integer, Integer>)searchForLabel.get("list");
                    if (plantList == null) {
                        plantList =  new HashMap();
                        searchForLabel.put("list", plantList);
                    }
                    plantList.put(plantId, 1);
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
                        Map<Integer, Integer> plantList = (Map<Integer, Integer>)searchForLabel.get("list");
                        if (plantList == null) {
                            plantList =  new HashMap();
                            searchForLabel.put("list", plantList);
                        }
                        plantList.put(plantId, 1);
                    }
                }
            }

            Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch("la", searchMap);
            Response<Object> response = callFirebaseSearch.execute();
        } catch (IOException ex) {

        }

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

    private static void photoSearch() {
        try {
            final FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map<String, Object>> apgivCall = firebaseClient.getApiService().getAPGIV3();
            Map<String, Object> apgiv = apgivCall.execute().body();
            Map<String, Map<String, Object>> photoSearch = new HashMap<>();

            parseAPGIV("", apgiv, photoSearch, "APG IV_v3/");

            parsePlants(photoSearch);

//            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().create();
//            System.out.println(gson.toJson(photoSearch));

            Call<Object> callFirebaseSearch = firebaseClient.getApiService().savePhotoSearch(photoSearch);
            Response<Object> response = callFirebaseSearch.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void parsePlants(Map<String, Map<String, Object>> photoSearch) {
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Call<List<String>> plantListCall = firebaseClient.getApiService().getPlantsToUpdate();
            List<String> plants = plantListCall.execute().body();

            for (String plantName : plants) {

                System.out.println(plantName);

                Map<String, Object> plant = new HashMap<>();
                plant.put("count", 1);
                plant.put("path", plantName);
                photoSearch.put(plantName.toLowerCase(), plant);

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantName);
                FirebasePlant firebasePlant = plantCall.execute().body();

                if (firebasePlant.getFreebaseId() != null) {
                    Map<String, Object> m = photoSearch.get("m");
                    if (m == null) {
                        m = new HashMap<>();
                        photoSearch.put("m",  m);
                    }
                    String[] freebaseIds = firebasePlant.getFreebaseId().split(",");
                    for (String freebaseId : freebaseIds) {
                        m.put(freebaseId.substring(freebaseId.lastIndexOf("/") + 1), plant);
                    }
                }

                if (firebasePlant.getSynonyms() != null) {
                    for (String synomym : firebasePlant.getSynonyms()) {
                        if (photoSearch.get(synomym.toLowerCase()) == null) {
                            Map<String, Object> plantSynonym = new HashMap<>();
                            plantSynonym.put("count", 1);
                            plantSynonym.put("path", plantName);
                            photoSearch.put(synomym.toLowerCase(), plantSynonym);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseAPGIV(String taxon, Map<String, Object> apgiv, Map<String, Map<String, Object>> photoSearch, String path) {

        Map<String, Object> item = new HashMap<>();
        boolean isDesiredType = false;
        List<String> freebaseId = new ArrayList<>();
        for (String key : apgiv.keySet()) {

            if ("type".equals(key)) {
                String type = (String)apgiv.get(key);
                isDesiredType = "Ordo".equals(type) || "Genus".equals(type) || "Familia".equals(type) || "Subfamilia".equals(type)
                        || "Subgenus".equals(type) || "Tribus".equals(type) || "Subtribus".equals(type) || "Sectio".equals(type)
                        || "Subsectio".equals(type) || "Serie".equals(type) || "Subserie".equals(type);
            } else if ("count".equals(key)) {
                String count = String.format("%.0f", apgiv.get(key));
                item.put("count", Integer.parseInt(count));
            } else if ("list".equals(key)) {
                item.put("path", path + "list");
            } else if ("freebase".equals(key)) {
                Object freebase = apgiv.get(key);
                if (freebase instanceof List) {
                    for (Object freebaseItem : (List)freebase) {
                        freebaseId.add((String) freebaseItem);
                    }
                } else {
                    freebaseId.add((String) apgiv.get(key));
                }
            } else {
                parseAPGIV(key, (Map<String, Object>)apgiv.get(key), photoSearch, path + key + "/");
            }
        }

        if (isDesiredType) {
            photoSearch.put(taxon.toLowerCase(), item);

            if (freebaseId != null) {
                Map<String, Object> m = photoSearch.get("m");
                if (m == null) {
                    m = new HashMap<>();
                    photoSearch.put("m",  m);
                }
                for (String freebaseItem : freebaseId) {
                    m.put(freebaseItem.substring(freebaseItem.lastIndexOf("/") + 1), item);
                }
            }
        }
    }

}
