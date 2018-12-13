package sk.ab.herbsbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.ab.common.Constants;

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
    public final static int MAX_CACHE_SIZE = 2047;
    public final static int IMAGE_SIZE = 512;
    public final static int ICON_OPACITY = 125;

    public final static long MIN_CLICK_INTERVAL = 600;

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

    public final static int RATE_COUNTER = 5;
    public final static int RATE_NEVER = -1;
    public final static int RATE_NO = 0;
    public final static int RATE_SHOW = 1;
    public final static int RATE_DONE = 2;

    public final static String STORAGE_ENDPOINT = "https://storage.googleapis.com/abherbs-resources/";
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
    public final static String MY_REGION_KEY = "my_region";
    public final static String ALWAYS_MY_REGION_KEY = "always_my_region";

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
    public final static String ACTION_BROWSE = "browse";
    public final static String ACTION_BROWSE_URI = "uri";

    public final static String FIREBASE_COUNTS = "counts_4_v2";
    public final static String FIREBASE_LISTS = "lists_4_v2";
    public final static String FIREBASE_PLANTS = "plants_v2";
    public final static String FIREBASE_PLANTS_HEADERS = "plants_headers";
    public final static String FIREBASE_PLANTS_TO_UPDATE = "plants_to_update";
    public final static String FIREBASE_FAMILIES_TO_UPDATE = "families_to_update";
    public final static String FIREBASE_APG_IV = "APG IV_v2";
    public final static String FIREBASE_TRANSLATIONS = "translations";
    public final static String FIREBASE_TRANSLATIONS_TAXONOMY = "translations_taxonomy";
    public final static String FIREBASE_SEARCH = "search_v2";
    public final static String FIREBASE_PHOTO_SEARCH = "search_photo";
    public final static String FIREBASE_OBSERVATIONS = "observations";
    public final static String FIREBASE_SETTINGS = "settings";
    public final static String FIREBASE_SETTINGS_GENERIC_LABELS = "generic_labels";
    public final static String FIREBASE_OBSERVATIONS_BY_USERS = "by users";
    public final static String FIREBASE_OBSERVATIONS_BY_DATE = "by date";
    public final static String FIREBASE_OBSERVATIONS_BY_PLANT = "by plant";
    public final static String FIREBASE_OBSERVATIONS_PUBLIC = "public";
    public final static String FIREBASE_OBSERVATIONS_STATUS = "status";
    public final static String FIREBASE_STATUS_PRIVATE = "private";
    public final static String FIREBASE_STATUS_PUBLIC = "public";
    public final static String FIREBASE_STATUS_INCOMPLETE = "incomplete";
    public final static String FIREBASE_STATUS_REVIEW = "review";
    public final static String STATE_SEARCH_TEXT = "search_text";
    public final static String FIREBASE_USERS = "users";
    public final static String FIREBASE_USERS_TOKEN = "token";
    public final static String FIREBASE_VERSIONS = "versions";
    public final static String FIREBASE_SEARCH_BY_PHOTO = "search by photo";

    public final static String FIREBASE_NAME = "name";
    public final static String FIREBASE_APG_TYPE = "type";
    public final static String FIREBASE_APG_LIST = "list";
    public final static String FIREBASE_APG_COUNT = "count";

    public final static String FIREBASE_DATA_COUNT = "count";
    public final static String FIREBASE_DATA_LIST = "list";
    public final static String FIREBASE_DATA_PATH = "path";

    public final static String FIREBASE_APG_UNKNOWN_TYPE = "unknown";

    public static final Map<String, Integer> filterResources;
    static {
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put(Constants.COLOR_OF_FLOWERS + "_1", R.string.color_white);
        aMap.put(Constants.COLOR_OF_FLOWERS + "_2", R.string.color_yellow);
        aMap.put(Constants.COLOR_OF_FLOWERS + "_3", R.string.color_red);
        aMap.put(Constants.COLOR_OF_FLOWERS + "_4", R.string.color_blue);
        aMap.put(Constants.COLOR_OF_FLOWERS + "_5", R.string.color_green);

        aMap.put(Constants.HABITAT + "_1", R.string.habitat_meadow);
        aMap.put(Constants.HABITAT + "_2", R.string.habitat_garden);
        aMap.put(Constants.HABITAT + "_3", R.string.habitat_wetland);
        aMap.put(Constants.HABITAT + "_4", R.string.habitat_forest);
        aMap.put(Constants.HABITAT + "_5", R.string.habitat_rock);
        aMap.put(Constants.HABITAT + "_6", R.string.habitat_tree);

        aMap.put(Constants.NUMBER_OF_PETALS + "_1", R.string.petal_4);
        aMap.put(Constants.NUMBER_OF_PETALS + "_2", R.string.petal_5);
        aMap.put(Constants.NUMBER_OF_PETALS + "_3", R.string.petal_many);
        aMap.put(Constants.NUMBER_OF_PETALS + "_4", R.string.petal_bisymmetric);

        aMap.put(Constants.DISTRIBUTION + "_10", R.string.northern_europe);
        aMap.put(Constants.DISTRIBUTION + "_11", R.string.middle_europe);
        aMap.put(Constants.DISTRIBUTION + "_12", R.string.southwestern_europe);
        aMap.put(Constants.DISTRIBUTION + "_13", R.string.southeastern_europe);
        aMap.put(Constants.DISTRIBUTION + "_14", R.string.eastern_europe);
        aMap.put(Constants.DISTRIBUTION + "_20", R.string.northern_africa);
        aMap.put(Constants.DISTRIBUTION + "_21", R.string.macaronesia);
        aMap.put(Constants.DISTRIBUTION + "_22", R.string.west_tropical_africa);
        aMap.put(Constants.DISTRIBUTION + "_23", R.string.west_central_tropical_africa);
        aMap.put(Constants.DISTRIBUTION + "_24", R.string.northeast_tropical_africa);
        aMap.put(Constants.DISTRIBUTION + "_25", R.string.east_tropical_africa);
        aMap.put(Constants.DISTRIBUTION + "_26", R.string.south_tropical_africa);
        aMap.put(Constants.DISTRIBUTION + "_27", R.string.southern_africa);
        aMap.put(Constants.DISTRIBUTION + "_28", R.string.middle_atlantic_ocean);
        aMap.put(Constants.DISTRIBUTION + "_29", R.string.western_indian_ocean);
        aMap.put(Constants.DISTRIBUTION + "_30", R.string.siberia);
        aMap.put(Constants.DISTRIBUTION + "_31", R.string.russian_far_east);
        aMap.put(Constants.DISTRIBUTION + "_32", R.string.middle_asia);
        aMap.put(Constants.DISTRIBUTION + "_33", R.string.caucasus);
        aMap.put(Constants.DISTRIBUTION + "_34", R.string.western_asia);
        aMap.put(Constants.DISTRIBUTION + "_35", R.string.arabian_peninsula);
        aMap.put(Constants.DISTRIBUTION + "_36", R.string.china);
        aMap.put(Constants.DISTRIBUTION + "_37", R.string.mongolia);
        aMap.put(Constants.DISTRIBUTION + "_38", R.string.eastern_asia);
        aMap.put(Constants.DISTRIBUTION + "_40", R.string.indian_subcontinent);
        aMap.put(Constants.DISTRIBUTION + "_41", R.string.indochina);
        aMap.put(Constants.DISTRIBUTION + "_42", R.string.malesia);
        aMap.put(Constants.DISTRIBUTION + "_43", R.string.papuasia);
        aMap.put(Constants.DISTRIBUTION + "_50", R.string.australia);
        aMap.put(Constants.DISTRIBUTION + "_51", R.string.new_zealand);
        aMap.put(Constants.DISTRIBUTION + "_60", R.string.southwestern_pacific);
        aMap.put(Constants.DISTRIBUTION + "_61", R.string.south_central_pacific);
        aMap.put(Constants.DISTRIBUTION + "_62", R.string.northwestern_pacific);
        aMap.put(Constants.DISTRIBUTION + "_63", R.string.north_central_pacific);
        aMap.put(Constants.DISTRIBUTION + "_70", R.string.subarctic_america);
        aMap.put(Constants.DISTRIBUTION + "_71", R.string.western_canada);
        aMap.put(Constants.DISTRIBUTION + "_72", R.string.eastern_canada);
        aMap.put(Constants.DISTRIBUTION + "_73", R.string.northwestern_usa);
        aMap.put(Constants.DISTRIBUTION + "_74", R.string.north_central_usa);
        aMap.put(Constants.DISTRIBUTION + "_75", R.string.northeastern_usa);
        aMap.put(Constants.DISTRIBUTION + "_76", R.string.southwestern_usa);
        aMap.put(Constants.DISTRIBUTION + "_77", R.string.south_central_usa);
        aMap.put(Constants.DISTRIBUTION + "_78", R.string.southeastern_usa);
        aMap.put(Constants.DISTRIBUTION + "_79", R.string.mexico);
        aMap.put(Constants.DISTRIBUTION + "_80", R.string.central_america);
        aMap.put(Constants.DISTRIBUTION + "_81", R.string.caribbean);
        aMap.put(Constants.DISTRIBUTION + "_82", R.string.northern_south_america);
        aMap.put(Constants.DISTRIBUTION + "_83", R.string.western_south_america);
        aMap.put(Constants.DISTRIBUTION + "_84", R.string.brazil);
        aMap.put(Constants.DISTRIBUTION + "_85", R.string.southern_south_america);
        aMap.put(Constants.DISTRIBUTION + "_90", R.string.subantarctic_islands);
        aMap.put(Constants.DISTRIBUTION + "_91", R.string.antarctic_continent);

        filterResources = Collections.unmodifiableMap(aMap);
    }

    public static final Map<Integer, List<Map<String, Integer>>> wgsrpd;
    static {
        Map<Integer, List<Map<String, Integer>>> aMap = new HashMap<>();

        List<Map<String, Integer>> wgsrpdFirstLevel1 = new ArrayList<>();
        Map<String, Integer> wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northern_europe);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northern_europe);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_10);
        wgsrpdFirstLevel1.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.middle_europe);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_middle_europe);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_11);
        wgsrpdFirstLevel1.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southwestern_europe);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southwestern_europe);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_12);
        wgsrpdFirstLevel1.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southeastern_europe);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southeastern_europe);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_13);
        wgsrpdFirstLevel1.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.eastern_europe);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_eastern_europe);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_14);
        wgsrpdFirstLevel1.add(wgsrpdSecondLevel);

        aMap.put(1, wgsrpdFirstLevel1);

        List<Map<String, Integer>> wgsrpdFirstLevel2 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northern_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northern_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_20);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.macaronesia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_macaronesia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_21);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.west_tropical_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_west_tropical_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_22);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.west_central_tropical_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_central_tropical_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_23);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northeast_tropical_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northeast_tropical_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_24);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.east_tropical_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_east_tropical_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_25);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.south_tropical_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_south_tropical_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_26);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southern_africa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southern_africa);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_27);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.middle_atlantic_ocean);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_middle_atlantic_ocean);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_28);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.western_indian_ocean);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_western_indian_ocean);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_29);
        wgsrpdFirstLevel2.add(wgsrpdSecondLevel);

        aMap.put(2, wgsrpdFirstLevel2);

        List<Map<String, Integer>> wgsrpdFirstLevel3 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.siberia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_siberia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_30);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.russian_far_east);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_russian_far_east);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_31);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.middle_asia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_middle_asia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_32);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.caucasus);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_caucasus);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_33);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.western_asia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_western_asia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_34);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.arabian_peninsula);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_arabian_peninsula);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_35);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.china);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_china);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_36);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.mongolia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_mongolia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_37);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.eastern_asia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_east_asia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_38);
        wgsrpdFirstLevel3.add(wgsrpdSecondLevel);

        aMap.put(3, wgsrpdFirstLevel3);

        List<Map<String, Integer>> wgsrpdFirstLevel4 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.indian_subcontinent);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_indian_subcontinent);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_40);
        wgsrpdFirstLevel4.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.indochina);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_indochina);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_41);
        wgsrpdFirstLevel4.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.malesia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_malesia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_42);
        wgsrpdFirstLevel4.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.papuasia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_papuasia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_43);
        wgsrpdFirstLevel4.add(wgsrpdSecondLevel);

        aMap.put(4, wgsrpdFirstLevel4);

        List<Map<String, Integer>> wgsrpdFirstLevel5 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.australia);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_australia);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_50);
        wgsrpdFirstLevel5.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.new_zealand);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_new_zealand);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_51);
        wgsrpdFirstLevel5.add(wgsrpdSecondLevel);

        aMap.put(5, wgsrpdFirstLevel5);

        List<Map<String, Integer>> wgsrpdFirstLevel6 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southwestern_pacific);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southwestern_pacific);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_60);
        wgsrpdFirstLevel6.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.south_central_pacific);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_south_central_pacific);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_61);
        wgsrpdFirstLevel6.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northwestern_pacific);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northwestern_pacific);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_62);
        wgsrpdFirstLevel6.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.north_central_pacific);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_north_central_pacific);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_63);
        wgsrpdFirstLevel6.add(wgsrpdSecondLevel);

        aMap.put(6, wgsrpdFirstLevel6);

        List<Map<String, Integer>> wgsrpdFirstLevel7 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.subarctic_america);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_subarctic_america);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_70);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.western_canada);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_western_canada);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_71);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.eastern_canada);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_eastern_canada);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_72);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northwestern_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northwestern_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_73);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.north_central_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_north_central_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_74);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northeastern_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northeastern_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_75);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southwestern_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southwestern_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_76);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.south_central_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_south_central_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_77);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southeastern_usa);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southeastern_united_states);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_78);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.mexico);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_mexico);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_79);
        wgsrpdFirstLevel7.add(wgsrpdSecondLevel);

        aMap.put(7, wgsrpdFirstLevel7);

        List<Map<String, Integer>> wgsrpdFirstLevel8 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.central_america);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_central_america);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_80);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.caribbean);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_caribbean);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_81);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.northern_south_america);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_northern_south_america);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_82);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.western_south_america);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_western_south_america);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_83);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.brazil);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_brazil);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_84);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.southern_south_america);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_southern_south_america);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_85);
        wgsrpdFirstLevel8.add(wgsrpdSecondLevel);

        aMap.put(8, wgsrpdFirstLevel8);

        List<Map<String, Integer>> wgsrpdFirstLevel9 = new ArrayList<>();
        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.subantarctic_islands);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_subantarctic_islands);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_90);
        wgsrpdFirstLevel9.add(wgsrpdSecondLevel);

        wgsrpdSecondLevel =  new HashMap<>();
        wgsrpdSecondLevel.put("text", R.string.antarctic_continent);
        wgsrpdSecondLevel.put("drawable", R.drawable.wgsrpd_antarctic_continent);
        wgsrpdSecondLevel.put("value", R.string.wgsrpd_91);
        wgsrpdFirstLevel9.add(wgsrpdSecondLevel);

        aMap.put(9, wgsrpdFirstLevel9);

        wgsrpd = Collections.unmodifiableMap(aMap);
    }
}
