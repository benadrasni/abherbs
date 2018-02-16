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
            String path = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_PATH);
            if (count != null) {
                Log.d(TAG, path + " (" + count + ")");
                callProperActivity(Integer.parseInt(count), path);
            } else {
                Intent intent = new Intent(this, getFilterPlantsActivityClass());
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, getFilterPlantsActivityClass());
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
