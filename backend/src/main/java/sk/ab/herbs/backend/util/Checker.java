package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.service.FirebaseClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Checker {
    public static int PLANTS_COUNT = 937;

    public static String PATH = "D:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv";
    public static String MISSING_FILE_SUFFIX = "_missing.txt";

    private static ArrayList<Integer> DEFAULT_DISTRIBUTION = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14));
    private static ArrayList<Integer> DISTRIBUTION = new ArrayList<>(Arrays.asList(10, 11, 12, 13, 14, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 40, 41, 42, 43, 50, 51, 60, 61, 62, 63, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 90, 91));

    private static String[] languages = {"bg", "cs", "da", "de", "en", "es", "et", "fi", "fr", "hr", "hu", "it", "ja", "ko", "lt", "lv", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "uk"};
    private static String[] languagesWithLabel = {"ar", "bg", "cs", "da", "de", "el", "en", "es", "et", "fa", "fi", "fr", "he", "hr", "hu", "is", "it", "ja", "ko", "lt", "lv", "mt", "nl", "no", "pa", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "tr", "uk", "zh"};

    public static String CELL_DELIMITER = ";";
    public static String ALIAS_DELIMITER = ",";

    public static void main(String[] params) {

        //checkNames();
        //checkNameTranslations();
        //checkPlantTranslation();
        //checkTranslation();
        //checkFilter();
        checkSources();
        //addIds();
        //checkFruit();
    }

    private static void checkFilter() {
        System.out.println("########### Checking filter ##############");

        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            for (int i = 0; i < PLANTS_COUNT; i++) {

                Call<PlantHeader> plantHeaderCall = firebaseClient.getApiService().getPlantHeader(i);
                PlantHeader plantHeader = plantHeaderCall.execute().body();

                for(Integer color : plantHeader.getFilterColor()) {
                    if (color < 1 || color > 5) {
                        System.out.println("ERROR at " + i + "(color: " + color + ")");
                    }
                }

                for(Integer habitat : plantHeader.getFilterHabitat()) {
                    if (habitat < 1 || habitat > 8) {
                        System.out.println("ERROR at " + i + "(habitat: " + habitat + ")");
                    }
                }

                for(Integer petal : plantHeader.getFilterPetal()) {
                    if (petal < 1 || petal > 4) {
                        System.out.println("ERROR at " + i + "(petal: " + petal + ")");
                    }
                }

                for(Integer distribution : plantHeader.getFilterDistribution()) {
                    if (!DISTRIBUTION.contains(distribution)) {
                        System.out.println("ERROR at " + i + "(distribution: " + distribution + ")");
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkNames() {
        System.out.println("########### Checking names ##############");
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

    private static void addIds() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            int i = 0;
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                //System.out.println(plantLine[0]);

                Call<Integer> savePlantId = firebaseClient.getApiService().savePlantId(plantLine[0], i);
                savePlantId.execute();

                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkNameTranslations() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            PrintWriter writerSummary = new PrintWriter(PATH + "count_missing_new.txt", "UTF-8");

            for (String language : languagesWithLabel) {
                int count = 0;
                PrintWriter writer = new PrintWriter(PATH + language + "_missing.txt", "UTF-8");

                Scanner scan = new Scanner(file);
                while (scan.hasNextLine()) {

                    final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                    //System.out.println(plantLine[0]);

                    if (checkNameTranslations(firebaseClient, writer, language, plantLine[0])) {
                        count++;
                    }
                }
                scan.close();

                writer.close();

                writerSummary.println(language + "..." + count);
            }

            writerSummary.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkFruit() {
        System.out.println("########### Checking fruit ##############");

        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
                Scanner scan = new Scanner(file);
                while (scan.hasNextLine()) {

                    final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                    //System.out.println(plantLine[0]);

                    checkFruitTranslations(firebaseClient,"sk", plantLine[0]);
                }
                scan.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkPlantTranslation() {
        System.out.println("########### Checking plant translation ##############");

        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            for (String language : languagesWithLabel) {

                Call<Map<String, Object>> searchCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> search = searchCall.execute().body();

                for (Map.Entry<String, Object> entry : search.entrySet()) {
                    Object plant = entry.getValue();

                    if (plant instanceof List) {
                        System.out.println("!!!!!!" + language + ": " + entry.getKey() + "!!!!!!");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkSources() {
        System.out.println("########### Checking sources ##############");

        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            for (String language : languages) {
                System.out.println(language);

                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> translation = translationCall.execute().body();

                for (Map.Entry<String, Object> entry : translation.entrySet()) {
                    Map<String, Object> plant = (Map<String, Object>) entry.getValue();

                    if (plant.get("sourceUrls") != null) {
                        for (String url : (List<String>) plant.get("sourceUrls")) {
                            if (!url.contains("//") || url.indexOf("/", url.indexOf("//") + 2) == -1) {
                                System.out.println("language: " + language + ", plant: " + plant.get("label") + ",  src: " + url);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(PATH + PLANTS_FILE);
        String plantName = "";
        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                plantName = plantLine[0];
                System.out.println(plantName);
                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = plantCall.execute().body();

                if (plant.getSourceUrls() != null) {
                    for (String url : plant.getSourceUrls()) {
                        if (!url.contains("//") || url.indexOf("/", url.indexOf("//") + 2) == -1) {
                            System.out.println("plant: " + plant.getName() + ",  src: " + url);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(plantName);
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

    private static boolean checkNameTranslations(FirebaseClient firebaseClient, PrintWriter writer, String language, String plantName) {
        try {
            Call<Map<String, Object>> plantTranslationCall = firebaseClient.getApiService().getTranslation(language, plantName);
            Map<String, Object> plantTranslation = plantTranslationCall.execute().body();

            if (plantTranslation == null || plantTranslation.get("label") == null) {
                writer.println(plantName);
                return true;
            }
        } catch (Exception ex) {
            System.out.println(language + " - " + plantName);
        }
        return false;
    }

    private static boolean checkFruitTranslations(FirebaseClient firebaseClient, String language, String plantName) {
        try {
            Call<Map<String, Object>> plantTranslationCall = firebaseClient.getApiService().getTranslation(language, plantName);
            Map<String, Object> plantTranslation = plantTranslationCall.execute().body();

            if (plantTranslation == null || plantTranslation.get("fruit") == null) {
                System.out.println(plantName);
                return true;
            }
        } catch (Exception ex) {
            System.out.println(language + " - " + plantName);
        }
        return false;
    }

    private static void checkTranslation() {
        System.out.println("########### Checking translation ##############");

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
