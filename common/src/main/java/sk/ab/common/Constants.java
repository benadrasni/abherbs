package sk.ab.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 25.6.2016
 * Time: 20:02
 */
public class Constants {
    public final static String COMMONS = "commons";

    public final static int LIST_THRESHOLD = 20;

    public final static String LANGUAGE_LA = "la";
    public final static String LANGUAGE_EN = "en";
    public final static String LANGUAGE_SK = "sk";
    public final static String LANGUAGE_CS = "cs";
    public final static String LANGUAGE_DE = "de";
    public final static String LANGUAGE_FR = "fr";
    public final static String LANGUAGE_ES = "es";
    public final static String LANGUAGE_PT = "pt";

    public final static String HEIGHT_UNIT = "cm";

    public final static Map<Integer, String> LANGUAGES;
    static {
        LANGUAGES = new TreeMap<Integer, String>();
        LANGUAGES.put(1, LANGUAGE_SK);
        LANGUAGES.put(2, LANGUAGE_CS);
        LANGUAGES.put(3, LANGUAGE_DE);
        LANGUAGES.put(4, LANGUAGE_FR);
        LANGUAGES.put(5, LANGUAGE_ES);
        LANGUAGES.put(9, LANGUAGE_PT);
    }

    public final static String PLANT = "Plant";

    public final static String COLOR_OF_FLOWERS = "filterColor";
    public final static String HABITAT = "filterHabitat";
    public final static String NUMBER_OF_PETALS = "filterPetal";

    public final static int PLANT_FILTER_COLOR = 294;
    public final static int PLANT_FILTER_HABITAT = 297;
    public final static int PLANT_FILTER_PETALS = 301;
    public final static int PLANT_FILTER_INFLORESCENCE = 225;
    public final static int PLANT_FILTER_SEPAL = 239;
    public final static int PLANT_FILTER_STEM = 240;
    public final static int PLANT_FILTER_LEAF_SHAPE = 241;
    public final static int PLANT_FILTER_LEAF_MARGIN = 242;
    public final static int PLANT_FILTER_LEAF_VENETATION = 243;
    public final static int PLANT_FILTER_LEAF_ARRANGEMENT = 244;
    public final static int PLANT_FILTER_ROOT = 245;

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
    public final static int PLANT_TRIVIA = 292;
    public final static int PLANT_TOXICITY = 407;
    public final static int PLANT_HERBALISM = 408;

    public final static int NAMES_TO_DISPLAY = 5;

    public final static String TAXONOMY_FAMILY = "Familia";
}
