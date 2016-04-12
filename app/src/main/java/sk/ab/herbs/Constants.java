package sk.ab.herbs;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 20:02
 */
public class Constants {
    public final static int LIST_THRESHOLD = 20;

    public final static int FAB_FONT_SIZE = 70;

    public final static String EMAIL = "whatsthoseflowers@gmail.com";

    public final static String STORAGE_ENDPOINT = "http://storage.googleapis.com/abherbs/.families/";
    public final static String DEFAULT_EXTENSION = ".webp";

    public final static int FLOWERS = 12;

    public final static String FAMILY = "family";
    public final static String RESOURCE_SEPARATOR = "_";

    public final static int COLOR_OF_FLOWERS_ID = 224;
    public final static int HABITATS_ID = 226;
    public final static int NUMBER_OF_PETALS_ID = 238;

    public final static int PLANT_NAME = 49;
    public final static int PLANT_ALT_NAMES = 398;
    public final static int PLANT_PHOTO_URL = 395;
    public final static int PLANT_SOURCE_URL = 402;
    public final static int PLANT_IMAGE_URL = 293;
    public final static int PLANT_HEIGHT_FROM = 403;
    public final static int PLANT_HEIGHT_TO = 404;
    public final static int PLANT_FLOWERING_FROM = 405;
    public final static int PLANT_FLOWERING_TO = 406;
    public final static int PLANT_DESCRIPTION = 400;
    public final static int PLANT_STEM = 290;
    public final static int PLANT_FLOWER = 287;
    public final static int PLANT_FRUIT = 288;
    public final static int PLANT_LEAF = 289;
    public final static int PLANT_HABITAT = 291;
    public final static int PLANT_INFLORESCENCE = 396;
    public final static int PLANT_DOMAIN = 383;
    public final static int PLANT_KINGDOM = 384;
    public final static int PLANT_SUBKINGDOM = 385;
    public final static int PLANT_LINE = 386;
    public final static int PLANT_BRANCH = 387;
    public final static int PLANT_PHYLUM = 388;
    public final static int PLANT_CLS = 389;
    public final static int PLANT_ORDER = 390;
    public final static int PLANT_FAMILY = 391;
    public final static int PLANT_GENUS = 392;
    public final static int PLANT_SPECIES_LATIN = 286;
    public final static int PLANT_TOXICITY_CLASS = 409;
    public final static int PLANT_TOXICITY = 407;
    public final static int PLANT_HERBALISM = 408;


    public final static int DEFAULT_LANGUAGE = 0;
    public final static int ORIGINAL_LANGUAGE = -1;

    public final static Map<String, Integer> LANGUAGES;
    public final static String LANGUAGE_DEFAULT_KEY = "language_default";
    public final static String CHANGE_LOCALE_KEY = "change_locale";
    public final static String PROPOSE_TRANSLATION_KEY = "propose_translation";
    public final static String RESET_KEY = "reset";
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

    public static int getValueResource(Resources resources, int valueId) {
        int result = 0;
        if (valueId == resources.getInteger(R.integer.cof_white)) {
            result = R.string.color_white;
        } else if (valueId == resources.getInteger(R.integer.cof_yellow)) {
            result = R.string.color_yellow;
        } else if (valueId == resources.getInteger(R.integer.cof_red)) {
            result = R.string.color_red;
        } else if (valueId == resources.getInteger(R.integer.cof_blue)) {
            result = R.string.color_blue;
        } else if (valueId == resources.getInteger(R.integer.cof_green)) {
            result = R.string.color_green;
        } else if (valueId == resources.getInteger(R.integer.ph_meadows)) {
            result = R.string.habitat_meadow;
        } else if (valueId == resources.getInteger(R.integer.ph_gardens)) {
            result = R.string.habitat_garden;
        } else if (valueId == resources.getInteger(R.integer.ph_moorlands)) {
            result = R.string.habitat_wetland;
        } else if (valueId == resources.getInteger(R.integer.ph_woodlands)) {
            result = R.string.habitat_forest;
        } else if (valueId == resources.getInteger(R.integer.ph_rocks)) {
            result = R.string.habitat_rock;
        } else if (valueId == resources.getInteger(R.integer.ph_trees)) {
            result = R.string.habitat_tree;
        } else if (valueId == resources.getInteger(R.integer.nop_4)) {
            result = R.string.nop_4;
        } else if (valueId == resources.getInteger(R.integer.nop_5)) {
            result = R.string.nop_5;
        } else if (valueId == resources.getInteger(R.integer.nop_many)) {
            result = R.string.nop_many;
        } else if (valueId == resources.getInteger(R.integer.nop_bisymmetric)) {
            result = R.string.nop_bisymmetric;
        }

        return result;
    }
}
