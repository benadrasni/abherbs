package sk.ab.herbs.backend.util;

import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by adrian on 4.5.2016.
 */
public class Preparer {

    public static void main(String[] params) {

        try {
            String name = "Atropa belladonna";
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + name + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();
            String vn = wikiPage.substring(wikiPage.indexOf("{{VN"), wikiPage.indexOf("}}", wikiPage.indexOf("{{VN"))).replace("\n", "");
            String[] vnItems = vn.substring(5, vn.length()).split("\\|");

            Map<String, List<String>> result = new TreeMap<>();
            List<String> latin = new ArrayList<>();
            latin.add(name);
            result.put("la", latin);

            for(String vnItem : vnItems) {
                String[] hlp = vnItem.split("=");
                if (hlp.length > 1) {
                    String language = hlp[0];

                    String[] names = hlp[1].trim().split(", ");
                    if (names.length == 1) {
                        names = hlp[1].split(" / ");
                    }
                    List<String> speciesValuesOld = new ArrayList<>(Arrays.asList(names));
                    List<String> speciesValues = new ArrayList<>();
                    for(String speciesValue : speciesValuesOld) {
                        if (!name.toLowerCase().equals(speciesValue.toLowerCase())) {
                            speciesValues.add(speciesValue.trim());
                        }
                    }

                    if (speciesValues.size() > 0) {
                        result.put(language, speciesValues);
                    }

                }
            }

            String id = getWikidata(name);


            URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json");
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(id);

            JsonObject labels = wikidata.getAsJsonObject("labels");

            for (Map.Entry<String,JsonElement> entry : labels.entrySet()) {
                JsonObject value = entry.getValue().getAsJsonObject();

                List<String> names = result.get(value.get("language").getAsString());
                List<String> namesLower = new ArrayList<>();
                if (names == null) {
                    names = new ArrayList<>();
                    result.put(value.get("language").getAsString(), names);
                }
                for (String onename : names) {
                    namesLower.add(onename.toLowerCase());
                }

                if (!namesLower.contains(value.get("value").getAsString().toLowerCase()) && !name.toLowerCase().equals(value.get("value").getAsString().toLowerCase())) {
                    names.add(value.get("value").getAsString());
                }
            }

            JsonElement elem = wikidata.get("aliases");
            if (elem.isJsonObject()) {
                JsonObject aliases = wikidata.getAsJsonObject("aliases");

                for (Map.Entry<String, JsonElement> entry : aliases.entrySet()) {
                    JsonArray value = entry.getValue().getAsJsonArray();

                    List<String> names = result.get(entry.getKey());
                    List<String> namesLower = new ArrayList<>();
                    if (names == null) {
                        names = new ArrayList<>();
                        result.put(entry.getKey(), names);
                    }
                    for (String onename : names) {
                        namesLower.add(onename.toLowerCase());
                    }

                    for (JsonElement v : value) {
                        if (!namesLower.contains(v.getAsJsonObject().get("value").getAsString().toLowerCase()) && !name.toLowerCase().equals(v.getAsJsonObject().get("value").getAsString().toLowerCase())) {
                            names.add(v.getAsJsonObject().get("value").getAsString());
                        }
                    }
                }
            }

            for(Map.Entry<String, List<String>> entry : result.entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();

                if (value != null && value.size() > 0 && !key.startsWith("kk-") && !key.startsWith("zh-sg") && !key.startsWith("zh-hk") && !key.startsWith("zh-cn")) {
                    System.out.print("|" + key + "=");

                    StringBuilder names = new StringBuilder();
                    for (String onename : value) {
                        if (names.length() > 0) {
                            names.append(", ");
                        }
                        names.append(onename);
                    }
                    System.out.println(names.toString());
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String getWikidata(String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + name).get();

            String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

            return wikiPage.substring(wikiPage.lastIndexOf("/")+1, wikiPage.indexOf("#"));


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
