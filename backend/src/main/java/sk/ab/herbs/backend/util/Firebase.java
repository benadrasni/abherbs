package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import retrofit.Call;
import sk.ab.common.entity.Plant;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.service.HerbCloudClient;

/**
 *
 * Created by adrian on 4.5.2016.
 */
public class Firebase {
    public static String PATH = "C:/Development/Projects/abherbs/backend/";
//    public static String PATH = "/home/adrian/Dev/projects/abherbs/backend/";


    public static void main(String[] params) {

        firebaseSynchronization();
    }

    private static void firebaseSynchronization() {
        try {
            final HerbCloudClient herbCloudClient = new HerbCloudClient();
            final FirebaseClient firebaseClient = new FirebaseClient();

            File file = new File(PATH + "Plants.csv");

            Scanner scan = new Scanner(file);
            while(scan.hasNextLine()) {

                final String[] plantLine = scan.nextLine().split(",");
                System.out.println(plantLine[0]);

                Call<Plant> callCloud = herbCloudClient.getApiService().getDetail(plantLine[0]);
                Plant plant = callCloud.execute().body();

                Call<Plant> callFirebase = firebaseClient.getApiService().savePlant(plantLine[0], plant);
                callFirebase.execute().body();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
