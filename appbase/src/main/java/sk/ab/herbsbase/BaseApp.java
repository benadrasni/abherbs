package sk.ab.herbsbase;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;
import java.util.Locale;
import java.util.Stack;

import sk.ab.common.service.FirebaseClient;
import sk.ab.common.service.GoogleClient;
import sk.ab.common.service.HerbCloudClient;
import sk.ab.herbsbase.commons.BaseFilterFragment;

/**
 *
 * Created by adrian on 25. 2. 2017.
 */

public abstract class BaseApp extends Application {
    public static String sDefSystemLanguage;

    private static DisplayImageOptions options;

    protected Tracker tracker;
    protected List<BaseFilterFragment> filterAttributes;
    private Stack<BaseFilterFragment> backStack;
    private boolean isLoading;

    private HerbCloudClient herbCloudClient;
    private FirebaseClient firebaseClient;
    private GoogleClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        sDefSystemLanguage = Locale.getDefault().getLanguage();

        initImageLoader(getApplicationContext());

        backStack = new Stack<>();

        herbCloudClient = new HerbCloudClient();
        firebaseClient = new FirebaseClient();
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

    public FirebaseClient getFirebaseClient() {
        return firebaseClient;
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}
