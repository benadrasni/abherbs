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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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

            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "Plants.csv");
            int i = 0;

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                i++;
                final String[] plantLine = scan.nextLine().split(",");
                if (i < 425) continue;

                Document docPost = Jsoup.connect("http://www.plantarium.ru/page/search.html?match=begins&type=0&mode=full&sample=" + plantLine[0]).timeout(10*1000).get();

                String name = null;
                StringBuilder names = new StringBuilder();
                String nameLatin = null;

                Elements searchResult = docPost.getElementsByTag("a");
                for (Element link : searchResult) {
                    if (link.attr("href").startsWith("/page/view/item/")) {

                        Thread.sleep(3000);
                        Document docPlant = Jsoup.connect("http://www.plantarium.ru" + link.attr("href")).timeout(10*1000).get();

                        name = "";
                        names = new StringBuilder();
                        nameLatin = "";

                        Elements taxonNames = docPlant.getElementsByClass("taxon-name");
                        if (taxonNames.size() > 1) {
                            nameLatin = taxonNames.get(0).text() + " " + taxonNames.get(1).text();
                        }

                        System.out.println(plantLine[0] + " ... " + nameLatin);

                        if (plantLine[0].equals(nameLatin)) {
                            Elements ruNames = docPlant.getElementsByAttributeValue("id", "boxRusNamesList");
                            if (ruNames.size() > 0) {
                                Elements nameElements = ruNames.get(0).getElementsByTag("span");
                                if (nameElements.size() > 0) {
                                    name = nameElements.get(0).text();
                                    if (nameElements.size() > 1) {
                                        for (int j = 1; j < nameElements.size(); j++) {
                                            if (names.length() > 0) {
                                                names.append(",");
                                            }
                                            names.append(nameElements.get(j).text());
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    }
                }

                if (name != null && name.length() > 0) {

                    Call<Plant> callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_ru", name, "replace", "string");
                    callCloud.execute();

                    if (names.length() > 0) {
                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_ru", names.toString(), "replace", "list");
                        callCloud.execute();
                    }

                }

                Thread.sleep(3000);
            }


//            final HerbCloudClient herbCloudClient = new HerbCloudClient();
//
//            File file = new File(PATH + "Plants.csv");
//
//            Scanner scan = new Scanner(file);
//            while(scan.hasNextLine()){
//
//                final String[] plantLine = scan.nextLine().split(",");
//
//                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(plantLine[0]);
//
//                Plant plant = callCloud.execute().body();
//
//                String valueLabel = plant.getLabel().get("ru");
//
//                String label = valueLabel; //langNames.get(plantLine[0]);
//
//                if (label == null && plant.getSynonyms() != null) {
//                    for(String synonym : plant.getSynonyms()) {
//                        label = langNames.get(synonym);
//                        if (label != null) {
//                            break;
//                        }
//                    }
//                }

//                if (label != null) {
//                    if (valueName == null) {
//                        label = valueLabel.toLowerCase();
//                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_pl", label, "replace", "string");
//
//                        callCloud.execute();
//                    }
//                } else {
//                    if (valueLabel == null) {
//                        System.out.println(plantLine[0]);
//                    }
//                }

//                ArrayList<String> synonyms = plant.getSynonyms();
//
//                ArrayList<String> valueNames = plant.getNames().get("pl");
//                if (valueNames != null) {
//                    StringBuilder names = new StringBuilder();
//                    for(String name : valueNames) {
//                        if (names.length() > 0) {
//                            names.append(",");
//                        }
//                        boolean isSynonym = false;
//                        for (String synonym : synonyms) {
//                            if (name.toLowerCase().equals(synonym.toLowerCase())) {
//                                isSynonym = true;
//                                break;
//                            }
//                        }
//
//                        if (!isSynonym) {
//                            names.append(name.toLowerCase());
//                        }
//                    }
//                    if (names.length() > 0) {
//                        System.out.println(plant.getLabel().get("la") + "......." + names.toString());
//                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_pl", names.toString(), "replace", "list");
//
//                        callCloud.execute();
//                    }
//                }

//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
