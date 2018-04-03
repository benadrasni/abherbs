package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;


/**
 * Splash activity
 *
 * Created by adrian on 11.3.2017.
 */
public abstract class SplashBaseActivity extends SearchBaseActivity {

    private static final String TAG = "SplashBaseActivity";

    @Override
    protected int getLayoutResId() {
        return R.layout.splash_activity;
    };


    protected void startApplication() {
        if (getIntent().getExtras() != null) {
            String count = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_COUNT);
            if (count != null) {
                String path = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_PATH);
                Log.d(TAG, path + " (" + count + ")");
                callProperActivity(Integer.parseInt(count), path);
            } else {
                String action = getIntent().getExtras().getString(AndroidConstants.ACTION);
                if (AndroidConstants.ACTION_UPGRADE.equals(action)) {
                    Uri uri = Uri.parse("market://details?id=sk.ab.herbsplus");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=sk.ab.herbsplus")));
                    }
                } else {
                    Intent intent = new Intent(this, getFilterPlantsActivityClass());
                    startActivity(intent);
                }
            }
        } else {
            Intent intent = new Intent(this, getFilterPlantsActivityClass());
            startActivity(intent);
        }
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
