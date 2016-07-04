package sk.ab.herbs.backend.util;

import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.util.TreeMap;

import retrofit.Call;
import sk.ab.common.entity.Plant;
import sk.ab.common.service.HerbCloudClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Updater {
    public static String PATH = "C:/Development/Projects/abherbs/backend/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/";


    public static void main(String[] params) {

        try {
            Map<String, String> langNames = new HashMap<>();

//            File namefile = new File("C:/Development/Projects/uknames.csv");
//
//            Scanner namescan = new Scanner(namefile);
//            while(namescan.hasNextLine()) {
//                final String[] plantLine = namescan.nextLine().split(",");
//
//                if (plantLine.length > 1) {
//                    langNames.put(plantLine[0], plantLine[1]);
//                }
//            }

//            PrintWriter writer = new PrintWriter("C:/Development/Projects/uknames.csv", "UTF-8");
//
//            for(int i = 1; i < 398; i++) {
//                System.out.println(i);
//                Document doc = Jsoup.connect("http://econtsh.astra.in.ua/system.php?page=" + i + "&lang=ua&filterfield=&filter=&gerbar=").get();
//
//                Elements table = doc.getElementsByClass("tbl");
//
//                Elements elements = table.get(0).getElementsByTag("tr");
//                int j = -1;
//                for (Element element : elements) {
//                    j++;
//                    if (j==0) continue;
//                    Elements tds = element.getElementsByTag("td");
//
//                    String nameLatin = "";
//                    Element nameLatinElement = tds.get(1);
//                    Elements anchors = nameLatinElement.getElementsByTag("a");
//                    if (anchors.size() > 0) {
//                        nameLatin = anchors.get(0).text();
//                    } else {
//                        nameLatin = nameLatinElement.text();
//                    }
//
//                    String[] hlp = nameLatin.split(" ");
//
//                    if (hlp.length > 1) {
//                        nameLatin = hlp[0] + " " + hlp[1];
//                    }
//
//                    //langNames.put(nameLatin, tds.get(2).text());
//                    writer.println(nameLatin + "," + tds.get(2).text());
//                }
//            }
//
//            writer.close();

            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "Plants.csv");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){

                final String[] plantLine = scan.nextLine().split(",");

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(plantLine[0]);

                Plant plant = callCloud.execute().body();

//                String valueLabel = plant.getLabel().get("pl");
//
//                String label = valueLabel; //langNames.get(plantLine[0]);
//
//                if (label == null && plant.getSynonyms() != null) {
//                    for(String synonym : plant.getSynonyms()) {
//                        label = names.get(synonym);
//                        if (label != null) {
//                            break;
//                        }
//                    }
//                }
//
//                if (label != null) {
////                    if (valueName == null) {
//                        label = valueLabel.toLowerCase();
//                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_pl", label, "replace", "string");
//
//                        callCloud.execute();
////                    }
//                } else {
//                    if (valueLabel == null) {
//                        System.out.println(plantLine[0]);
//                    }
//                }

                ArrayList<String> synonyms = plant.getSynonyms();

                ArrayList<String> valueNames = plant.getNames().get("pl");
                if (valueNames != null) {
                    StringBuilder names = new StringBuilder();
                    for(String name : valueNames) {
                        if (names.length() > 0) {
                            names.append(",");
                        }
                        boolean isSynonym = false;
                        for (String synonym : synonyms) {
                            if (name.toLowerCase().equals(synonym.toLowerCase())) {
                                isSynonym = true;
                                break;
                            }
                        }

                        if (!isSynonym) {
                            names.append(name.toLowerCase());
                        }
                    }
                    if (names.length() > 0) {
                        System.out.println(plant.getLabel().get("la") + "......." + names.toString());
                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_pl", names.toString(), "replace", "list");

                        callCloud.execute();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
