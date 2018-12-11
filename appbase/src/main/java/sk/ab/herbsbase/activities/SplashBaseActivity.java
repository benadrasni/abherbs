package sk.ab.herbsbase.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;


/**
 * Splash activity
 *
 * Created by adrian on 11. 3. 2017.
 */
public abstract class SplashBaseActivity extends SearchBaseActivity {

    private static final String TAG = "SplashBaseActivity";

    @Override
    protected int getLayoutResId() {
        return R.layout.splash_activity;
    }

    protected void startApplication() {
        Intent intent = new Intent(this, getFilterPlantsActivityClass());

        if (getIntent().getExtras() != null) {
            String count = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_COUNT);
            if (count != null) {
                String path = getIntent().getExtras().getString(AndroidConstants.FIREBASE_DATA_PATH);
                Log.d(TAG, path + " (" + count + ")");
                callProperActivity(Integer.parseInt(count), path);
                return;
            } else {
                String action = getIntent().getExtras().getString(AndroidConstants.ACTION);
                if (AndroidConstants.ACTION_BROWSE.equals(action)) {
                    String uriPath = getIntent().getExtras().getString(AndroidConstants.ACTION_BROWSE_URI);
                    if (uriPath != null) {
                        Uri uri = Uri.parse(uriPath);
                        Intent newIntent = new Intent(Intent.ACTION_VIEW, uri);
                        List<ResolveInfo> activities = this.getPackageManager().queryIntentActivities(intent,
                                PackageManager.MATCH_DEFAULT_ONLY);
                        if (activities.size() > 0) {
                            intent = newIntent;
                        }
                    }
                }
            }
        }
        startActivity(intent);
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
