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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public class Updater {
    public static int PLANTS_COUNT = 937;

    public static String PATH = "C:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv";

    public static String CELL_DELIMITER = ";";

    public static void main(String[] params) {

        //addFreebaseIds();
        addIds();
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

    private static void addFreebaseIds() {
        try {
            final FirebaseClient firebaseClient = new FirebaseClient();
            Call<Map<String, Object>> apgivCall = firebaseClient.getApiService().getAPGIV2();
            Map<String, Object> apgiv = apgivCall.execute().body();

            //parseAPGIV(firebaseClient,"", apgiv,"APG IV_v3/");
            parsePlants();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseAPGIV(FirebaseClient firebaseClient, String taxon, Map<String, Object> apgiv, String path) {

        boolean isDesiredType = false;
        for (String key : apgiv.keySet()) {
            if ("type".equals(key)) {
                String type = (String)apgiv.get(key);
                isDesiredType = "Ordo".equals(type) || "Genus".equals(type) || "Familia".equals(type) || "Subfamilia".equals(type)
                        || "Tribus".equals(type) || "Subtribus".equals(type);
            } else if ("count".equals(key)) {
                continue;
            } else if ("list".equals(key)) {
                continue;
            } else {
                parseAPGIV(firebaseClient, key, (Map<String, Object>)apgiv.get(key), path + key + "/");
            }
        }

        if (isDesiredType) {
            try {
                Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + taxon).get();
                String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

                if (wikiPage.lastIndexOf("/") > -1) {
                    String wikiData = wikiPage.substring(wikiPage.lastIndexOf("/") + 1, wikiPage.indexOf("#"));

                    URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikiData + ".json");
                    HttpURLConnection request = (HttpURLConnection) url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                    JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(wikiData);

                    JsonObject claims = wikidata.getAsJsonObject("claims");

                    if (claims.get("P646") != null) {

                        String freebaseId = claims.get("P646").getAsJsonArray().get(0).getAsJsonObject().get("mainsnak").getAsJsonObject().get("datavalue").getAsJsonObject().get("value").getAsString();

                        Call<String> saveFreebaseId = firebaseClient.getApiService().saveStringAttribute(path, "freebase", freebaseId);
                        saveFreebaseId.execute();

                        System.out.println(taxon + ": " + freebaseId);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parsePlants() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = plantCall.execute().body();


                Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + plantLine[1]).get();
                String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

                if (wikiPage.lastIndexOf("/") > -1) {
                    String wikiData = wikiPage.substring(wikiPage.lastIndexOf("/") + 1, wikiPage.indexOf("#"));
                    plant.getWikilinks().put("data", "https://www.wikidata.org/wiki/" + wikiData);

                    URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + wikiData + ".json");
                    HttpURLConnection request = (HttpURLConnection) url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                    JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(wikiData);

                    JsonObject claims = wikidata.getAsJsonObject("claims");

                    if (claims.get("P646") != null) {

                        String freebaseId = claims.get("P646").getAsJsonArray().get(0).getAsJsonObject().get("mainsnak").getAsJsonObject().get("datavalue").getAsJsonObject().get("value").getAsString();
                        plant.setFreebaseId(freebaseId);

                        Call<FirebasePlant> savePlant = firebaseClient.getApiService().savePlant(plantLine[0], plant);
                        savePlant.execute();

                        System.out.println(plantLine[0] + ": " + freebaseId);
                    }
                }



            }
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
