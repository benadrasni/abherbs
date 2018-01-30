package sk.ab.herbsbase;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

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
    public static Locale sDefSystemLocale;

    private static DisplayImageOptions options;

    protected List<BaseFilterFragment> filterAttributes;
    private Stack<BaseFilterFragment> backStack;
    private boolean isLoading;

    private GoogleClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        sDefSystemLocale = Locale.getDefault();

        backStack = new Stack<>();

        googleClient = new GoogleClient();
    }

    public static void initImageLoader(Context context, int cacheSize) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(cacheSize * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    
    public static boolean isConnectedToWifi(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public abstract boolean isOffline();

    public DisplayImageOptions getOptions() {
        return options;
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

    public abstract void setToken(String token);
}
