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
public class UpdaterNamesHu {

    public static void main(String[] params) {

        try {
            Map<String, String> names = new HashMap<>();

            File file = new File("/home/adrian/Downloads/Fuveszkonyv_Taxon20111104.csv");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {
                final String[] plantLine = scan.nextLine().split(",");

                if (plantLine.length > 2) {
                    names.put(plantLine[0] + " " + plantLine[1], plantLine[2]);
                }
            }

//            Document doc = Jsoup.connect("http://anp.nemzetipark.gov.hu/index_v.php?pg=menu_320").get();
//
//            Elements elements = doc.getElementsByTag("td");
//            boolean isFirst = true;
//            String nameLatin = "";
//            for (Element element : elements) {
//                if (isFirst) {
//                    nameLatin = element.text();
//                } else {
//                    names.put(nameLatin, element.text());
//                }
//                isFirst = !isFirst;
//            }

            final HerbCloudClient herbCloudClient = new HerbCloudClient();

            file = new File("/home/adrian/Dev/projects/abherbs/backend/Plants.csv");

            scan = new Scanner(file);
            while(scan.hasNextLine()){

                final String[] plantLine = scan.nextLine().split(",");

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(plantLine[0]);

                Plant plant = callCloud.execute().body();

                String valueHu = plant.getLabel().get("hu");

                String value = names.get(plantLine[0]);

                if (value != null) {
                    if (valueHu == null) {
                        value = value.toLowerCase();
                        callCloud = herbCloudClient.getApiService().update(plantLine[0], "label_hu", value, "replace", "string");

                        callCloud.execute();
                    }
                } else {
                    if (valueHu == null) {
                        System.out.println(plantLine[0]);
                    }
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
