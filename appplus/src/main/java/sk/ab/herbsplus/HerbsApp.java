package sk.ab.herbsplus;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.fragments.ColorOfFlowers;
import sk.ab.herbsbase.fragments.Habitats;
import sk.ab.herbsbase.fragments.NumberOfPetals;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 12/1/14
 * Time: 9:20 PM
 * <p/>
 */
public class HerbsApp extends BaseApp {
    private static final String PROPERTY_ID = "UA-56892333-1";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbsplus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int rateCounter = preferences.getInt(sk.ab.herbsbase.AndroidConstants.RATE_COUNT_KEY, sk.ab.herbsbase.AndroidConstants.RATE_COUNTER);
        rateCounter--;
        editor.putInt(sk.ab.herbsbase.AndroidConstants.RATE_COUNT_KEY, rateCounter);

        int rateState = preferences.getInt(sk.ab.herbsbase.AndroidConstants.RATE_STATE_KEY, sk.ab.herbsbase.AndroidConstants.RATE_NO);
        if (rateCounter <= 0 && rateState == sk.ab.herbsbase.AndroidConstants.RATE_NO) {
            editor.putInt(sk.ab.herbsbase.AndroidConstants.RATE_STATE_KEY, sk.ab.herbsbase.AndroidConstants.RATE_SHOW);
        }

        editor.putBoolean(sk.ab.herbsbase.AndroidConstants.RESET_KEY + BuildConfig.VERSION_CODE, true);
        editor.apply();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.enableAutoActivityReports(this);
        tracker = analytics.newTracker(PROPERTY_ID);

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumberOfPetals());
    }
}
