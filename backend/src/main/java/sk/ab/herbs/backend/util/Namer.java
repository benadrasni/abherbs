package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.service.FirebaseClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Namer {
    public static String PATH = "C:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_NAMES_FILE = "plant_names.csv";
    public static String PLANTS_FILE = "plants.csv";

    public static String CELL_DELIMITER = ",";
    public static String ALIAS_DELIMITER = "\\|";

    public static void main(String[] params) {

        //addNameTranslations();
    }

    private static void addNameTranslations() {
        File file = new File(PATH + PLANTS_NAMES_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            String[] languages = scan.nextLine().split(CELL_DELIMITER);

            int j = 0;
            while(scan.hasNextLine()) {
                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                j++;
                if (j < 890) continue;

                System.out.println(plantLine[0]);

                for (int i=2; i < languages.length; i++) {
                    //System.out.println(plantLine[0] + " - " + languages[i]);
                    if (i < plantLine.length && plantLine[i] != null && !plantLine[i].isEmpty()) {
                        addNameTranslations(firebaseClient, plantLine[0], languages[i].substring(9), plantLine[i]);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasJapanese(CharSequence charSequence) {
        boolean hasJapanese = false;
        for (char c : charSequence.toString().toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                    || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION) {
                hasJapanese = true;
                break;
            }
        }

        return hasJapanese;
    }

    private static void addNameTranslations(FirebaseClient firebaseClient, String plantName, String language, String namesAll) throws IOException {
        boolean shouldSaveLabel = false;
        boolean shouldSaveNames = false;
        List<String> names = new ArrayList<>();

        for (String name : Arrays.asList(namesAll.split(ALIAS_DELIMITER))) {
            if ("ja".equals(language) && hasJapanese(name)) {
                names.add(name);
            } else if (!"ja".equals(language)) {
                names.add(name);
            }
        }

        Call<Map<String, Object>> plantTranslationCall = firebaseClient.getApiService().getTranslation(language, plantName);
        Map<String, Object> plantTranslation = plantTranslationCall.execute().body();

        if (plantTranslation == null) {
            plantTranslation = new HashMap<>();
        }

        String currentLabel = (String)plantTranslation.get("label");
        if (currentLabel == null || ("ja".equals(language) && !hasJapanese(currentLabel))) {
            if (names.size() > 0) {
                plantTranslation.put("label", names.remove(0));
                shouldSaveLabel = true;
            }
        } else {
            if ("he".equals(language)) {
                System.out.println(plantName + ": " + currentLabel);
            }
            int i;
            boolean exists = false;
            for (i = 0; i < names.size(); i++) {
                if (names.get(i).toLowerCase().equals(currentLabel.toLowerCase())) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                names.remove(i);
            }
        }

        List<String> currentNames = (List<String>)plantTranslation.get("names");
        if (currentNames == null) {
            currentNames = new ArrayList<>();
        }

        Set<String> namesSet = new LinkedHashSet<>();
        for (String currentName : currentNames) {
            if (currentName.equals(currentLabel)) {
                shouldSaveNames = true;
                continue;
            }
            if ("ja".equals(language) && hasJapanese(currentName)) {
                namesSet.add(currentName);
            } else if (!"ja".equals(language)) {
                namesSet.add(currentName);
            } else {
                shouldSaveNames = true;
            }
        }
        for (String name : names) {
            boolean exists = false;
            for (String currentName : namesSet) {
                if (name.toLowerCase().equals(currentName.toLowerCase())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                namesSet.add(name);
                shouldSaveNames = true;
            }
        }

        if (shouldSaveNames) {
            plantTranslation.put("names", Arrays.asList(namesSet.toArray()));
        }

        if (shouldSaveLabel || shouldSaveNames) {
            Call<Object> saveTranslationCall = firebaseClient.getApiService().saveTranslation(language, plantName, plantTranslation);
            saveTranslationCall.execute();

            if (shouldSaveLabel) {
                System.out.println(plantName + ": " + language + " - " + plantTranslation.get("label"));
            }
        }

   }
}
