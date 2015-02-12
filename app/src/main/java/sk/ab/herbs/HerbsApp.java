package sk.ab.herbs;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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

    private static DisplayImageOptions options;

    private Tracker tracker;
    private List<BaseFilterFragment> filterAttributes;
    private Map<Integer, Integer> filter;
    private boolean isLoading;
    private int count;

    @Override
    public void onCreate() {
        super.onCreate();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        analytics.enableAutoActivityReports(this);

        tracker = analytics.newTracker(PROPERTY_ID);

        initImageLoader(getApplicationContext());

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumbersOfPetals());

        filter = new HashMap<>();
    }

    public synchronized Tracker getTracker() {
        return tracker;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    public DisplayImageOptions getOptions() {
        return options;
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
