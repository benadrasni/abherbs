package sk.ab.herbsbase.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sk.ab.herbsbase.AndroidConstants;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/14/14
 * Time: 8:23 PM
 *
 * Utils
 */
public class Utils {

    public static String getMonthName(int monthNumber) {
        String monthName = "";
        if(monthNumber>=0 && monthNumber<12) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, monthNumber);
            monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        }
        return monthName;
    }

    public static void setVisibility(View v, int resId) {
        View view = v.findViewById(resId);
        if (view.isShown()) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return Math.round(pixels);
    }

    // For writing to a Parcel
    public static <K extends Parcelable,V extends Parcelable> void writeParcelableMap(
            Parcel parcel, int flags, Map<K, V > map)
    {
        parcel.writeInt(map.size());
        for(Map.Entry<K, V> e : map.entrySet()){
            parcel.writeParcelable(e.getKey(), flags);
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    // For reading from a Parcel
    public static <K extends Parcelable,V extends Parcelable> Map<K,V> readParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass)
    {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<K, V>(size);
        for(int i = 0; i < size; i++){
            map.put(kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    public static void displayImage(File filesDir, String fileName, ImageView imageView, DisplayImageOptions options) {
        String fileUri;
        File imgFile = new File(filesDir.getPath() + AndroidConstants.SEPARATOR + fileName);
        if (imgFile.exists()) {
            fileUri = "file:///" + imgFile.getPath();
        } else {
            fileUri = AndroidConstants.STORAGE_ENDPOINT + fileName;
        }

        ImageLoader.getInstance().displayImage(fileUri, new ImageViewAware(imageView), options, new ImageSize(AndroidConstants.IMAGE_SIZE, AndroidConstants.IMAGE_SIZE), null, null);
    }

    public static void displayImage(File filesDir, String fileName, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {
        String fileUri;
        File imgFile = new File(filesDir.getPath() + AndroidConstants.SEPARATOR + fileName);
        if (imgFile.exists()) {
            fileUri = "file:///" + imgFile.getPath();
        } else {
            fileUri = AndroidConstants.STORAGE_ENDPOINT + fileName;
        }
        ImageLoader.getInstance().displayImage(fileUri, imageView, options, listener);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_COMPACT);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void changeLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public static String getThumbnailUrl(String url) {
        String result = url;
        if (url.lastIndexOf('/') > -1) {
            result = url.substring(0, url.lastIndexOf('/')) + AndroidConstants.THUMBNAIL_DIR + url.substring(url.lastIndexOf('/'));
        }
        return result;
    }

    public static List<String> splitWordsIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        ArrayList<String> result = new ArrayList<>();

        ArrayList<String> currentLine = new ArrayList<>();

        String[] sources = source.split("\\s");
        for(String chunk : sources) {
            if(paint.measureText(chunk) < maxWidthPx) {
                processFitChunk(maxWidthPx, paint, result, currentLine, chunk);
            } else {
                //the chunk is too big, split it.
                List<String> splitChunk = splitIntoStringsThatFit(chunk, maxWidthPx, paint);
                for(String chunkChunk : splitChunk) {
                    processFitChunk(maxWidthPx, paint, result, currentLine, chunkChunk);
                }
            }
        }

        if(! currentLine.isEmpty()) {
            result.add(TextUtils.join(" ", currentLine));
        }
        return result;
    }

    /**
     * Splits a string to multiple strings each of which does not exceed the width
     * of maxWidthPx.
     */
    private static List<String> splitIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        if(TextUtils.isEmpty(source) || paint.measureText(source) <= maxWidthPx) {
            return Arrays.asList(source);
        }

        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        for(int i = 1; i <= source.length(); i++) {
            String substr = source.substring(start, i);
            if(paint.measureText(substr) >= maxWidthPx) {
                //this one doesn't fit, take the previous one which fits
                String fits = source.substring(start, i - 1);
                result.add(fits);
                start = i - 1;
            }
            if (i == source.length()) {
                String fits = source.substring(start, i);
                result.add(fits);
            }
        }

        return result;
    }

    /**
     * Processes the chunk which does not exceed maxWidth.
     */
    private static void processFitChunk(float maxWidth, Paint paint, ArrayList<String> result, ArrayList<String> currentLine, String chunk) {
        currentLine.add(chunk);
        String currentLineStr = TextUtils.join(" ", currentLine);
        if (paint.measureText(currentLineStr) >= maxWidth) {
            //remove chunk
            currentLine.remove(currentLine.size() - 1);
            result.add(TextUtils.join(" ", currentLine));
            currentLine.clear();
            //ok because chunk fits
            currentLine.add(chunk);
        }
    }
}
