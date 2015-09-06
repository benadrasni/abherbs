package sk.ab.tools;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

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
}
