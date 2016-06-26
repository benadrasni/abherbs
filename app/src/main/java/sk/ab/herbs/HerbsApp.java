package sk.ab.herbs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.analytics.GoogleAnalytics;
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

import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.service.GoogleClient;
import sk.ab.common.service.HerbCloudClient;
import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.fragments.ColorOfFlowers;
import sk.ab.herbs.fragments.Habitats;
import sk.ab.herbs.fragments.NumberOfPetals;

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
    private List<BaseFilterFragment> filterAttributes;
    private Stack<BaseFilterFragment> backStack;
    private Map<String, String> filter;
    private List<PlantHeader> plantList;
    private Plant plant;
    private boolean isLoading;
    private int count;

    private HerbCloudClient herbCloudClient;
    private GoogleClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        sDefSystemLanguage = Locale.getDefault().getLanguage();

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Boolean wasReset = preferences.getBoolean(Constants.RESET_KEY + BuildConfig.VERSION_CODE, false);
        if (!wasReset) {
            editor.putString(Constants.LANGUAGE_DEFAULT_KEY, sDefSystemLanguage);
        }

        int rateCounter = preferences.getInt(Constants.RATE_COUNT_KEY, Constants.RATE_COUNTER);
        rateCounter--;
        editor.putInt(Constants.RATE_COUNT_KEY, rateCounter);

        int rateState = preferences.getInt(Constants.RATE_STATE_KEY, Constants.RATE_NO);
        if (rateCounter <= 0 && rateState == Constants.RATE_NO) {
            editor.putInt(Constants.RATE_STATE_KEY, Constants.RATE_SHOW);
        }

        editor.putBoolean(Constants.RESET_KEY + BuildConfig.VERSION_CODE, true);
        editor.apply();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.enableAutoActivityReports(this);
        tracker = analytics.newTracker(PROPERTY_ID);

        initImageLoader(getApplicationContext());

        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumberOfPetals());

        backStack = new Stack<>();
        filter = new HashMap<>();

        herbCloudClient = new HerbCloudClient();
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

    public DisplayImageOptions getOptions() {
        return options;
    }

    public HerbCloudClient getHerbCloudClient() {
        return herbCloudClient;
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

    public Map<String, String> getFilter() {
        return filter;
    }

    public List<PlantHeader> getPlantList() {
        return plantList;
    }

    public void setPlantList(List<PlantHeader> plantList) {
        this.plantList = plantList;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
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
