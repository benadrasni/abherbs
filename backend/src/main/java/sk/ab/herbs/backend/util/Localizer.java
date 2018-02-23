package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private static String PATH_TO_APPBASE = "C:/Dev/Projects/abherbs/appbase/src/main/res";
    private static String PATH_TO_APP = "C:/Dev/Projects/abherbs/app/src/main/res";
    private static String PATH_TO_APPPLUS = "C:/Dev/Projects/abherbs/appplus/src/main/res";

    public static void main(String[] params) {
        Map<String, Map<String, Map<String, String>>> appTranslations = new TreeMap<>();
        appTranslations.put("appbase", processResDir(PATH_TO_APPBASE));
        appTranslations.put("app", processResDir(PATH_TO_APP));
        appTranslations.put("appplus", processResDir(PATH_TO_APPPLUS));

        try {
            FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map> callFirebaseTranslationsApp = firebaseClient.getApiService().saveTranslationsApp(appTranslations);
            callFirebaseTranslationsApp.execute().body();
        } catch (IOException ex) {

        }
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
}
