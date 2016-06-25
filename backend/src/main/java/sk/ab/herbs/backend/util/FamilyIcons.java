package sk.ab.herbs.backend.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 *
 * Created by adrian on 4.5.2016.
 */
public class FamilyIcons {

    public static void main(String[] params) {

        File file = new File("/home/adrian/Dev/projects/abherbs/backend/Families.csv");

        try {
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine()){
                final String[] row = scan.nextLine().split(",");
                System.out.println(row[1]);
                try {
                    downloadFromUrl(new URL("https://storage.googleapis.com/abherbs/.families/family_" + row[0] + ".webp"), "/home/adrian/Dev/temp/" + row[1] + ".webp");
                } catch (IOException e) {
                }

            }
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void downloadFromUrl(URL url, String localFilename) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URLConnection urlConn = url.openConnection();//connect

            is = urlConn.getInputStream();               //get connection inputstream
            fos = new FileOutputStream(localFilename);   //open outputstream to local file

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

}
