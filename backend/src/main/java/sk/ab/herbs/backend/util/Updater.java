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


        luontoportti("sv");
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


    }

    private static void luontoportti(String language) {

        Map<String, String> labels = new HashMap<>();
        Map<String, List<String>> aliases = new HashMap<>();

        try {
            Document docList = Jsoup.connect("http://www.luontoportti.com/suomi/" + language + "/kukkakasvit/?list=9").get();

            Elements textList = docList.getElementsByAttributeValue("id", "textList");
            if (textList.size() > 0) {
                Elements as = textList.get(0).getElementsByTag("a");
                for(Element a : as) {
                    String name = a.text();

                    Document plantDoc = Jsoup.connect(a.attr("href")).get();

                    Elements h4s = plantDoc.getElementsByTag("h4");
                    String nameLatin = null;
                    if (h4s.size() > 0) {
                        nameLatin = h4s.get(0).text();
                    }

                    labels.put(nameLatin, name);

                    String alias = "";
                    Elements texts = plantDoc.getElementsByAttributeValue("id", "teksti");
                    if (texts.size() > 0) {
                        Elements lis = texts.get(0).getElementsByTag("li");
                        for(Element li : lis) {
                            if (li.text().startsWith("Synonym:")) {
                                alias = li.text().substring(9).trim();

                                aliases.put(nameLatin, Arrays.asList(alias.split(",")));
                                break;
                            }
                        }
                    }

                    System.out.println(nameLatin + ";" + name + ";" + alias);
                }
            }

//            File namefile = new File(PATH + langauge + "names.csv");
//
//            Scanner namescan = new Scanner(namefile);
//            while(namescan.hasNextLine()) {
//                final String[] plantLine = namescan.nextLine().split(";");
//
//                if (plantLine.length > 1) {
//                    labels.put(plantLine[0], plantLine[1]);
//                    if (plantLine.length > 2 && plantLine[2].length() > 0) {
//                        aliases.put(plantLine[0], Arrays.asList(plantLine[2].split(",")));
//                    }
//                }
//            }
//
//            final HerbCloudClient herbCloudClient = new HerbCloudClient();
//
//            File file = new File(PATH + "Plants.csv");
//
//            Scanner scan = new Scanner(file);
//            while(scan.hasNextLine()) {
//
//                final String[] plantLine = scan.nextLine().split(",");
//                String nameLatin = plantLine[0];
//
//                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(nameLatin);
//                Plant plant = callCloud.execute().body();
//
//                String valueLabel = plant.getLabel().get(language);
//                String label = labels.get(nameLatin);
//                if (label == null && plant.getSynonyms() != null) {
//                    for (String synonym : plant.getSynonyms()) {
//                        label = labels.get(synonym);
//                        if (label != null) {
//                            break;
//                        }
//                    }
//                }
//
//                List<String> aliasesToSave = new ArrayList<>();
//
//                if (label != null && valueLabel != null && !label.equals(valueLabel.substring(0,1).toUpperCase()+valueLabel.substring(1))) {
//                    aliasesToSave.add(valueLabel.substring(0,1).toUpperCase()+valueLabel.substring(1));
//                }
//
//                ArrayList<String> valueAlias = plant.getNames().get(language);
//                if (valueAlias != null) {
//                    for (String al : valueAlias) {
//                        boolean isSynonym = al.startsWith(nameLatin);
//                        if (!isSynonym) {
//                            for (String synonym : plant.getSynonyms()) {
//                                isSynonym = al.startsWith(synonym);
//                                if (isSynonym) break;
//                            }
//                        }
//                        if (!isSynonym) {
//                            al = al.substring(0,1).toUpperCase()+al.substring(1);
//                            if ((label == null && !valueLabel.equals(al)) || !label.equals(al)) {
//                                aliasesToSave.add(al);
//                            }
//                        }
//                    }
//                }
//                List<String> alias = aliases.get(nameLatin);
//                if (alias != null) {
//                    for (String al : alias) {
//                        al = al.substring(0,1).toUpperCase()+al.substring(1);
//                        if (!aliasesToSave.contains(al)) {
//                            if ((label == null && !valueLabel.equals(al)) || !label.equals(al)) {
//                                aliasesToSave.add(al);
//                            }
//                        }
//                    }
//                }
//
//                StringBuilder aliasSb = new StringBuilder();
//                for (String al : aliasesToSave) {
//                    if (aliasSb.length() > 0) {
//                        aliasSb.append(",");
//                    }
//                    aliasSb.append(al);
//                }
//
//                if (label != null) {
//                    if (!label.equals(valueLabel)) {
//                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_" + language, label, "replace", "string");
//                        callCloud.execute();
//                    }
//                } else {
//                    if (valueLabel == null) {
//                        System.out.println(plantLine[0]);
//                    }
//                }
//
//                if (aliasSb.length() > 0) {
//                    StringBuilder valAlias = new StringBuilder();
//                    if (valueAlias != null) {
//                        for (String al : valueAlias) {
//                            if (valAlias.length() > 0) {
//                                valAlias.append(",");
//                            }
//                            valAlias.append(al);
//                        }
//                    }
//
//                    String s = aliasSb.toString();
//                    if (!s.equals(valAlias.toString())) {
//                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_" + language, s, "replace", "list");
//                        callCloud.execute();
//                    }
//                }
//
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void plantariumru() {

        try {
            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "Plants.csv");
            int i = 0;

            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                i++;
                final String[] plantLine = scan.nextLine().split(",");
                if (i < 425) continue;

                Document docPost = Jsoup.connect("http://www.plantarium.ru/page/search.html?match=begins&type=0&mode=full&sample=" + plantLine[0]).timeout(10 * 1000).get();

                String name = null;
                StringBuilder names = new StringBuilder();
                String nameLatin = null;

                Elements searchResult = docPost.getElementsByTag("a");
                for (Element link : searchResult) {
                    if (link.attr("href").startsWith("/page/view/item/")) {

                        Thread.sleep(3000);
                        Document docPlant = Jsoup.connect("http://www.plantarium.ru" + link.attr("href")).timeout(10 * 1000).get();

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
