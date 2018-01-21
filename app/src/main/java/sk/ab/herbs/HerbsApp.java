package sk.ab.herbs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import sk.ab.herbsbase.AndroidConstants;
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

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // image cache
        int cacheSize = preferences.getInt(AndroidConstants.CACHE_SIZE_KEY, AndroidConstants.DEFAULT_CACHE_SIZE);
        if (cacheSize <= 0) {
            cacheSize = AndroidConstants.DEFAULT_CACHE_SIZE;
            editor.putInt(AndroidConstants.CACHE_SIZE_KEY, cacheSize);
            editor.apply();
        }
        initImageLoader(getApplicationContext(), cacheSize);

        // rate counter
        int rateCounter = preferences.getInt(AndroidConstants.RATE_COUNT_KEY, AndroidConstants.RATE_COUNTER);
        rateCounter--;
        editor.putInt(AndroidConstants.RATE_COUNT_KEY, rateCounter);
        int rateState = preferences.getInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);
        if (rateCounter <= 0 && rateState == AndroidConstants.RATE_NO) {
            editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_SHOW);
        }

        // version specific key
        editor.putBoolean(sk.ab.herbsbase.AndroidConstants.RESET_KEY + BuildConfig.VERSION_CODE, true);
        editor.apply();

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumberOfPetals());
    }

    @Override
    public boolean isOffline() {
        return false;
    }
}
