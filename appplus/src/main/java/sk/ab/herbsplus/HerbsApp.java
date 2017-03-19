package sk.ab.herbsplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

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
        boolean offline = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        DatabaseReference countsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_COUNTS);
        countsRef.keepSynced(true);
        DatabaseReference listsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_LISTS);
        listsRef.keepSynced(true);
        if (offline) {
            DatabaseReference plantsRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_PLANTS);
            plantsRef.keepSynced(true);
            DatabaseReference taxonomyRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_APG_III);
            taxonomyRef.keepSynced(true);
        }

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

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumberOfPetals());
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
