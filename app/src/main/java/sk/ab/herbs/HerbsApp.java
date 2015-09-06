package sk.ab.herbs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

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
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import sk.ab.commons.BaseFilterFragment;
import sk.ab.herbs.fragments.ColorOfFlowers;
import sk.ab.herbs.fragments.Habitats;
import sk.ab.herbs.fragments.NumbersOfPetals;
import sk.ab.herbs.service.GoogleClient;
import sk.ab.herbs.service.HerbClient;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 12/1/14
 * Time: 9:20 PM
 * <p/>
 */
public class HerbsApp extends Application {
    public static String sDefSystemLanguage;
    private static final String PROPERTY_ID = "UA-56892333-1";
    private static DisplayImageOptions options;

    private Tracker tracker;
    private Locale locale;
    private List<BaseFilterFragment> filterAttributes;
    private Stack<BaseFilterFragment> backStack;
    private Map<Integer, Integer> filter;
    private boolean isLoading;
    private int count;

    private HerbClient herbClient;
    private GoogleClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        sDefSystemLanguage = Locale.getDefault().getLanguage();

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Boolean wasReset = preferences.getBoolean(Constants.RESET_KEY + BuildConfig.VERSION_CODE, false);

        if (!wasReset && (sDefSystemLanguage.equals(Constants.LANGUAGE_CS)
                || sDefSystemLanguage.equals(Constants.LANGUAGE_DE))) {
            editor.clear();
            editor.commit();
        }
        editor.putBoolean(Constants.RESET_KEY + BuildConfig.VERSION_CODE, true);
        editor.commit();

        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        Boolean changeLocale = preferences.getBoolean(Constants.CHANGE_LOCALE_KEY, false);

        if (changeLocale && !sDefSystemLanguage.equals(language)) {
            locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        } else {
            locale = Locale.getDefault();
        }

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        analytics.enableAutoActivityReports(this);
        tracker = analytics.newTracker(PROPERTY_ID);

        initImageLoader(getApplicationContext());

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumbersOfPetals());

        backStack = new Stack<>();
        filter = new HashMap<>();

        herbClient = new HerbClient();
        googleClient = new GoogleClient();
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public DisplayImageOptions getOptions() {
        return options;
    }

    public HerbClient getHerbClient() {
        return herbClient;
    }

    public GoogleClient getGoogleClient() {
        return googleClient;
    }

    public List<BaseFilterFragment> getFilterAttributes() {
        return filterAttributes;
    }

    public Stack<BaseFilterFragment> getBackStack() {
        return backStack;
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
