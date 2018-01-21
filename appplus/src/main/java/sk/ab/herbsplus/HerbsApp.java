package sk.ab.herbsplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

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

        boolean offline = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);

        // Firebase synchronization
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        DatabaseReference countsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_COUNTS);
        countsRef.keepSynced(true);
        DatabaseReference listsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_LISTS);
        listsRef.keepSynced(true);
        DatabaseReference plantsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_PLANTS);
        plantsRef.keepSynced(true);
        if (offline) {
            DatabaseReference taxonomyRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_APG_IV);
            taxonomyRef.keepSynced(true);
            String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
            DatabaseReference searchInLanguageRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.FIREBASE_SEPARATOR + language);
            searchInLanguageRef.keepSynced(true);
            DatabaseReference searchInLatinRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.FIREBASE_SEPARATOR + AndroidConstants.LANGUAGE_LA);
            searchInLatinRef.keepSynced(true);
            DatabaseReference translationsInLanguage = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.FIREBASE_SEPARATOR + language);
            translationsInLanguage.keepSynced(true);
            DatabaseReference translationsInLanguageGT = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.FIREBASE_SEPARATOR + language + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(true);
            DatabaseReference translationsInEnglish = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.FIREBASE_SEPARATOR + AndroidConstants.LANGUAGE_EN);
            translationsInEnglish.keepSynced(true);
        }

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
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
    }
}
