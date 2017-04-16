package sk.ab.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 *
 * Created by adrian on 11. 3. 2017.
 */

public class Utils {

    public static String getFilterKey(Map<String, String> filter, String[] filterAttributes) {
        StringBuilder filterKey = new StringBuilder();

        String separator = "";
        for (String filterAttribute : filterAttributes) {
            filterKey.append(separator);
            filterKey.append(getOrDefault(filter, filterAttribute, ""));

            separator = "_";
        }

        return filterKey.toString();
    }

    public static String getOrDefault(Map<String, String> filter, String key, String defValue) {
        String value = filter.get(key);
        if (value == null) {
            value = defValue;
        }
        return value;
    }

    public static void downloadFromUrl(URL url, String localFilename) throws IOException {
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URLConnection urlConn = url.openConnection();//connect

            is = urlConn.getInputStream();               //get connection inputstream
            File file = new File(localFilename);
            file.getParentFile().mkdirs();
            fos = new FileOutputStream(file);   //open outputstream to local file

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

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}
