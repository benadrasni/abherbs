package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import retrofit2.Call;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.util.Utils;

/**
 *
 * Created by adrian on 4.5.2016.
 */
public class Firebase {
    private static String PATH_TO_PLANTS = "C:/Dev/Projects/abherbs/backend/txt/plants.csv";
    private static String PATH_TO_FAMILIES = "C:/Dev/Projects/abherbs/backend/txt/Families.csv";
    private static String PATH_TO_STORAGE = "C:/Dev/Storage/";

    private static String GOOGLE_STORAGE_URL = "https://storage.googleapis.com/abherbs";

    public static void main(String[] params) {

        firebaseSynchronization();
    }

    private static void firebaseSynchronization() {

        final FirebaseClient firebaseClient = new FirebaseClient();

        downloadFamilyIcons();

        downloadPhotos(firebaseClient);
    }

    private static void downloadFamilyIcons() {
        File file = new File(PATH_TO_FAMILIES);

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                final String[] row = scan.nextLine().split(",");
                System.out.println(row[1]);
                try {
                    Utils.downloadFromUrl(new URL(GOOGLE_STORAGE_URL + "/.families/family_" + row[0] + ".webp"), PATH_TO_STORAGE + "families/" + row[1] + ".webp");
                } catch (IOException e) {
                }

            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadPhotos(FirebaseClient firebaseClient) {
        File file = new File(PATH_TO_PLANTS);

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                final String[] plantLine = scan.nextLine().split(";");
                System.out.println(plantLine[0]);

                Call<FirebasePlant> callCloudPlant = firebaseClient.getApiService().getPlant(plantLine[0]);
                FirebasePlant plant = callCloudPlant.execute().body();

                try {
                    Utils.downloadFromUrl(new URL(plant.getIllustrationUrl()), PATH_TO_STORAGE + plant.getIllustrationUrl().substring(GOOGLE_STORAGE_URL.length()));

                    for(String photoUrl : plant.getPhotoUrls()) {
                        Utils.downloadFromUrl(new URL(photoUrl), PATH_TO_STORAGE + "photos/" + photoUrl.substring(GOOGLE_STORAGE_URL.length()));
                        String thumbnailUrl = photoUrl.substring(0, photoUrl.lastIndexOf("/")) + "/.thumbnails" + photoUrl.substring(photoUrl.lastIndexOf("/"));
                        Utils.downloadFromUrl(new URL(thumbnailUrl), PATH_TO_STORAGE + "photos/" + thumbnailUrl.substring(GOOGLE_STORAGE_URL.length()));
                    }

                } catch (IOException e) {
                }

            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
