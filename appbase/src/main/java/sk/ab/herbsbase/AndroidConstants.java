package sk.ab.herbsbase;

import android.content.res.Resources;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 20:02
 */
public class AndroidConstants {
    public final static String PACKAGE = "sk.ab.herbs";

    public final static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public final static int REQUEST_SIGN_IN = 100;
    public final static int REQUEST_TAKE_PHOTO = 101;
    public final static int REQUEST_PICK_PHOTO = 102;
    public final static int REQUEST_LOCATION = 103;

    public static final String BROADCAST_DOWNLOAD = "BROADCAST_DOWNLOAD";
    public static final String BROADCAST_UPLOAD = "BROADCAST_UPLOAD";
    public static final String EXTENDED_DATA_COUNT_ALL = "count_all";
    public static final String EXTENDED_DATA_COUNT_SYNCHONIZED = "count_synchronized";

    public final static String DATE_SKELETON = "ddMMMyyyyHHmm";
    public final static int DEFAULT_CACHE_SIZE = 50;
    public final static int IMAGE_SIZE = 512;
    public final static int ICON_OPACITY = 125;

    public final static long MIN_CLICK_INTERVAL = 600;

    public final static String EMAIL = "whatsthoseflowers@gmail.com";
    public final static String WEB_URL = "https://whatsthatflower.com/";
    public final static int VERSION_1_2_7 = 36;
    public final static int VERSION_1_3_1 = 42;

    public final static String STATE_FILTER = "filter";
    public final static String STATE_FILTER_CLEAR = "filter_clear";
    public final static String STATE_FILTER_POSITION = "filter_position";
    public final static String STATE_LIST_PATH = "list_path";
    public final static String STATE_PLANT_LIST_COUNT = "plant_list_count";
    public final static String STATE_PLANT = "plant";
    public final static String STATE_TRANSLATION_IN_LANGUAGE = "translation_in_language";
    public final static String STATE_TRANSLATION_IN_LANGUAGE_GT = "translation_in_language_gt";
    public final static String STATE_TRANSLATION_IN_ENGLISH = "translation_in_english";
    public final static String STATE_FROM_NOTIFICATION = "from_notification";
    public final static String STATE_LIST_POSITION = "list_position";
    public final static String STATE_OBSERVATION = "latitude";
    public final static String STATE_LATITUDE = "latitude";
    public final static String STATE_LONGITUDE = "longitude";
    public final static String STATE_IS_SUBSCRIBED = "is_subscribed";

    public final static String RATE = "RATE";
    public final static int RATE_COUNTER = 5;
    public final static int RATE_NEVER = -1;
    public final static int RATE_NO = 0;
    public final static int RATE_SHOW = 1;
    public final static int RATE_DONE = 2;

    public final static String STORAGE_ENDPOINT = "http://storage.googleapis.com/abherbs-resources/";
    public final static String STORAGE_FAMILIES = "families/";
    public final static String STORAGE_PHOTOS = "photos/";
    public final static String DEFAULT_EXTENSION = ".webp";
    public static final String THUMBNAIL_DIR = "/.thumbnails";

    public final static String RES_TAXONOMY_PREFIX = "taxonomy_";
    public final static String ROOT_TAXON = "Eukaryota";
    public final static String TAXON_ORDO = "Ordo";
    public final static String TAXON_FAMILIA = "Familia";

    public final static String LANGUAGE_DEFAULT_KEY = "language_default";
    public final static String LANGUAGE_GT_SUFFIX = "-GT";
    public final static String CACHE_SIZE_KEY = "cache_size";
    public final static String SUBSCRIBE_FACT_KEY = "subscribe_fact_flowers";
    public final static String SUBSCRIBE_FACT_TOPIC = "fact_flowers";
    public final static String RESET_KEY = "reset";
    public final static String RATE_STATE_KEY = "rate_state";
    public final static String RATE_COUNT_KEY = "rate_count";
    public final static String SHOWCASE_FILTER_KEY = "showcase_filter";
    public final static String SHOWCASE_DISPLAY_KEY = "showcase_display";
    public final static String TOKEN_KEY = "token";

    public final static String LANGUAGE_NOT_SUPPORTED = " (not supported)";
    public final static String LANGUAGE_EN = "en";
    public final static String LANGUAGE_SK = "sk";
    public final static String LANGUAGE_CS = "cs";
    public final static String LANGUAGE_LA = "la";
    public final static String[] LANGUAGES = {"العربية", "Čeština", "Dansk", "Deutsch", "English", "Español", "Eesti", "فارسی", "Français", "हिन्दी", "עברית",
            "Hrvatski", "Italiano", "Latviešu", "Lietuvių", "Magyar", "Nederlands", "日本語", "Norsk", "ਪੰਜਾਬੀ", "Polski", "Português", "Română",
            "Русский", "Slovenčina", "Slovenščina", "Српски / srpski", "Svenska", "Suomi", "Українська"};
    public final static String[] LANGUAGE_CODES = {"ar", "cs", "da", "de", "en", "es", "et", "fa", "fr", "hi", "he", "hr", "it", "lv", "lt", "hu", "nl",
            "ja", "no", "pa", "pl", "pt", "ro", "ru", "sk", "sl", "sr", "sv", "fi", "uk"};

    public final static String HTML_BOLD = "<b>";
    public final static String HTML_BOLD_CLOSE = "</b>";

    public final static String ITEM_LEGEND = "Legend";
    public final static String ITEM_SETTINGS = "Settings";
    public final static String ITEM_FEEDBACK = "Feedback";
    public final static String ITEM_HELP = "Help";
    public final static String ITEM_ABOUT = "About";
    public final static String ITEM_CC0 = "Creative Commons Zero";
    public final static String ITEM_LOGIN = "Login";
    public final static String ITEM_LOGOUT = "Logout";
    public final static String ITEM_SUBSCRIPTION = "Subscription";

    public final static String SEPARATOR = "/";
    public final static String ACTION = "action";
    public final static String ACTION_UPGRADE = "upgrade";

    public final static String FIREBASE_COUNTS = "counts_4";
    public final static String FIREBASE_LISTS = "lists_4";
    public final static String FIREBASE_PLANTS = "plants";
    public final static String FIREBASE_PLANTS_TO_UPDATE = "plants_to_update";
    public final static String FIREBASE_FAMILIES_TO_UPDATE = "families_to_update";
    public final static String FIREBASE_APG_IV = "APG IV";
    public final static String FIREBASE_SEARCH = "search";
    public final static String FIREBASE_TRANSLATIONS = "translations";
    public final static String FIREBASE_OBSERVATIONS = "observations";
    public final static String FIREBASE_OBSERVATIONS_BY_USERS = "by users";
    public final static String FIREBASE_OBSERVATIONS_BY_DATE = "by date";
    public final static String FIREBASE_OBSERVATIONS_BY_PLANT = "by plant";
    public final static String FIREBASE_OBSERVATIONS_PUBLIC = "public";
    public final static String FIREBASE_OBSERVATIONS_STATUS = "status";
    public final static String FIREBASE_USERS = "users";
    public final static String FIREBASE_USERS_TOKEN = "token";
    public final static String FIREBASE_VERSIONS = "versions";

    public final static String FIREBASE_APG_TYPE = "type";
    public final static String FIREBASE_APG_NAMES = "names";
    public final static String FIREBASE_APG_LIST = "list";
    public final static String FIREBASE_APG_COUNT = "count";

    public final static String FIREBASE_DATA_COUNT = "count";
    public final static String FIREBASE_DATA_LIST = "list";
    public final static String FIREBASE_DATA_PATH = "path";

    public final static String FIREBASE_APG_UNKNOWN_TYPE = "unknown";

    public static int getValueResource(Resources resources, String value) {
        int result = 0;
        if (value.equals(resources.getString(R.string.cof_white))) {
            result = R.string.color_white;
        } else if (value.equals(resources.getString(R.string.cof_yellow))) {
            result = R.string.color_yellow;
        } else if (value.equals(resources.getString(R.string.cof_red))) {
            result = R.string.color_red;
        } else if (value.equals(resources.getString(R.string.cof_blue))) {
            result = R.string.color_blue;
        } else if (value.equals(resources.getString(R.string.cof_green))) {
            result = R.string.color_green;
        } else if (value.equals(resources.getString(R.string.ph_meadows))) {
            result = R.string.habitat_meadow;
        } else if (value.equals(resources.getString(R.string.ph_gardens))) {
            result = R.string.habitat_garden;
        } else if (value.equals(resources.getString(R.string.ph_moorlands))) {
            result = R.string.habitat_wetland;
        } else if (value.equals(resources.getString(R.string.ph_woodlands))) {
            result = R.string.habitat_forest;
        } else if (value.equals(resources.getString(R.string.ph_rocks))) {
            result = R.string.habitat_rock;
        } else if (value.equals(resources.getString(R.string.ph_trees))) {
            result = R.string.habitat_tree;
        } else if (value.equals(resources.getString(R.string.nop_4))) {
            result = R.string.petal_4;
        } else if (value.equals(resources.getString(R.string.nop_5))) {
            result = R.string.petal_5;
        } else if (value.equals(resources.getString(R.string.nop_many))) {
            result = R.string.petal_many;
        } else if (value.equals(resources.getString(R.string.nop_bisymmetric))) {
            result = R.string.petal_bisymmetric;
        }

        return result;
    }
}
