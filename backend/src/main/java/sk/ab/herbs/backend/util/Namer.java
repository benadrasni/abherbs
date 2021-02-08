package sk.ab.herbs.backend.util;

import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public static String PATH = "D:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_NAMES_FILE = "plant_names.csv";
    public static String PLANTS_FILE = "plants.csv";

    public static String CELL_DELIMITER = ",";
    public static String ALIAS_DELIMITER = "\\|";

    public static void main(String[] params) {

        //addNameTranslations();
        //addGoogleNameTranslations("ko");
        addTaxonTranslation("Gloriosa", "Gloriosa");
    }

    private static void addTaxonTranslation(String taxon, String wikiname) {
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + wikiname).get();

            String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

            if (wikiPage.lastIndexOf("/") > -1) {
                String wikiData = wikiPage.substring(wikiPage.lastIndexOf("/") + 1, wikiPage.indexOf("#"));

                URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikiData + ".json");
                HttpURLConnection request = (HttpURLConnection) url.openConnection();

                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(wikiData);

                JsonObject labels = wikidata.getAsJsonObject("labels");
                for (Map.Entry<String, JsonElement> label : labels.entrySet()) {
                    String language = label.getKey();
                    String value = label.getValue().getAsJsonObject().get("value").getAsString();

                    System.out.println(language + ": " + value);

                    if (!value.equals(taxon)) {
                        Call<List<String>> plantTranslationCall = firebaseClient.getApiService().getTranslationTaxonomy(language, taxon);
                        List<String> taxonTranslation = plantTranslationCall.execute().body();
                        if (taxonTranslation == null) {
                            taxonTranslation = new ArrayList<>();
                        }

                        if (!taxonTranslation.contains(value) && !taxonTranslation.contains(value.toLowerCase())) {
                            taxonTranslation.add(language.equals("de") ? value : value.toLowerCase());

                            Call<Object> saveTaxon = firebaseClient.getApiService().saveTranslationTaxonomy(language, taxon, taxonTranslation);
                            saveTaxon.execute();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

    private static void addGoogleNameTranslations(String language) {
        File file = new File(PATH + language + "_missing.txt");
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()) {
                final String plantName = scan.nextLine();
                System.out.println(plantName);
                addGoogleNameTranslation(firebaseClient, plantName, language);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addGoogleNameTranslation(FirebaseClient firebaseClient, String planName, String language) {
        try {
            Document doc = Jsoup.connect("https://www.google.com/search?lr=lang_" + language + "&q=" + planName + "&gs_lcp=CgZwc3ktYWIQAzICCAAyAggAMgIIADIGCAAQFhAeMgYIABAWEB4yBggAEBYQHjIGCAAQFhAeMgYIABAWEB4yBggAEBYQHjIGCAAQFhAeOgQIIxAnOgQIABAeOgYIABAIEB5Q8ihY8ihgiS5oAHAAeACAAVOIAaABkgEBMpgBAKABAqABAaoBB2d3cy13aXo&sclient=psy-ab&ved=0ahUKEwi7ru-brMroAhUKKqwKHQZkBy4Q4dUDCAs&uact=5").get();
            String name = doc.getElementsByClass("SPZz6b").val();

            System.out.println(name);

        } catch (Exception ex) {
            System.out.println(language + " - " + planName);
        }
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
