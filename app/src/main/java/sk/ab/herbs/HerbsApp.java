package sk.ab.herbs;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 12/1/14
 * Time: 9:20 PM
 * <p/>
 */
public class HerbsApp extends Application {
    public HerbsApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        analytics.enableAutoActivityReports(this);
    }
}
