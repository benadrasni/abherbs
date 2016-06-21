package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.Response;
import sk.ab.herbs.backend.entity.Plant;
import sk.ab.herbs.backend.service.HerbCloudClient;

/**
 * Created by adrian on 4.5.2016.
 */
public class Synchronizer {

    public static void main(String[] params) {

        File file = new File("C:/Development/projects/abherbs/backend/Plants.csv");

        try {
            Scanner scan = new Scanner(file);

            final HerbCloudClient herbCloudClient = new HerbCloudClient();
            while(scan.hasNextLine()){
                String[] name = scan.nextLine().split(",");

                herbCloudClient.getApiService().synchronizePlant(name[0], name[1])
                        .enqueue(new Callback<Plant>() {
                            @Override
                            public void onResponse(Response<Plant> response) {

                            }

                            @Override
                            public void onFailure(Throwable t) {

                            }
                        });

                break;
            }
            scan.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
