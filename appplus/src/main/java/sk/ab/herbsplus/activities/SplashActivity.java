package sk.ab.herbsplus.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import sk.ab.herbsbase.AndroidConstants;
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    AndroidConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            startApplication();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AndroidConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startApplication();
                } else {
                    finish();
                }
            }
        }
    }

    private void startApplication() {
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
