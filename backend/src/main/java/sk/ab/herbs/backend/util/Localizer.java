package sk.ab.herbs.backend.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import retrofit2.Call;
import sk.ab.common.service.FirebaseClient;

/**
 *
 * Created by adrian on 22. 2. 2018.
 */

public class Localizer {
    private static String PATH_TO_APPBASE = "D:/Dev/Projects/abherbs/appbase/src/main/res";
    private static String PATH_TO_APP = "D:/Dev/Projects/abherbs/app/src/main/res";
    //private static String PATH_TO_FLUTTER_APP = "/home/adrian/StudioProjects/abherbs_flutter/lib/l10n";
    private static String PATH_TO_FLUTTER_APP = "D:/Dev/Projects/abherbs_flutter/lib/l10n";
    private static String PATH_TO_APPPLUS = "D:/Dev/Projects/abherbs/appplus/src/main/res";

    public static void main(String[] params) {
        Map<String, Map<String, Map<String, String>>> appTranslations = new TreeMap<>();
        //appTranslations.put("appbase", processResDir(PATH_TO_APPBASE));
        //appTranslations.put("app", processResDir(PATH_TO_APP));
        appTranslations.put("app", processResJsonDir(PATH_TO_FLUTTER_APP));
        //appTranslations.put("appplus", processResDir(PATH_TO_APPPLUS));
        appTranslations.put("web", processFirebaseNode());

        try {
            FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map> callFirebaseTranslationsApp = firebaseClient.getApiService().saveTranslationsApp(appTranslations);
            callFirebaseTranslationsApp.execute().body();
        } catch (IOException ex) {

        }
    }

    private static Map<String, Map<String, String>> processResJsonDir(String path) {
        Map<String, Map<String, String>> appTranslations = new TreeMap<>();

        try {
            File dir = new File(path);
            for (File resource : dir.listFiles()) {
                String resourceName = resource.getName();

                if (resourceName == "intl_en_US.arb" || resourceName == "intl_en_UK.arb") continue;

                String language = resourceName.substring(5,7);
                String content = new String(Files.readAllBytes(Paths.get(resource.getAbsolutePath())), "UTF-8");

                System.out.println(resourceName);

                JsonParser parser = new JsonParser();
                JsonObject labels = parser.parse(content).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : labels.entrySet()) {
                    String value = entry.getValue().getAsString();

                    Map<String, String> translation = appTranslations.get(entry.getKey());
                    if (translation == null) {
                        translation = new HashMap<>();
                        appTranslations.put(entry.getKey(), translation);
                    }
                    translation.put(language, value);
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return appTranslations;
    }

    private static Map<String, Map<String, String>> processResDir(String path) {
        Map<String, Map<String, String>> appTranslations = new TreeMap<>();
        File dir = new File(path);
        for (File resource : dir.listFiles()) {
            String resourceName = resource.getName();
            if (resourceName.startsWith("values")) {
                String language = "en";
                if (resourceName.indexOf("-") > -1) {
                    language = resourceName.substring(resourceName.indexOf("-") + 1, resourceName.length());
                }
                File stringsRes = new File(resource.getAbsolutePath() + "/strings.xml");
                if (stringsRes.exists()) {

                    try {
                        Scanner scan = new Scanner(stringsRes);
                        while(scan.hasNextLine()) {
                            String line = scan.nextLine().trim();
                            if (line.startsWith("<string ") && line.indexOf("translatable=\"false\"") == -1) {
                                String key = line.substring(14, line.indexOf("\"", 14));
                                String value = line.substring(line.indexOf(">") + 1, line.indexOf("</string>"));

                                Map<String, String> translation = appTranslations.get(key);
                                if (translation == null) {
                                    translation = new HashMap<>();
                                    appTranslations.put(key, translation);
                                }
                                translation.put(language, value);
                            }
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return appTranslations;
    }

    private static Map<String,Map<String,String>> processFirebaseNode() {
        Map<String, Map<String, String>> appTranslations = new TreeMap<>();

        try {
            FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map<String, Map<String, String>>> callFirebaseWebTranslations = firebaseClient.getApiService().getWebTranslations();
            Map<String, Map<String, String>> translations = callFirebaseWebTranslations.execute().body();

            for (String langKey : translations.keySet()) {
                Map<String, String> langValue = translations.get(langKey);
                for(String itemKey : langValue.keySet()) {
                    String value = langValue.get(itemKey);
                    Map<String, String> appTranslation = appTranslations.get(itemKey);
                    if (appTranslation == null) {
                        appTranslation = new HashMap<>();
                        appTranslations.put(itemKey, appTranslation);
                    }
                    appTranslation.put(langKey, value);
                }
            }

        } catch (IOException ex) {

        }

        return appTranslations;
    }
}
