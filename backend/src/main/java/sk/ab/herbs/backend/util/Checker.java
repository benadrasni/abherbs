package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.service.FirebaseClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Checker {
    public static String PATH = "C:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv";
    public static String MISSING_FILE_SUFFIX = "_missing.txt";

    private static ArrayList<Integer> DEFAULT_DISTRIBUTION = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14));

    private static String[] languages = {"cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};

    public static String CELL_DELIMITER = ";";
    public static String ALIAS_DELIMITER = ",";

    public static void main(String[] params) {

        //checkNames();
        checkNameTranslations();
        //checkSearch();
        //checkTranslation();
    }

    private static void checkNames() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                //System.out.println(plantLine[0]);

                check(firebaseClient, plantLine[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkNameTranslations() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                //System.out.println(plantLine[0]);

                checkNameTranslations(firebaseClient, plantLine[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkSearch() {
        final FirebaseClient firebaseClient = new FirebaseClient();

        List<String> plants = new ArrayList<>();
        File file = new File(PATH + PLANTS_FILE);
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                plants.add(plantLine[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (String language : languages) {
                System.out.println(language);

                Call<Map<String, Object>> searchCall = firebaseClient.getApiService().getSearch(language);
                Map<String, Object> search = searchCall.execute().body();

                for (Map.Entry<String, Object> entry : search.entrySet()) {
                    Map<String, Boolean> plantNames = (Map<String, Boolean>) entry.getValue();

                    for (Map.Entry<String, Boolean> entry1 : plantNames.entrySet()) {
                        String plantName = entry1.getKey();
                        if (!plants.contains(plantName)) {
                            System.out.println("!!!!!!" + plantName + "!!!!!!");
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void check(FirebaseClient firebaseClient, String plantName) throws IOException {
        Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantName);
        FirebasePlant plant = plantCall.execute().body();

        if (!plantName.equals(plant.getName())) {
            System.out.println("!!!!!!" + plantName + "!!!!!!");
        }
    }

    private static void checkNameTranslations(FirebaseClient firebaseClient, String plantName) throws IOException {
        String[] languages = {"pl"};
        for (String language : languages) {

            Call<Map<String, Object>> plantTranslationCall = firebaseClient.getApiService().getTranslation(language, plantName);
            Map<String, Object> plantTranslation = plantTranslationCall.execute().body();

            if (plantTranslation == null || plantTranslation.get("label") == null) {
                System.out.println(plantName);
            }
        }
    }

    private static void checkTranslation() {
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            for (String language : languages) {

                Call<Map<String, Object>> searchCall = firebaseClient.getApiService().getTranslation(language + "-GT");
                Map<String, Object> search = searchCall.execute().body();

                if (search != null) {
                    System.out.println(language + ": " + search.entrySet().size());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
