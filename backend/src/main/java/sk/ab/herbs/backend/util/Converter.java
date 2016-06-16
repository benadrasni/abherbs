package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by adrian on 4.5.2016.
 */
public class Converter {

    public static void main(String[] params) {

        try {
            Scanner scan = new Scanner(new File("C:/Development/Projects/textWikipecies.txt"));
            boolean first = true;
            String result = "";
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if (first) {
                    result = "|" + line.trim();
                } else {
                    result = result + "=" + line.trim();
                    System.out.println(result);
                }
                first = !first;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
