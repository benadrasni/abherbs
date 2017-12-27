package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import retrofit2.Call;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.service.FirebaseClient;

/**
 *
 * Created by adrian on 12/26/2017.
 */

public class Refresher {

    public static String PATH = "D:/Projects/abherbs/backend/txt/";
    public static String PLANTS_FILE = "plants.csv"; //"sample.csv";

    public static String CELL_DELIMITER = ";";
    public static String ALIAS_DELIMITER = ",";

    public static void main(String[] params) {

        updateSources();
    }

    private static void updateSources() {
        File file = new File(PATH + PLANTS_FILE);
        final FirebaseClient firebaseClient = new FirebaseClient();

        try {
            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(CELL_DELIMITER);

                System.out.println(plantLine[0]);

                updateSourcesForPlant(firebaseClient, plantLine[0], plantLine[2]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateSourcesForPlant(FirebaseClient firebaseClient, String plantName, String plantId) {
        try {
            Call<FirebasePlant> plantCall = firebaseClient.getApiService().getPlant(plantName);
            FirebasePlant plant = plantCall.execute().body();

            Call<Map<String, Object>> translationCall = firebaseClient.getApiService().getTranslation("en", plantName);
            Map<String, Object> translationEn = translationCall.execute().body();

            ArrayList<String> commonSources = new ArrayList<>();
            ArrayList<String> enSources = new ArrayList<>();

            if (translationEn.get("sourceUrls") != null) {
                for (String source : (ArrayList<String>) translationEn.get("sourceUrls")) {
                    if (source.startsWith("http://commons.wikimedia.org")) {
                        source = source.replace("http:", "https:");
                    }

                    if (source.contains("commons.wikimedia.org")) {
                        if (!commonSources.contains(source)) {
                            commonSources.add(source);
                        }
                    } else {
                        if (!enSources.contains(source)) {
                            enSources.add(source);
                        }
                    }
                }
            }

            if (plant.getSourceUrls() != null) {
                for (String source : plant.getSourceUrls()) {
                    if (source.contains("commons.wikimedia.org")) {
                        if (!commonSources.contains(source)) {
                            commonSources.add(source);
                        }
                    } else {
                        if (!enSources.contains(source)) {
                            enSources.add(source);
                        }
                    }
                }
            }

            plant.setPlantId(Integer.parseInt(plantId));
            plant.setSourceUrls(commonSources);
            translationEn.put("sourceUrls", enSources);

            Call<Object> callFirebaseSaveTranslation = firebaseClient.getApiService().saveTranslation("en", plantName, translationEn);
            callFirebaseSaveTranslation.execute().body();

            Call<FirebasePlant> callFirebaseSavePlant = firebaseClient.getApiService().savePlant(plantName, plant);
            callFirebaseSavePlant.execute().body();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
