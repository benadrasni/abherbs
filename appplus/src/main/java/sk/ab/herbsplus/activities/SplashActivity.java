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
import android.util.Log;
import android.view.WindowManager;

import sk.ab.common.entity.PlantTaxon;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsplus.HerbsApp;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.StorageLoading;


/**
 * Splash activity
 *
 * Created by adrian on 11.3.2017.
 */
public class SplashActivity extends SearchBaseActivity {

    private static final String TAG = "SplashActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        if (getIntent().getExtras() != null) {
            String count = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_COUNT);
            String path = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_PATH);
            Log.d(TAG, path + " (" + count + ")");
            callProperActivity(Integer.parseInt(count), path);
        } else {
            Intent intent = new Intent(this, FilterPlantsPlusActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void callProperActivity(Integer count, String path) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (count == 1) {
            callDetailActivity(path, true);
        } else {
            callListActivity(path, count, true);
        }
    }
}
