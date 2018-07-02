package sk.ab.herbsplus;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sk.ab.common.Constants;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.commons.BaseFilterFragment;
import sk.ab.herbsbase.fragments.ColorOfFlowers;
import sk.ab.herbsbase.fragments.Distribution;
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

    private BaseFilterFragment colorOfFlowers;
    private BaseFilterFragment habitats;
    private BaseFilterFragment numberOfPetals;
    private BaseFilterFragment distribution;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        boolean offline = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        if (offline) {
            // Firebase synchronization
            DatabaseReference countsRef = database.getReference(AndroidConstants.FIREBASE_COUNTS);
            countsRef.keepSynced(true);
            DatabaseReference listsRef = database.getReference(AndroidConstants.FIREBASE_LISTS);
            listsRef.keepSynced(true);
            DatabaseReference plantsRef = database.getReference(AndroidConstants.FIREBASE_PLANTS);
            plantsRef.keepSynced(true);
            DatabaseReference plantsHeadersRef = database.getReference(AndroidConstants.FIREBASE_PLANTS_HEADERS);
            plantsHeadersRef.keepSynced(true);
            DatabaseReference taxonomyRef = database.getReference(AndroidConstants.FIREBASE_APG_IV);
            taxonomyRef.keepSynced(true);

            // language dependent
            String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
            DatabaseReference taxonomyInLanguageRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS_TAXONOMY
                    + AndroidConstants.SEPARATOR + language);
            taxonomyInLanguageRef.keepSynced(true);
            DatabaseReference searchInLanguageRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + language);
            searchInLanguageRef.keepSynced(true);
            DatabaseReference searchInLatinRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_LA);
            searchInLatinRef.keepSynced(true);
            DatabaseReference translationsInLanguage = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + language);
            translationsInLanguage.keepSynced(true);
            DatabaseReference translationsInLanguageGT = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + language + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(true);
            DatabaseReference translationsInEnglish = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_EN);
            translationsInEnglish.keepSynced(true);
        }

        // image cache
        int cacheSize = preferences.getInt(AndroidConstants.CACHE_SIZE_KEY, AndroidConstants.DEFAULT_CACHE_SIZE);
        if (cacheSize <= 0 || cacheSize * 1024 * 1024 > Integer.MAX_VALUE) {
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
        editor.putBoolean(AndroidConstants.RESET_KEY + BuildConfig.VERSION_CODE, true);
        editor.apply();

        filterAttributes = new ArrayList<>();

        List<String> filters = new ArrayList<>();
        for (String filterTag : SpecificConstants.FILTERS) {
            String filter = preferences.getString(filterTag, null);
            if (filter != null) {
                filters.add(filter);
            }
        }
        if (filters.isEmpty()) {
            filters = new ArrayList<>(Arrays.asList(SpecificConstants.FILTER_ATTRIBUTES));
        }
        setFilters(filters);
    }

    @Override
    public boolean isOffline() {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
    }

    @Override
    public void setToken(String token) {
        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AndroidConstants.TOKEN_KEY, token);
        editor.apply();
    }

    public void setFilters(List<String> filters) {
        filterAttributes.clear();
        for (final String filter : filters) {
            switch (filter) {
                case Constants.COLOR_OF_FLOWERS:
                    if (colorOfFlowers == null) {
                        colorOfFlowers = new ColorOfFlowers();
                    }
                    filterAttributes.add(colorOfFlowers);
                    break;
                case Constants.HABITAT:
                    if (habitats == null) {
                        habitats = new Habitats();
                    }
                    filterAttributes.add(habitats);
                    break;
                case Constants.NUMBER_OF_PETALS:
                    if (numberOfPetals == null) {
                        numberOfPetals = new NumberOfPetals();
                    }
                    filterAttributes.add(numberOfPetals);
                    break;
                case Constants.DISTRIBUTION:
                    if (distribution == null) {
                        distribution = new Distribution();
                    }
                    filterAttributes.add(distribution);
                    break;
            }
        }

    }
}
