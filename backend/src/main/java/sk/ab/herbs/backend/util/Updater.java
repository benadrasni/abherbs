package sk.ab.herbs.backend.util;

import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Response;
import sk.ab.common.entity.Plant;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.service.HerbCloudClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Updater {
    public static String PATH = "C:/Dev/Projects/abherbs/backend/txt/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv";
    public static String MISSING_FILE_SUFFIX = "_missing.txt";




    public static String CELL_DELIMITER = ";";
    public static String ALIAS_DELIMITER = ",";

    public static void main(String[] params) {

        missing();
        //search();
    }

    private static void termini() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                System.out.println(plantLine[0]);

                termini(firebaseClient, plantLine[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void termini(FirebaseClient firebaseClient, String plantName) {

        try {
            List<String> plantNames = new ArrayList<>();
            Document docPlant = Jsoup.connect("http://termini.lza.lv/term.php?term=" + plantName + "&lang=LA").timeout(50*1000).get();
            Elements results = docPlant.getElementsByClass("translations");
            if (results.size() == 0) {
                results = docPlant.getElementsByClass("entryline");
                if (results.size() > 0) {
                    results = results.get(0).getElementsByTag("span");
                    if (results.size() > 1 && "LV".equals(results.get(0).text())) {
                        for (int i = 1; i < results.size(); i++) {
                            plantNames.add(results.get(i).text().trim());
                        }
                    }
                }
            } else {
                for (Element result : results) {
                    if (result.attr("href").endsWith("LV")) {
                        plantNames.add(result.text().trim());
                        break;
                    }
                }
            }

            if (plantNames.size() > 0) {
                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation("lv", plantName);
                Map<String, Object> translation = translationCall.execute().body();
                if (translation == null) {
                    translation = new HashMap<String, Object>();
                }
                String label = (String)translation.get("label");
                List<String> names = (List) translation.get("names");

                translation.put("label", plantNames.get(0));

                List<String> newNames = new ArrayList<>();
                for (int i=1; i < plantNames.size(); i++) {
                    newNames.add(plantNames.get(i).trim());
                }

                if (label != null && !label.toLowerCase().equals(plantNames.get(0))) {
                    boolean exists = false;
                    for(String name : newNames) {
                        if (label.toLowerCase().equals(name)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        newNames.add(label.toLowerCase());
                    }
                }

                if (names != null) {
                    for (String oldName : names) {
                        if (oldName.toLowerCase().equals(plantNames.get(0))) {
                            continue;
                        }
                        boolean exists = false;
                        for(String name : newNames) {
                            if (oldName.toLowerCase().equals(name)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            newNames.add(oldName.toLowerCase());
                        }

                    }
                }

                if (newNames.size() > 0) {
                    translation.put("names", newNames);
                } else {
                    translation.remove("names");
                }

                Call<Object> callFirebaseSave = firebaseClient.getApiService().saveTranslation("lv", plantName, translation);
                callFirebaseSave.execute().body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void termanianet() {
        File file = new File(PATH + PLANTS_FILE);

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                if (plantLine[0].compareTo("Vaccinium vitis-idaea") <= 0) continue;

                System.out.println(plantLine[0]);

                termanianet(plantLine[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void termanianet(String plantName) {
        final HerbCloudClient herbCloudClient = new HerbCloudClient();

        try {
            Map<String, List<String>> all = new HashMap<>();

            Document docPlant = Jsoup.connect("http://www.termania.net/iskanje?query=" + plantName + "&SearchIn=All").timeout(10*1000).get();

            Elements results = docPlant.getElementsByClass("results");
            if (results.size() > 1) {

                Elements sections = results.get(1).getElementsByTag("h4");

                for (Element section : sections) {
                    if (section.text().startsWith(plantName)) {
                        Elements names = section.nextElementSibling().getElementsByClass("nlang");
                        for (Element name : names) {
                            String language = name.text();
                            Node sibling = name.nextSibling();
                            String[] pnames = sibling.outerHtml().split(",");

                            for(String pname : pnames) {
                                putName(all, language, pname.trim());
                            }
                        }
                    }
                }
            }

            if (all.size() > 0) {

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(plantName);
                Plant plant = callCloud.execute().body();


                for (Map.Entry<String, List<String>> entry : all.entrySet()) {
                    String language = entry.getKey();
                    if (language.equals("la")) {
                        continue;
                    }

                    List<String> names = entry.getValue();

                    if (names.size() > 0) {
                        String existingLabel = plant.getLabel().get(language);
                        if (existingLabel != null) {
                            if (!containsCaseInsensitive(existingLabel, names)) {
                                names.add(1, existingLabel);
                            }
                        }

                        ArrayList<String> existingAlias = plant.getNames().get(language);
                        if (existingAlias != null) {
                            for (String alias : existingAlias) {
                                if (!containsCaseInsensitive(alias, names)) {
                                    names.add(alias);
                                }
                            }
                        }

                        String newLabel = names.remove(0);
                        if (!newLabel.equals(existingLabel)) {
                            callCloud = herbCloudClient.getApiService().update(plantName, "label_" + language, newLabel, "replace", "string");
                            callCloud.execute();
                        }

                        StringBuilder sb = new StringBuilder();
                        for (String n : names) {
                            if (sb.length() > 0) {
                                sb.append(",");
                            }
                            sb.append(n);
                        }

                        if (sb.length() > 0) {
                            String alias = sb.toString();
                            callCloud = herbCloudClient.getApiService().update(plantName, "alias_" + language, alias, "replace", "list");
                            callCloud.execute();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void putName(Map<String, List<String>> all, String language, String name) {
        List<String> result = all.get(language);
        if (result == null) {
            result = new ArrayList<>();
        }
        if (!containsCaseInsensitive(name, result)) {
            result.add(name);
        }

        all.put(language, result);
    }

    private static void gardensljubljana() {
        try {
//            File f = new File("C:/Development/temp/PLANTAS.html");
//            Document docList = Jsoup.parse(f, "UTF-8");
//
//            Elements tables = docList.getElementsByTag("table");
//            Element table = tables.get(0);
//
//            Elements trs = table.getElementsByTag("tr");
//
//            for (Element tr : trs) {
//                Elements tds = tr.getElementsByTag("td");
//                if (tds.size() > 0) {
//                    Elements as = tds.get(0).getElementsByTag("a");
//                    if (as.size() > 1) {
//                        String latinName = as.get(1).text();
//                        String name = "";
//                        StringBuilder alias = new StringBuilder();
//                        String names = tds.get(1).text();
//                        if (names.length() > 0) {
//                            String s[] = names.split(", ");
//                            if (s.length > 0) {
//                                name = s[0];
//                            }
//                            if (s.length > 1) {
//                                for(int i = 1; i < s.length; i++) {
//                                    if (alias.length() > 0) {
//                                        alias.append(ALIAS_DELIMITER);
//                                    }
//                                    alias.append(s[i]);
//                                }
//                            }
//                            System.out.println(latinName + CELL_DELIMITER + name + CELL_DELIMITER + alias.toString());
//                        }
//                    }
//                }
//            }

            update("sl_names.csv", "sl", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void botanicjp() {
        try {
//            for(int i=97; i<123; i++) { //97 - 123
//
//                Document docList = Jsoup.connect("http://www.botanic.jp/contents/zz"+(char)i+".htm").timeout(10*1000).get();
//
//                Elements tables = docList.getElementsByTag("table");
//                Element table = tables.get(4);
//
//                Elements trs = table.getElementsByTag("tr");
//
//                for (Element tr : trs) {
//                    Elements tds = tr.getElementsByTag("td");
//                    if (tds.size() > 0) {
//                        Elements as = tds.get(0).getElementsByTag("a");
//                        if (as.size() > 0) {
//                            String latinName = as.get(0).text();
//                            String name = tds.get(1).text();
//                            String alias = "";
//
////                            if (latinName.compareTo("Spiraea salicifolia") < 0) {
////                                continue;
////                            }
//
//                            try {
//                                Document docPlant = Jsoup.connect("http://www.botanic.jp" + as.get(0).attr("href").substring(2)).timeout(10 * 1000).get();
//
//                                Elements spans = docPlant.getElementsByTag("span");
//                                if (spans.size() > 0) {
//                                    String txt = spans.get(0).text();
//                                    if (txt.indexOf("(") > -1) {
//                                        alias = txt.substring(txt.indexOf("(") + 1, txt.length() - 1);
//                                    }
//                                }
//                            } catch (HttpStatusException ex) {
//
//                            }
//
//                            System.out.println(latinName + CELL_DELIMITER + name + CELL_DELIMITER + alias);
//                        }
//                    }
//                }
//            }

            update("ja_names.csv", "ja", false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void hircbotanichr() {
        try {
//            for(int i=141; i<40000; i++) {
//
//                Document docList = Jsoup.connect("http://hirc.botanic.hr/fcd/DetaljiFrame.aspx?IdVrste="+i).timeout(10*1000).get();
//
//                String latinName = "";
//                String name = "";
//                StringBuilder alias = new StringBuilder();
//                Elements latinNames = docList.getElementsByAttributeValue("id", "ContentPlaceHolder1_DetaljiVrste1_FormView1_lblNazivVrste");
//                if (latinNames.size() > 0) {
//                    if (latinNames.first().text().contains("ssp.")) {
//                        continue;
//                    }
//                    String[] latinTxt = latinNames.first().text().split(" ");
//
//                    latinName = latinTxt[0] + " " + latinTxt[1];
//
//                    Elements names = docList.getElementsByAttributeValue("id", "ContentPlaceHolder1_DetaljiVrste1_ctl02_Repeater1_lblNarodnaImena");
//                    if (names.size() > 0) {
//                        Element element = names.first();
//                        element = element.nextElementSibling();
//                        if (element != null) {
//                            int count = 0;
//                            while (element.attr("id") == null || !element.attr("id").equals("ContentPlaceHolder1_DetaljiVrste1_ctl02_Repeater1_lblSufix")) {
//                                String txt = element.text().trim();
//                                if (txt.contains("(Hr)")) {
//                                    if (count == 0) {
//                                        name = txt.substring(0, txt.indexOf("(Hr)") - 1).trim();
//                                    } else {
//                                        if (alias.length() > 0) {
//                                            alias.append(ALIAS_DELIMITER);
//                                        }
//                                        alias.append(txt.substring(0, txt.indexOf("(Hr)") - 1).trim());
//                                    }
//                                    count++;
//                                }
//                                element = element.nextElementSibling();
//                            }
//                        }
//
//                        if (name.length() > 0) {
//                            System.out.println(latinName + CELL_DELIMITER + name + CELL_DELIMITER + alias.toString());
//                        }
//                    }
//                }
//            }

            update("hr_names.csv", "hr", false);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void eplantero() {
        try {
//            for(int i=97; i<123; i++) { //97 - 123
//
//                Document docList = Jsoup.connect("http://www.eplante.ro/plante-a-z/litera-"+(char)i+".html").timeout(10*1000).get();
//
//                Elements tables = docList.getElementsByClass("col");
//                Element table = tables.get(0);
//
//                Elements as = table.getElementsByTag("a");
//
//                for (Element a : as) {
//                    String nameTxt = a.text().replace(" ( ", ";").replace(" )", "");
//                    String[] names = nameTxt.split(";");
//
//                    if (names.length == 2) {
//                        String latinName = names[0];
//                        String[] alias = names[1].split(",");
//
//                        String name = alias[0];
//
//                        StringBuilder sb = new StringBuilder();
//                        if (alias.length > 1) {
//                            for(int j=1; j<alias.length;j++) {
//                                if (sb.length() > 0) {
//                                    sb.append(",");
//                                }
//                                sb.append(alias[j].trim());
//                            }
//                        }
//
//                        System.out.println(latinName + CELL_DELIMITER + name + CELL_DELIMITER + sb.toString());
//                    }
//                }
//            }

            update("ro_names.csv", "ro", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void plantlistro() {
        try {
//            for(int i=65; i<91; i++) { //65 - 91
//
//                Document docList = Jsoup.connect("http://plantlist.ro/speciesset.php?id="+(char)i).timeout(10*1000).get();
//
//                Elements tables = docList.getElementsByClass("columnList");
//                Element table = tables.get(0);
//
//                Elements as = table.getElementsByTag("a");
//
//                for (Element a : as) {
////                            if (latinName.compareTo("Spiraea salicifolia") < 0) {
////                                continue;
////                            }
//
//                    try {
//                        Document docPlant = Jsoup.connect("http://plantlist.ro" + a.attr("href")).timeout(10 * 1000).get();
//
//                        String latinName = "";
//                        Elements h1s = docPlant.getElementsByTag("h1");
//                        if (h1s.size() > 0) {
//                            latinName = h1s.get(0).text();
//                            if (latinName.indexOf("subsp.") > 0) {
//                                latinName = latinName.substring(0, latinName.indexOf("subsp.")-1).trim();
//                            }
//                        }
//
//                        String name = "";
//                        Elements divs = docPlant.getElementsByClass("subtitleImportant");
//                        if (divs.size() > 0) {
//                            String txt = divs.get(0).text().substring(5, divs.get(0).text().length()-1);
//
//                            String[] alias = txt.split(",");
//                            name = alias[0].trim();
//
//                            StringBuilder sb = new StringBuilder();
//                            if (alias.length > 1) {
//                                for(int j=1; j<alias.length;j++) {
//                                    if (sb.length() > 0) {
//                                        sb.append(",");
//                                    }
//                                    sb.append(alias[j].trim());
//                                }
//                            }
//                            if (latinName.length() > 0 && name.length() > 0) {
//                                System.out.println(latinName + CELL_DELIMITER + name + CELL_DELIMITER + sb.toString());
//                            }
//                        }
//
//                    } catch (HttpStatusException ex) {
//
//                    }
//
//                }
//            }

            update("ro_names.csv", "ro", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void piantemagiche() {

        Map<String, String> labels = new HashMap<>();

        try {
//            for(int i=1; i<30; i++) {
//
//                Document docList = Jsoup.connect("http://piantemagiche.it/piante-dalla-a-alla-z/"+i).timeout(10*1000).get();
//
//                Elements itemList = docList.getElementsByClass("listing-item");
//                for (Element li : itemList) {
//                    Elements as = li.getElementsByTag("a");
//                    if (as.size() > 0) {
//                        String name = as.get(0).text().replace(" (", ",").replace(")", "");
//
//                        String[] names = name.split(",");
//                        if (names.length == 2) {
//                            labels.put(names[0], names[1]);
//                            System.out.println(names[0] + "," + names[1]);
//                        }
//                    }
//                }
//            }

            File namefile = new File(PATH + "it_names.csv");

            Scanner namescan = new Scanner(namefile);
            while(namescan.hasNextLine()) {
                final String[] plantLine = namescan.nextLine().split(",");

                if (plantLine.length > 1) {
                    labels.put(plantLine[0], plantLine[1]);
                }
            }

            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "plants.csv");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(",");
                String nameLatin = plantLine[0];

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(nameLatin);
                Plant plant = callCloud.execute().body();

                String valueLabel = plant.getLabel().get("it");
                if (valueLabel != null) {
                    valueLabel = valueLabel.toLowerCase();
                }

                String label = labels.get(nameLatin);
                if (label != null) {
                    label = label.toLowerCase();
                }

                if (label == null && plant.getSynonyms() != null) {
                    for (String synonym : plant.getSynonyms()) {
                        label = labels.get(synonym);
                        if (label != null) {
                            label = label.toLowerCase();
                            break;
                        }
                    }
                }

                if (label == null && valueLabel != null) {
                    label = valueLabel;
                }

                if (label != null) {
                    callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_it", label, "replace", "string");
                    callCloud.execute();

                    ArrayList<String> aliasToSave = new ArrayList<>();
                    if (valueLabel != null && !label.equals(valueLabel)) {
                        aliasToSave.add(valueLabel);
                    }

                    ArrayList<String> valueAlias = plant.getNames().get("it");
                    if (valueAlias != null) {
                        for (String alias : valueAlias) {
                            alias = alias.toLowerCase();
                            if (!aliasToSave.contains(alias) && !alias.equals(label)) {
                                aliasToSave.add(alias);
                            }
                        }
                    }

                    if (aliasToSave.size() > 0) {
                        StringBuilder aliasSb = new StringBuilder();
                        for (String al : aliasToSave) {
                            if (aliasSb.length() > 0) {
                                aliasSb.append(",");
                            }
                            aliasSb.append(al);
                        }

                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_it", aliasSb.toString(), "replace", "list");
                        callCloud.execute();
                    }

                } else {
                    System.out.println(plantLine[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void miljolareSearch() {

        try {
            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "no_missing.txt");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()){
                final String plantName = scan.nextLine();

                String json = Jsoup.connect("https://www.miljolare.no/sok/?format=json&limit=5&term="+plantName).ignoreContentType(true).execute().body();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(json);

                JsonArray treffs = root.getAsJsonObject().getAsJsonArray("treff");
                if (treffs.size() > 0) {
                    for (JsonElement treff : treffs) {
                        if (treff.getAsJsonObject().get("tittel").getAsString().contains(plantName)) {
                            Document docPlant = Jsoup.connect("https://www.miljolare.no" + treff.getAsJsonObject().get("url").getAsString()).get();

                            Elements taxonTable = docPlant.getElementsByClass("STABELL");
                            if (taxonTable.size() > 0) {
                                Elements taxons = taxonTable.get(0).getElementsByAttributeValue("style", "font-weight: bold;");
                                if (taxons.size() > 0) {
                                    Elements tds = taxons.get(0).getElementsByTag("td");
                                    if (tds.size() > 0) {

                                        if (tds.get(tds.size()-1).text().contains("(")) {
                                            String[] names = tds.get(tds.size() - 1).text().split(" \\(");
                                            names[0] = names[0].toLowerCase();
                                            names[1] = names[1].substring(0, names[1].length() - 1);

                                            Call<Plant> callCloud = herbCloudClient.getApiService().update(plantName, "label_no", names[0], "replace", "string");
                                            callCloud.execute();
                                        } else {
                                            System.out.println(plantName);
                                        }
                                    }
                                }

                            }

                            break;
                        }
                    }
                } else {
                    System.out.println(plantName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void miljolare() {

        Map<String, String> labels = new HashMap<>();

        try {

            Document docList = Jsoup.connect("https://www.miljolare.no/artstre/?or_id=2376&side=arter&start=1&antal=1311").get();

            Elements textList = docList.getElementsByTag("tbody");
            if (textList.size() > 0) {
                Elements trs = textList.get(0).getElementsByTag("tr");
                for(Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    if (tds.size() > 1) {
                        String name = tds.get(0).text().replace(" (", ",").replace(")", "");

                        String[] names = name.split(",");
                        if (names.length == 2) {
                            labels.put(names[1], names[0]);
                        }
                    }
                }
            }

            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "plants.csv");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(",");
                String nameLatin = plantLine[0];

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(nameLatin);
                Plant plant = callCloud.execute().body();

                String valueLabel = plant.getLabel().get("no");
                String label = labels.get(nameLatin);
                if (label == null && plant.getSynonyms() != null) {
                    for (String synonym : plant.getSynonyms()) {
                        label = labels.get(synonym);
                        if (label != null) {
                            label = label.toLowerCase();
                            break;
                        }
                    }
                } else {
                    label = label.toLowerCase();
                }

                if (valueLabel == null) {
                    if (label != null) {
                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_no", label, "replace", "string");
                        callCloud.execute();
                    } else {
                        System.out.println(plantLine[0]);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void luontoportti(String language) {

        try {
//            Document docList = Jsoup.connect("http://www.luontoportti.com/suomi/" + language + "/kukkakasvit/?list=9").get();
//
//            Elements textList = docList.getElementsByAttributeValue("id", "textList");
//            if (textList.size() > 0) {
//                Elements as = textList.get(0).getElementsByTag("a");
//                for(Element a : as) {
//                    String name = a.text();
//
//                    Document plantDoc = Jsoup.connect(a.attr("href")).get();
//
//                    Elements h4s = plantDoc.getElementsByTag("h4");
//                    String nameLatin = null;
//                    if (h4s.size() > 0) {
//                        nameLatin = h4s.get(0).text();
//                    }
//
//                    String alias = "";
//                    Elements texts = plantDoc.getElementsByAttributeValue("id", "teksti");
//                    if (texts.size() > 0) {
//                        Elements lis = texts.get(0).getElementsByTag("li");
//                        for(Element li : lis) {
//                            if (li.text().startsWith("También se llama:")) {
//                                alias = li.text().substring(18).trim();
//
//                                break;
//                            }
//                        }
//                    }
//
//                    if (nameLatin.length() > 3) {
//                        System.out.println(nameLatin + CELL_DELIMITER + name + CELL_DELIMITER + alias);
//                    }
//                }
//            }

            update("es_names.csv", "es", true);;


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void plantariumru() {

        try {
            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            File file = new File(PATH + "plants.csv");
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

    private static void missing() {
        Map<String, BufferedWriter> missingFiles = new HashMap<>();
        Map<String, Integer> missingCounts = new HashMap<>();
        String[] languages = {"sk", "cs", "en", "fr", "pt", "es", "ru", "uk", "de", "no", "da", "fi", "sv", "is",
                "ja", "zh", "hu", "pl", "nl", "tr", "it", "ro", "lt", "lv", "sr", "hr", "sl", "el", "bg", "mt", "et", "fa", "ar", "hi", "pa", "id", "he"};

        try {
            final FirebaseClient firebaseClient = new FirebaseClient();

            Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation();
            Map<String, Object> translation = translationCall.execute().body();

            File file = new File(PATH + PLANTS_FILE);

            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);
                String nameLatin = plantLine[0];
                System.out.println(nameLatin);

                for(String language : languages) {
                    String value = null;
                    Object plantInLanguage = ((Map<String, Object>)translation.get(language)).get(nameLatin);
                    if (plantInLanguage != null) {
                        value = (String) ((Map<String, Object>)plantInLanguage).get("label");
                    }

                    if (value == null) {
                        BufferedWriter bw = missingFiles.get(language);
                        if (bw == null) {
                            File f = new File(PATH + language + MISSING_FILE_SUFFIX);
                            bw = new BufferedWriter(new FileWriter(f));

                            missingFiles.put(language, bw);
                            missingCounts.put(language, 0);
                        }
                        bw.write(nameLatin + "\n");
                        missingCounts.put(language, missingCounts.get(language)+1);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                for (Map.Entry<String, BufferedWriter> bwEntry : missingFiles.entrySet()) {
                    System.out.println(bwEntry.getKey() + "..." + missingCounts.get(bwEntry.getKey()));
                    bwEntry.getValue().close();
                }
            } catch (Exception e) {
            }
        }
    }

    private static void update(String fileWithNames, String language, boolean toLowerCase) throws IOException{
        Map<String, String> labels = new HashMap<>();
        Map<String, ArrayList<String>> aliases = new HashMap<>();

        File nameFile = new File(PATH + fileWithNames);

        Scanner nameScan = new Scanner(nameFile);
        while(nameScan.hasNextLine()) {
            final String[] plantLine = nameScan.nextLine().split(CELL_DELIMITER);

            if (plantLine.length > 1) {
                String existingLabel = labels.get(plantLine[0]);
                if (existingLabel == null) {
                    labels.put(plantLine[0], plantLine[1]);
                } else if (!existingLabel.equals(plantLine[1])) {
                    ArrayList<String> existingAliases = aliases.get(plantLine[0]);
                    if (existingAliases == null) {
                        existingAliases = new ArrayList<>();
                    }
                    if (!containsCaseInsensitive(plantLine[1], existingAliases)) {
                        existingAliases.add(plantLine[1]);
                    }
                    aliases.put(plantLine[0], existingAliases);
                }
            }
            if (plantLine.length > 2) {
                ArrayList<String> existingAliases = aliases.get(plantLine[0]);
                if (existingAliases == null) {
                    existingAliases = new ArrayList<>();
                }
                if (!containsCaseInsensitive(plantLine[2], existingAliases)) {
                    existingAliases.add(plantLine[2]);
                }
                aliases.put(plantLine[0], existingAliases);
            }
        }

        final HerbCloudClient herbCloudClient = new HerbCloudClient();

        File file = new File(PATH + PLANTS_FILE);

        Scanner scan = new Scanner(file);
        while(scan.hasNextLine()) {

            final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);
            String nameLatin = plantLine[0];

            Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(nameLatin);
            Plant plant = callCloud.execute().body();

            String existingLabel = plant.getLabel().get(language);
            if (toLowerCase && existingLabel != null) {
                existingLabel = existingLabel.toLowerCase();
            }

            String newLabel = labels.get(nameLatin);
            if (toLowerCase && newLabel != null) {
                newLabel = newLabel.toLowerCase();
            }

            if (newLabel == null && plant.getSynonyms() != null) {
                for (String synonym : plant.getSynonyms()) {
                    newLabel = labels.get(synonym);
                    if (toLowerCase && newLabel != null) {
                        newLabel = newLabel.toLowerCase();
                        break;
                    }
                }
            }

            if (newLabel == null && existingLabel != null) {
                newLabel = existingLabel;
            }

            if (newLabel != null) {
                callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_"+language, newLabel, "replace", "string");
                callCloud.execute();

                ArrayList<String> aliasToSave = new ArrayList<>();
                if (existingLabel != null && !newLabel.equals(existingLabel)) {
                    aliasToSave.add(existingLabel);
                }

                ArrayList<String> existingAliases = plant.getNames().get(language);
                if (existingAliases != null) {
                    for (String alias : existingAliases) {
                        if (toLowerCase) {
                            alias = alias.toLowerCase();
                        }
                        if (!containsCaseInsensitive(alias, aliasToSave) && !alias.equals(newLabel)) {
                            aliasToSave.add(alias);
                        }
                    }
                }

                ArrayList<String> newAliasesString = aliases.get(nameLatin);
                if (newAliasesString != null) {
                    for (String al : newAliasesString) {
                        ArrayList<String> newAliases = new ArrayList<>(Arrays.asList(al.split(ALIAS_DELIMITER)));
                        for (String alias : newAliases) {
                            if (toLowerCase) {
                                alias = alias.toLowerCase();
                            }
                            if (!containsCaseInsensitive(alias, aliasToSave) && !alias.equals(newLabel)) {
                                aliasToSave.add(alias);
                            }
                        }
                    }
                }

                if (aliasToSave.size() > 0) {
                    StringBuilder aliasSb = new StringBuilder();
                    for (String al : aliasToSave) {
                        if (aliasSb.length() > 0) {
                            aliasSb.append(",");
                        }
                        aliasSb.append(al);
                    }

                    callCloud = herbCloudClient.getApiService().update(plantLine[0], "alias_"+language, aliasSb.toString(), "replace", "list");
                    callCloud.execute();
                }

            } else {
                System.out.println(plantLine[0]);
            }
        }
    }

    private static void search() {
        String[] languages = {""};
        final FirebaseClient firebaseClient = new FirebaseClient();


        try {
            for (String language : languages) {
                Map<String, Map<String, Boolean>> searchMap = new HashMap<>();

                Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation(language);
                Map<String, Object> translation = translationCall.execute().body();

                for (Map.Entry<String, Object> entry : translation.entrySet()) {
                    String plantName = entry.getKey();
                    System.out.println(plantName);
                    Map<String, Object> plant = (Map<String, Object>) entry.getValue();

                    Map<String, Boolean> searchForLabel = null;
                    String label = (String)plant.get("label");
                    if (label != null) {
                        label = label.toLowerCase();
                        searchForLabel = searchMap.get(label);
                        if (searchForLabel == null) {
                            searchForLabel = new HashMap<>();
                            searchMap.put(label, searchForLabel);
                        }
                        searchForLabel.put(plantName, true);
                    }

                    List<String> names = (List<String>)plant.get("names");
                    if (names != null) {
                        for (String name : names) {
                            name = name.toLowerCase();
                            searchForLabel = searchMap.get(name);
                            if (searchForLabel == null) {
                                searchForLabel = new HashMap<>();
                                searchMap.put(name, searchForLabel);
                            }
                            searchForLabel.put(plantName, true);
                        }
                    }
                }

                // name
                Call<Object> callFirebaseSearch = firebaseClient.getApiService().saveSearch(language, searchMap);
                callFirebaseSearch.execute().body();

            }
        } catch (IOException ex) {

        }

    }

    private static boolean containsCaseInsensitive(String strToCompare, List<String>list)
    {
        for(String str:list)
        {
            if(str.equalsIgnoreCase(strToCompare))
            {
                return(true);
            }
        }
        return(false);
    }
}
