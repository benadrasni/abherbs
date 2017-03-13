package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sk.ab.herbsplus.HerbsApp;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.StorageLoading;


/**
 * Splash activity
 *
 * Created by adrian on 11.3.2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
        boolean offlineMode = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);

        if (offlineMode && ((HerbsApp)getApplication()).isNetworkAvailable(getApplicationContext())) {
            StorageLoading storageLoading = new StorageLoading(this, FilterPlantsPlusActivity.class);
            storageLoading.downloadOfflineFiles();
        } else {
            Intent intent = new Intent(this, FilterPlantsPlusActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
