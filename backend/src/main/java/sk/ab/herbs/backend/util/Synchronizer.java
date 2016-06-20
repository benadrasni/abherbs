package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by adrian on 4.5.2016.
 */
public class Synchronizer {

    public static void main(String[] params) {

        File file = new File("/home/adrian/Dev/projects/abherbs/backend/Plants.csv");

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                String[] name = scan.nextLine().split(",");




                break;
            }
            scan.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }
}
