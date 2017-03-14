package sk.ab.herbsbase;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 20:02
 */
public class AndroidConstants {
    public final static String PACKAGE = "sk.ab.herbs";
    public final static int LIST_THRESHOLD = 20;

    public final static String EMAIL = "whatsthoseflowers@gmail.com";
    public final static int VERSION_1_2_7 = 36;
    public final static int VERSION_1_3_1 = 42;

    public final static String STATE_FILTER = "filter";
    public final static String STATE_FILTER_CLEAR = "filter_clear";
    public final static String STATE_FILTER_POSITION = "filter_position";
    public final static String STATE_PLANT_LIST = "plant_list";
    public final static String STATE_PLANT_LIST_COUNT = "plant_list_count";
    public final static String STATE_PLANT = "plant";

    public final static int RATE_COUNTER = 5;
    public final static int RATE_NEVER = -1;
    public final static int RATE_NO = 0;
    public final static int RATE_SHOW = 1;
    public final static int RATE_DONE = 2;

    public final static String RATE_STATUS_DONE = "done";
    public final static String RATE_STATUS_NEVER = "never";
    public final static String RATE_STATUS_LATER = "later";

    public final static String STORAGE_ENDPOINT = "http://storage.googleapis.com/abherbs/.families/";
    public final static String DEFAULT_EXTENSION = ".webp";

    public final static String RES_TAXONOMY_PREFIX = "taxonomy_";

    public final static Map<String, Integer> LANGUAGES;
    public final static String LANGUAGE_DEFAULT_KEY = "language_default";
    public final static String CHANGE_LOCALE_KEY = "change_locale";
    public final static String PROPOSE_TRANSLATION_KEY = "propose_translation";
    public final static String RESET_KEY = "reset";
    public final static String RATE_STATE_KEY = "rate_state";
    public final static String RATE_COUNT_KEY = "rate_count";
    public final static String SHOWCASE_FILTER_KEY = "showcase_filter";
    public final static String SHOWCASE_DISPLAY_KEY = "showcase_display";
    public final static String LANGUAGE_EN = "en";
    public final static String LANGUAGE_SK = "sk";
    public final static String LANGUAGE_CS = "cs";
    public final static String LANGUAGE_DE = "de";
    public final static String LANGUAGE_FR = "fr";
    public final static String LANGUAGE_ES = "es";
    public final static String LANGUAGE_RU = "ru";
    public final static String LANGUAGE_IT = "it";
    public final static String LANGUAGE_JA = "ja";
    public final static String LANGUAGE_PT = "pt";
    public final static String LANGUAGE_ZH = "zh";
    public final static String HEIGHT_UNIT = "cm";

    public final static String DEFAULT_LANGUAGE = LANGUAGE_EN;
    public final static String ORIGINAL_LANGUAGE = "original";

    public final static String ITEM_LEGEND = "Legend";
    public final static String ITEM_SETTINGS = "Settings";
    public final static String ITEM_FEEDBACK = "Feedback";
    public final static String ITEM_HELP = "Help";
    public final static String ITEM_ABOUT = "About";

    public final static String FIREBASE_SEPARATOR = "/";

    public final static String FIREBASE_FILTERS = "filters";
    public final static String FIREBASE_PLANTS = "plants";
    public final static String FIREBASE_APG_III = "APG III";
    public final static String FIREBASE_NAMES = "names";

    public final static String FIREBASE_COUNT = "count";
    public final static String FIREBASE_LIST = "list";

    static {
        LANGUAGES = new HashMap<String, Integer>();
        LANGUAGES.put(LANGUAGE_EN, 0);
        LANGUAGES.put(LANGUAGE_SK, 1);
        LANGUAGES.put(LANGUAGE_CS, 2);
        LANGUAGES.put(LANGUAGE_DE, 3);
        LANGUAGES.put(LANGUAGE_FR, 4);
        LANGUAGES.put(LANGUAGE_ES, 5);
        LANGUAGES.put(LANGUAGE_PT, 9);
    }

    public static int getLanguage(String sLanguage) {
        Integer language = LANGUAGES.get(sLanguage);
        return language == null ? 0 : language;
    }

    public static String getLanguage(Integer language) {
        for (Map.Entry<String, Integer> entry : LANGUAGES.entrySet()) {
            if (language == entry.getValue().intValue()) {
                return entry.getKey();
            }
        }
        return LANGUAGE_EN;
    }

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
