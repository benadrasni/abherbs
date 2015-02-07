package sk.ab.herbs;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.ab.commons.BaseFilterFragment;
import sk.ab.herbs.fragments.ColorOfFlowers;
import sk.ab.herbs.fragments.Habitats;
import sk.ab.herbs.fragments.NumbersOfPetals;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 12/1/14
 * Time: 9:20 PM
 * <p/>
 */
public class HerbsApp extends Application {
    private static final String PROPERTY_ID = "UA-56892333-1";

    private Tracker tracker;
    private List<BaseFilterFragment> filterAttributes;
    private Map<Integer, Integer> filter;
    private int count;

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        analytics.enableAutoActivityReports(this);

        tracker = analytics.newTracker(PROPERTY_ID);

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumbersOfPetals());

        filter = new HashMap<>();
    }

    public synchronized Tracker getTracker() {
        return tracker;
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return filterAttributes;
    }

    public Map<Integer, Integer> getFilter() {
        return filter;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
