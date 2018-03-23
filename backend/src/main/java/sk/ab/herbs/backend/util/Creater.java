package sk.ab.herbs.backend.util;

import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonElement;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import com.google.appengine.repackaged.com.google.gson.JsonParser;
import com.google.appengine.repackaged.com.google.io.protocol.HtmlFormGenerator;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Response;
import sk.ab.common.Constants;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantTranslation;
import sk.ab.common.service.FirebaseClient;
import sk.ab.common.util.Utils;

/**
 *
 * Created by adrian on 2. 6. 2017.
 */

public class Creater {

    private static String PATH_TO_PLANTS_TO_ADD = "C:/Dev/Projects/abherbs/backend/txt/plants_to_add.txt";
    private static String PATH_TO_PLANTS = "C:/Dev/Projects/abherbs/backend/txt/plants.csv";

    private static final Map<String, String> apgivOrdoNameMap;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Amborellales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Austrobaileyales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Nymphaeales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Canellales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Laurales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Magnoliales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Piperales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Acorales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Alismatales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Arecales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Asparagales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Commelinales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Dioscoreales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Liliales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Pandanales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Petrosaviales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Poales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Zingiberales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Buxales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Ceratophyllales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Dilleniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Gunnerales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Proteales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Ranunculales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Trochodendrales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Berberidopsidales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Caryophyllales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Santalales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Cornales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Ericales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Apiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Aquifoliales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Asterales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Bruniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Dipsacales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Escalloniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Paracryphiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Boraginales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Garryales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Gentianales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Icacinales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Lamiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Metteniusales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Solanales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Vahliales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Saxifragales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Celastrales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Malpighiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Oxalidales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Vitales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Cucurbitales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Fabales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Fagales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Rosales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Zygophyllales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Brassicales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Crossosomatales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Geraniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Huerteales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Malvales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Myrtales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Picramniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Sapindales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");

        apgivOrdoNameMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> apgiiiOrdoNameMap;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Ceratophyllales", "Superregnum/Regnum/Cladus/Ordo");
        aMap.put("Nymphaeales", "Superregnum/Regnum/Cladus/Ordo");
        aMap.put("Magnoliales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Piperales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Acorales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Alismatales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Asparagales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Liliales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Commelinales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Poales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Proteales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Ranunculales", "Superregnum/Regnum/Cladus/Cladus/Ordo");
        aMap.put("Caryophyllales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Santalales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Saxifragales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Cornales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Ericales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Apiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Aquifoliales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Asterales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Dipsacales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Boraginales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Gentianales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Lamiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Solanales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Celastrales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Malpighiales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Oxalidales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Vitales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Cucurbitales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Fabales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Rosales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Brassicales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Geraniales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Malvales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Myrtales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");
        aMap.put("Sapindales", "Superregnum/Regnum/Cladus/Cladus/Cladus/Cladus/Cladus/Ordo");

        apgiiiOrdoNameMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> apgivOrdoMap;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Amborellales", "Eukaryota/Plantae/Angiosperms/Basal angiosperms/Amborellales");
        aMap.put("Austrobaileyales", "Eukaryota/Plantae/Angiosperms/Basal angiosperms/Austrobaileyales");
        aMap.put("Nymphaeales", "Eukaryota/Plantae/Angiosperms/Basal angiosperms/Nymphaeales");
        aMap.put("Canellales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Magnoliids/Canellales");
        aMap.put("Laurales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Magnoliids/Laurales");
        aMap.put("Magnoliales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Magnoliids/Magnoliales");
        aMap.put("Piperales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Magnoliids/Piperales");
        aMap.put("Acorales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Acorales");
        aMap.put("Alismatales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Alismatales");
        aMap.put("Arecales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Arecales");
        aMap.put("Asparagales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Asparagales");
        aMap.put("Commelinales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Commelinales");
        aMap.put("Dioscoreales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Dioscoreales");
        aMap.put("Liliales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Liliales");
        aMap.put("Pandanales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Pandanales");
        aMap.put("Petrosaviales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Petrosaviales");
        aMap.put("Poales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Poales");
        aMap.put("Zingiberales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Monocots/Zingiberales");
        aMap.put("Buxales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Buxales");
        aMap.put("Ceratophyllales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Ceratophyllales");
        aMap.put("Dilleniales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Dilleniales");
        aMap.put("Gunnerales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Gunnerales");
        aMap.put("Proteales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Proteales");
        aMap.put("Ranunculales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Ranunculales");
        aMap.put("Trochodendrales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Trochodendrales");
        aMap.put("Berberidopsidales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Berberidopsidales");
        aMap.put("Caryophyllales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Caryophyllales");
        aMap.put("Santalales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Santalales");
        aMap.put("Cornales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Cornales");
        aMap.put("Ericales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Ericales");
        aMap.put("Apiales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Apiales");
        aMap.put("Aquifoliales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Aquifoliales");
        aMap.put("Asterales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Asterales");
        aMap.put("Bruniales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Bruniales");
        aMap.put("Dipsacales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Dipsacales");
        aMap.put("Escalloniales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Escalloniales");
        aMap.put("Paracryphiales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Campanulids/Paracryphiales");
        aMap.put("Boraginales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Boraginales");
        aMap.put("Garryales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Garryales");
        aMap.put("Gentianales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Gentianales");
        aMap.put("Icacinales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Icacinales");
        aMap.put("Lamiales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Lamiales");
        aMap.put("Metteniusales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Metteniusales");
        aMap.put("Solanales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Solanales");
        aMap.put("Vahliales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superasterids/Asterids/Lamiids/Vahliales");
        aMap.put("Saxifragales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Saxifragales");
        aMap.put("Celastrales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Celastrales");
        aMap.put("Malpighiales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malpighiales");
        aMap.put("Oxalidales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Oxalidales");
        aMap.put("Vitales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Vitales");
        aMap.put("Cucurbitales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Fabids/Cucurbitales");
        aMap.put("Fabales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Fabids/Fabales");
        aMap.put("Fagales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Fabids/Fagales");
        aMap.put("Rosales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Fabids/Rosales");
        aMap.put("Zygophyllales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Fabids/Zygophyllales");
        aMap.put("Brassicales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Brassicales");
        aMap.put("Crossosomatales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Crossosomatales");
        aMap.put("Geraniales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Geraniales");
        aMap.put("Huerteales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Huerteales");
        aMap.put("Malvales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Malvales");
        aMap.put("Myrtales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Myrtales");
        aMap.put("Picramniales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Picramniales");
        aMap.put("Sapindales", "Eukaryota/Plantae/Angiosperms/Mesangiosperms/Eudicots/Superrosids/Rosids/Malvids/Sapindales");

        apgivOrdoMap = Collections.unmodifiableMap(aMap);
    }

    private static final Map<String, String> apgiiiOrdoMap;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Ceratophyllales", "Eukaryota/Plantae/Angiosperms/Ceratophyllales");
        aMap.put("Nymphaeales", "Eukaryota/Plantae/Angiosperms/Nymphaeales");
        aMap.put("Magnoliales", "Eukaryota/Plantae/Angiosperms/Magnoliids/Magnoliales");
        aMap.put("Piperales", "Eukaryota/Plantae/Angiosperms/Magnoliids/Piperales");
        aMap.put("Acorales", "Eukaryota/Plantae/Angiosperms/Monocots/Acorales");
        aMap.put("Alismatales", "Eukaryota/Plantae/Angiosperms/Monocots/Alismatales");
        aMap.put("Asparagales", "Eukaryota/Plantae/Angiosperms/Monocots/Asparagales");
        aMap.put("Liliales", "Eukaryota/Plantae/Angiosperms/Monocots/Liliales");
        aMap.put("Commelinales", "Eukaryota/Plantae/Angiosperms/Monocots/Commelinids/Commelinales");
        aMap.put("Poales", "Eukaryota/Plantae/Angiosperms/Monocots/Commelinids/Poales");
        aMap.put("Proteales", "Eukaryota/Plantae/Angiosperms/Eudicots/Proteales");
        aMap.put("Ranunculales", "Eukaryota/Plantae/Angiosperms/Eudicots/Ranunculales");
        aMap.put("Caryophyllales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Caryophyllales");
        aMap.put("Santalales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Santalales");
        aMap.put("Saxifragales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Saxifragales");
        aMap.put("Cornales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Cornales");
        aMap.put("Ericales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Ericales");
        aMap.put("Apiales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_II/Apiales");
        aMap.put("Aquifoliales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_II/Aquifoliales");
        aMap.put("Asterales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_II/Asterales");
        aMap.put("Dipsacales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_II/Dipsacales");
        aMap.put("Boraginales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_I/Boraginales");
        aMap.put("Gentianales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_I/Gentianales");
        aMap.put("Lamiales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_I/Lamiales");
        aMap.put("Solanales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Asterids/Euasterids_I/Solanales");
        aMap.put("Celastrales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Celastrales");
        aMap.put("Malpighiales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Malpighiales");
        aMap.put("Oxalidales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Oxalidales");
        aMap.put("Vitales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Vitales");
        aMap.put("Cucurbitales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Cucurbitales");
        aMap.put("Fabales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Fabales");
        aMap.put("Rosales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_I/Rosales");
        aMap.put("Brassicales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_II/Brassicales");
        aMap.put("Geraniales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_II/Geraniales");
        aMap.put("Malvales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_II/Malvales");
        aMap.put("Myrtales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_II/Myrtales");
        aMap.put("Sapindales", "Eukaryota/Plantae/Angiosperms/Eudicots/Core_eudicots/Rosids/Eurosids_II/Sapindales");

        apgiiiOrdoMap = Collections.unmodifiableMap(aMap);
    }

    public static void main(String[] params) {

        try {
            final FirebaseClient firebaseClient = new FirebaseClient();
//            Call<Map<String, Object>> callFirebaseAPGIII = firebaseClient.getApiService().getAPGIIINew();
//            Map<String, Object> apgIII = callFirebaseAPGIII.execute().body();
//
//            Call<Map<String, Object>> callFirebaseAPGIV = firebaseClient.getApiService().getAPGIV();
//            Map<String, Object> apgIV = callFirebaseAPGIV.execute().body();
//
//            File fileOld = new File(PATH_TO_PLANTS);
//
//            Scanner scannerOld = new Scanner(fileOld);
//
//            while (scannerOld.hasNextLine()) {
//                final String[] plantLine = scannerOld.nextLine().split(";");
//                System.out.println(plantLine[0]);
//
//                //updateTaxonomy(apgivOrdoMap, apgIV, plantLine[0], plantLine[1]);
//                updatePlantTaxonomy(firebaseClient, plantLine[0], plantLine[1]);
//            }
//            scannerOld.close();

            File file = new File(PATH_TO_PLANTS_TO_ADD);

            Scanner scan = new Scanner(file);
            Integer plantId = 877;
            while(scan.hasNextLine()){
                final String plantName = scan.nextLine();
                final String wikiSpeciesName = plantName;
                System.out.println(plantName);

                addOrUpdateBasic(firebaseClient, plantId, plantName, wikiSpeciesName);

                plantId++;

//                updateTaxonomy(apgiiiOrdoMap, apgIII, plantName, wikiSpeciesName, true);
//                updateTaxonomy(apgivOrdoMap, apgIV, plantName, wikiSpeciesName, false);

//                updatePlantTaxonomy(firebaseClient, plantName, wikiSpeciesName);
                addOrUpdateTranslations(firebaseClient, plantName, wikiSpeciesName);
            }
            scan.close();

//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(new File("c:/Dev/APGIV.json"), apgIV);
//
//            Call<Object> saveAPGIV = firebaseClient.getApiService().saveAPGIV(apgIV);
//            Response<Object> responseAPGIV = saveAPGIV.execute();
//
//            Call<Object> saveAPGIII = firebaseClient.getApiService().saveAPGIIINew(apgIII);
//            Response<Object> responseAPGIII = saveAPGIII.execute();

//            System.out.println(response.body());
        } catch (IOException ex) {

        }
    }

    private static void addOrUpdateTranslations(FirebaseClient firebaseClient, String plantName, String wikiSpeciesName) {
        try {
            Call<FirebasePlant> getPlantCall = firebaseClient.getApiService().getPlant(plantName);
            FirebasePlant plantBasic = getPlantCall.execute().body();
            String familia = getTaxon(plantBasic, "Familia");

            Map<String, List<String>> names = readNames(wikiSpeciesName, "/wiki/" + wikiSpeciesName);
            Map<String, String> sitelinks = readWikilinks("/wiki/" + wikiSpeciesName);

            for (String language : names.keySet()) {
                if (Constants.LANGUAGE_LA.equals(language)) {
                    continue;
                }
                PlantTranslation plantTranslation = new PlantTranslation();

                ArrayList<String> namesInLanguage = (ArrayList)names.get(language);
                if (namesInLanguage.size() > 0) {
                    plantTranslation.setLabel(namesInLanguage.get(0));

                    namesInLanguage.remove(0);
                    if (namesInLanguage.size() > 0) {
                        plantTranslation.setNames(namesInLanguage);
                    }

                    if (Constants.LANGUAGE_EN.equals(language)) {
                        plantTranslation.setDescription("...");
                        plantTranslation.setFlower("...");
                        plantTranslation.setInflorescence("...");
                        plantTranslation.setFruit("...");
                        plantTranslation.setLeaf("...");
                        plantTranslation.setStem("...");
                        plantTranslation.setHabitat("...");
                    }

                    if (Constants.LANGUAGE_SK.equals(language)) {
                        plantTranslation.setDescription("...");
                        plantTranslation.setFlower("...");
                        plantTranslation.setInflorescence("...");
                        plantTranslation.setFruit("...");
                        plantTranslation.setLeaf("...");
                        plantTranslation.setStem("...");
                        plantTranslation.setHabitat("...");
                    }
                }

                if (sitelinks.get(language) != null) {
                    plantTranslation.setWikipedia(sitelinks.get(language));
                }

                Call<Object> saveTranslationCall = firebaseClient.getApiService().saveTranslation(language, plantName, plantTranslation);
                saveTranslationCall.execute();
            }
        } catch (IOException ex) {

        }
    }

    private static void updatePlantTaxonomy(FirebaseClient firebaseClient, String plantName, String wikiSpeciesName) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + wikiSpeciesName).get();
            Elements ps = doc.getElementsByTag("p");

            List<TextNode> taxonNames = new ArrayList<>();
            List<String> taxonValues = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                Element p = ps.get(i);
                taxonNames.addAll(p.textNodes());

                Elements aNodes = p.getElementsByTag("a");

                for (Element el : aNodes) {
                    if (!el.text().isEmpty()) {
                        if (plantName.equals(el.text())) {
                            break;
                        }
                        taxonValues.add(el.text());
                    }
                }
            }

            int j = 0;
            boolean afterFamilia = false;
            while (j < taxonValues.size()) {
                String tName = taxonNames.get(j).text().trim();
                if (tName.startsWith("Familia")) {
                    afterFamilia = true;
                }

                String value = taxonNames.get(j).text().trim();
                if (value.endsWith("Unassigned") || value.startsWith("Classification") || value.contains(".")) {
                    taxonNames.remove(j);
                } else if (afterFamilia && tName.startsWith("Cladus")) {
                    taxonNames.remove(j);
                    taxonValues.remove(j);
                } else {
                    j++;
                }
            }

            String pathNameAPGIII = "";
            String pathAPGIII = "";
            String pathNameAPGIV = "";
            String pathAPGIV = "";
            boolean isGenus = false;
            for (int i = 0; i < taxonValues.size(); i++) {
                String taxonName = taxonNames.get(i).text().trim();
                if (taxonName.endsWith(":")) {
                    taxonName = taxonName.substring(0,taxonName.length()-1);
                }
                String taxonValue = taxonValues.get(i).trim();
                if (taxonValue.contains(" ")) {
                    taxonValue = taxonValue.substring(taxonValue.lastIndexOf(" ") + 1);
                } else if (taxonValue.contains("\u00A0")) {
                    taxonValue = taxonValue.substring(taxonValue.lastIndexOf("\u00A0") + 1);
                }

                if (!pathNameAPGIII.isEmpty()) {
                    if (!isGenus) {
                        pathNameAPGIII = pathNameAPGIII + "/" + taxonName;
                        pathAPGIII = pathAPGIII + "/" + taxonValue;
                        if ("Genus".equals(taxonName)) {
                            isGenus = true;
                        }
                    }
                    pathNameAPGIV = pathNameAPGIV + "/" + taxonName;
                    pathAPGIV = pathAPGIV + "/" + taxonValue;
                }

                if ("Ordo".equals(taxonName)) {
                    pathNameAPGIII = apgiiiOrdoNameMap.get(taxonValue);
                    pathAPGIII = apgiiiOrdoMap.get(taxonValue);
                    pathNameAPGIV = apgivOrdoNameMap.get(taxonValue);
                    pathAPGIV = apgivOrdoMap.get(taxonValue);
                }
            }

            String[] namesIII = pathNameAPGIII.split("/");
            String[] valuesIII = pathAPGIII.split("/");
            HashMap<String, String> apgIII = new HashMap<>();
            for (int k = valuesIII.length - 1; k >= 0; k--) {
                apgIII.put((k < 10 ? "0" : "") + k + "_" + namesIII[valuesIII.length-1-k], valuesIII[valuesIII.length-1-k]);
            }
            Call<HashMap<String, String>> savePlantTaxonomy = firebaseClient.getApiService().savePlantTaxonomy(plantName, apgIII);
            Response<HashMap<String, String>> responseAPGIII = savePlantTaxonomy.execute();


//            String[] namesIV = pathNameAPGIV.split("/");
//            String[] valuesIV = pathAPGIV.split("/");
//            HashMap<String, String> apgIV = new HashMap<>();
//            for (int k = valuesIV.length - 1; k >= 0; k--) {
//                apgIV.put((k < 10 ? "0" : "") + k + "_" + namesIV[valuesIV.length-1-k], valuesIV[valuesIV.length-1-k]);
//            }
//
//            Call<HashMap<String, String>> savePlantAPGIV = firebaseClient.getApiService().savePlantAPGIV(plantName, apgIV);
//            Response<HashMap<String, String>> responseAPGIV = savePlantAPGIV.execute();
        } catch (IOException e) {

        }

    }

    private static void updateTaxonomy(Map<String, String> apgBase, Map<String, Object> apg, String plantName, String wikiSpeciesName, boolean isAPGIII) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + wikiSpeciesName).get();
            Elements ps = doc.getElementsByTag("p");

            List<TextNode> taxonNames = new ArrayList<>();
            List<String> taxonValues = new ArrayList<>();
            List<String> taxonLinks = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                Element p = ps.get(i);
                taxonNames.addAll(p.textNodes());

                Elements aNodes = p.getElementsByTag("a");

                for (Element el : aNodes) {
                    if (!el.text().isEmpty()) {
                        if (plantName.equals(el.text())) {
                            break;
                        }
                        taxonValues.add(el.text());
                        taxonLinks.add(el.attr("href"));
                    }
                }
            }

            int j = 0;
            boolean afterFamilia = false;
            while (j < taxonValues.size()) {
                String tName = taxonNames.get(j).text().trim();
                if (tName.startsWith("Familia")) {
                    afterFamilia = true;
                }

                String value = taxonNames.get(j).text().trim();
                if (value.endsWith("Unassigned") || value.startsWith("Classification") || value.contains(".")) {
                    taxonNames.remove(j);
                } else if (afterFamilia && tName.startsWith("Cladus")) {
                    taxonNames.remove(j);
                    taxonValues.remove(j);
                    taxonLinks.remove(j);
                } else {
                    j++;
                }
            }

            String path = "";
            boolean isGenus = false;
            for (int i = 0; i < taxonValues.size(); i++) {
                if (isGenus && isAPGIII) {
                    break;
                }
                String taxonName = taxonNames.get(i).text().trim();
                if (taxonName.endsWith(":")) {
                    taxonName = taxonName.substring(0,taxonName.length()-1);
                }
                String taxonValue = taxonValues.get(i).trim();
                String taxonLink = taxonLinks.get(i).trim();

                if (!path.isEmpty()) {
                    String[] parsedPath = path.split("/");
                    Map<String, Object> taxon = apg;
                    for(String part : parsedPath) {
                        taxon = (Map<String, Object>)taxon.get(part);
                    }

                    if (taxonValue.contains(" ")) {
                        taxonValue = taxonValue.substring(taxonValue.lastIndexOf(" ") + 1);
                    } else if (taxonValue.contains("\u00A0")) {
                        taxonValue = taxonValue.substring(taxonValue.lastIndexOf("\u00A0") + 1);
                    }
                    Map<String, Object> taxonBellow = (Map<String, Object>)taxon.get(taxonValue);
                    if (taxonBellow == null) {
                        System.out.println(taxonName + ": " + taxonValue);
                        taxonBellow = createTaxon(taxonName, taxonValue, taxonLink);
                        if (i == taxonValues.size()-1 || (isAPGIII && "Genus".equals(taxonName))) {
                            Map<String, Boolean> list = new HashMap<>();
                            list.put(plantName, true);
                            taxonBellow.put("list", list);
                            taxonBellow.put("count", list.size());
                        }
                        taxon.put(taxonValue, taxonBellow);
                    } else {
                        if (i == taxonValues.size()-1 || (isAPGIII && "Genus".equals(taxonName))) {
                            Map<String, Boolean> list = (Map<String, Boolean>)taxonBellow.get("list");
                            list.put(plantName, true);
                            taxonBellow.put("count", list.size());
                        }
                    }

                    Map<String, Boolean> list = (Map<String, Boolean>)taxon.get("list");
                    if (list == null) {
                        list = new HashMap<>();
                    }
                    list.put(plantName, true);
                    taxon.put("list", list);
                    taxon.put("count", list.size());

                    path = path + "/" + taxonValue;
                }

                if ("Ordo".equals(taxonName)) {
                    path = apgBase.get(taxonValue);
                }

                if ("Genus".equals(taxonName)) {
                    isGenus = true;
                }
            }

        } catch (IOException e) {

        }
    }

    private static void addOrUpdateBasic(FirebaseClient firebaseClient, Integer plantId, String plantName, String wikiSpeciesName) throws IOException {
        FirebasePlant plantBasic = new FirebasePlant();
        plantBasic.setName(plantName);
        plantBasic.setWikiName(plantName);
        plantBasic.setPlantId(plantId);
        plantBasic.setFilterColor(new ArrayList<String>(Arrays.asList("?")));
        plantBasic.setFilterHabitat(new ArrayList<String>(Arrays.asList("?")));
        plantBasic.setFilterPetal(new ArrayList<String>(Arrays.asList("?")));
        plantBasic.setFloweringFrom(1);
        plantBasic.setFloweringTo(1);
        plantBasic.setHeightFrom(0);
        plantBasic.setHeightTo(0);

        HashMap<String, String> wikilinks = new HashMap<>();
        wikilinks.put("commons", "https://commons.wikimedia.org/wiki/" + plantName.replace(" ", "_"));
        wikilinks.put("species", "https://species.wikimedia.org/wiki/" + plantName.replace(" ", "_"));
        plantBasic.setWikilinks(wikilinks);

        String ordo = "";
        String familia = "";

        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/wiki/" + wikiSpeciesName).get();
            Elements ps = doc.getElementsByTag("p");

            List<TextNode> taxonNames = new ArrayList<>();
            List<String> taxonValues = new ArrayList<>();
            List<String> taxonLinks = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                Element p = ps.get(i);
                for (TextNode textNode : p.textNodes()) {
                    if (!textNode.getWholeText().trim().isEmpty()) {
                        taxonNames.add(textNode);
                    }
                }

                Elements aNodes = p.getElementsByTag("a");

                for (Element el : aNodes) {
                    if (!el.text().isEmpty()) {
                        if (plantName.equals(el.text())) {
                            break;
                        }
                        taxonValues.add(el.text());
                        taxonLinks.add(el.attr("href"));
                    }
                }
            }

            int j = 0;
            boolean afterFamilia = false;
            while (j < taxonValues.size()) {
                String tName = taxonNames.get(j).text().trim();
                if (tName.startsWith("Familia")) {
                    afterFamilia = true;
                }

                String value = taxonNames.get(j).text().trim();
                if (value.endsWith("Unassigned") || value.startsWith("Classification") || value.contains(".")) {
                    taxonNames.remove(j);
                } else if (afterFamilia && tName.startsWith("Cladus")) {
                    taxonNames.remove(j);
                    taxonValues.remove(j);
                    taxonLinks.remove(j);
                } else {
                    j++;
                }
            }

            String pathNameAPGIII = "";
            String pathAPGIII = "";
            String pathNameAPGIV = "";
            String pathAPGIV = "";
            for (int i = 0; i < taxonValues.size(); i++) {
                String taxonName = taxonNames.get(i).text().trim();
                if (taxonName.endsWith(":")) {
                    taxonName = taxonName.substring(0,taxonName.length()-1);
                }
                String taxonValue = taxonValues.get(i).trim();
                if (taxonValue.contains(" ")) {
                    taxonValue = taxonValue.substring(taxonValue.lastIndexOf(" ") + 1);
                } else if (taxonValue.contains("\u00A0")) {
                    taxonValue = taxonValue.substring(taxonValue.lastIndexOf("\u00A0") + 1);
                }

                if (!pathNameAPGIII.isEmpty()) {
                    pathNameAPGIII = pathNameAPGIII + "/" + taxonName;
                    pathAPGIII = pathAPGIII + "/" + taxonValue;
                    pathNameAPGIV = pathNameAPGIV + "/" + taxonName;
                    pathAPGIV = pathAPGIV + "/" + taxonValue;
                }

                if ("Ordo".equals(taxonName)) {
                    pathNameAPGIII = apgiiiOrdoNameMap.get(taxonValue);
                    pathAPGIII = apgiiiOrdoMap.get(taxonValue);
                    pathNameAPGIV = apgivOrdoNameMap.get(taxonValue);
                    pathAPGIV = apgivOrdoMap.get(taxonValue);
                    ordo = taxonValue;
                } else if ("Familia".equals(taxonName)) {
                    familia = taxonValue;
                }
            }

            String[] names = pathNameAPGIII.split("/");
            String[] values = pathAPGIII.split("/");
            HashMap<String, String> apgIII = new HashMap<>();
            for (int k = values.length - 1; k >= 0; k--) {
                apgIII.put((k < 10 ? "0" : "") + k + "_" + names[values.length-1-k], values[values.length-1-k]);
            }
            plantBasic.setTaxonomy(apgIII);

            names = pathNameAPGIV.split("/");
            values = pathAPGIV.split("/");
            HashMap<String, String> apgIV = new HashMap<>();
            for (int k = values.length - 1; k >= 0; k--) {
                apgIV.put((k < 10 ? "0" : "") + k + "_" + names[values.length-1-k], values[values.length-1-k]);
            }
            plantBasic.setAPGIV(apgIV);

        } catch (IOException e) {

        }

        plantBasic.setIllustrationUrl(ordo + "/" + familia + "/" + plantName.replace(" ", "_") + "/" + plantName.replace(" ", "_") + ".webp");

        ArrayList<String> urls = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            File file = new File("C:/Dev/Storage/storage/photos/" + ordo + "/" + familia + "/" + plantName.replace(" ", "_") + "/" + plantName.substring(0,1).toLowerCase() + plantName.substring(plantName.indexOf(" ") + 1, plantName.indexOf(" ") + 2) + i + ".webp");
            if (file.exists()) {
                urls.add(ordo + "/" + familia + "/" + plantName.replace(" ", "_") + "/" + plantName.substring(0,1).toLowerCase() + plantName.substring(plantName.indexOf(" ") + 1, plantName.indexOf(" ") + 2) + i + ".webp");
            } else {
                break;
            }
        }
        plantBasic.setPhotoUrls(urls);

        ArrayList<String> synonyms = getSynonyms(wikiSpeciesName);
        if (!synonyms.isEmpty()) {
            plantBasic.setSynonyms(synonyms);
        }

        ArrayList<String> sources = new ArrayList<>();
        File file = new File("C:/Dev/Plants/" + familia + "/" + plantName+ "/sources.txt");
        if (file.exists()) {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                sources.add(scan.nextLine());
            }
            plantBasic.setSourceUrls(sources);
        }

        Call<FirebasePlant> savePlant = firebaseClient.getApiService().savePlant(plantName, plantBasic);
        Response<FirebasePlant> response = savePlant.execute();
    }

    private static ArrayList<String> getSynonyms(String wikiSpeciesName) {
        ArrayList<String> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + wikiSpeciesName + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();

            String[] lines = wikiPage.split("\n");

            Set<String> synonymSet = new TreeSet<>();
            String key = "synonym";
            boolean isSynonyms = false;
            for(String line : lines) {
                if (line.contains("Synonym") || line.contains("{{SN")) {
                    isSynonyms = true;
                }

                if (!isSynonyms) {
                    continue;
                }

                if (line.trim().length() > 0) {
//                    if (line.trim().equals("{{HOT}}")) {
//                        if (synonymSet.size() > 0) {
//                            entity.setProperty(key, synonymList);
//                        }
//                        key = "Homotypic";
//                        synonymSet = new TreeSet<>();
//                        continue;
//                    } else if (line.trim().equals("{{HET}}")) {
//                        if (synonymSet.size() > 0) {
//                            entity.setProperty(key, synonymList);
//                        }
//                        key = "Heterotypic";
//                        synonymSet = new TreeSet<>();
//                        continue;
//                    } else if (line.trim().equals("{{BA}}")) {
//                        if (synonymSet.size() > 0) {
//                            entity.setProperty(key, synonymList);
//                        }
//                        key = "Basionym";
//                        synonymSet = new TreeSet<>();
//                        continue;
//                    }

                    if (line.contains("References") || line.contains("Vernacular names")
                            || (line.contains("Hybrids") && line.contains("=="))
                            || (line.contains("Notes") && line.contains("=="))) {
                        break;
                    }

                    if (line.contains("''") && line.substring(line.indexOf("''")+2).contains("''")) {
                        String synonym = line.substring(line.indexOf("''")+2, line.indexOf("''", line.indexOf("''")+2));

                        if (synonym.startsWith("[[")) {
                            synonym = synonym.substring(2);
                        }
                        if (synonym.endsWith("]]")) {
                            synonym = synonym.substring(0,synonym.length()-2);
                        }

                        synonymSet.add(synonym.replace("'", ""));
                    }
                }
            }
            synonymSet.remove(wikiSpeciesName);
            synonymSet.remove("{{BASEPAGENAME}}");
            synonymSet.remove("Homotypic");
            synonymSet.remove("Heterotypic");
            synonymSet.remove("Basionym");
            synonymSet.remove("Homonyms");
            synonymSet.remove("vide");
            List<String> synonymList = new ArrayList<>();
            for(String synonym : synonymSet) {
                result.add(synonym);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Map<String, Object> createTaxon(String taxonName, String taxonValue, String taxonLink) {
        Map<String, Object> taxon = new HashMap<>();

        Map<String, List<String>> result = readNames(taxonValue, taxonLink);

        taxon.put("type", taxonName);
        taxon.put("names", result);

        return taxon;
    }

    private static Map<String, List<String>> readNames(String taxonValue, String taxonLink) {
        Map<String, List<String>> result = new TreeMap<>();
        Map<String, List<String>> preresult = new TreeMap<>();
        List<String> latin = new ArrayList<>();
        latin.add(taxonValue);
        preresult.put("la", latin);

        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org/w/index.php?title=" + taxonLink.substring(taxonLink.lastIndexOf("/")+1) + "&action=edit").get();

            String wikiPage = doc.getElementsByTag("textarea").val();

            if (wikiPage.contains("{{VN")) {
                String vn = wikiPage.substring(wikiPage.indexOf("{{VN"), wikiPage.indexOf("}}", wikiPage.indexOf("{{VN"))).replace("\n", "");
                String[] vnItems = vn.substring(5, vn.length()).split("\\|");

                for (String vnItem : vnItems) {
                    String[] hlp = vnItem.split("=");
                    if (hlp.length > 1) {
                        String language = hlp[0];

                        String[] names = hlp[1].trim().split(", ");
                        if (names.length == 1) {
                            names = hlp[1].split(" / ");
                        }
                        List<String> speciesValuesOld = new ArrayList<>(Arrays.asList(names));
                        List<String> speciesValues = new ArrayList<>();
                        for (String speciesValue : speciesValuesOld) {
                            if (!taxonValue.toLowerCase().equals(speciesValue.toLowerCase())) {
                                speciesValues.add(speciesValue.trim());
                            }
                        }

                        if (speciesValues.size() > 0) {
                            preresult.put(language, speciesValues);
                        }

                    }
                }
            }

            String id = getWikidata(taxonLink);

            if (id != null) {

                URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json");
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(id);

                JsonObject labels = wikidata.getAsJsonObject("labels");

                for (Map.Entry<String, JsonElement> entry : labels.entrySet()) {
                    JsonObject value = entry.getValue().getAsJsonObject();

                    List<String> names = preresult.get(value.get("language").getAsString());
                    List<String> namesLower = new ArrayList<>();
                    if (names == null) {
                        names = new ArrayList<>();
                        preresult.put(value.get("language").getAsString(), names);
                    }
                    for (String onename : names) {
                        namesLower.add(onename.toLowerCase());
                    }

                    if (!namesLower.contains(value.get("value").getAsString().toLowerCase()) && !taxonValue.toLowerCase().equals(value.get("value").getAsString().toLowerCase())) {
                        names.add(value.get("value").getAsString());
                    }
                }

                JsonElement elem = wikidata.get("aliases");
                if (elem.isJsonObject()) {
                    JsonObject aliases = wikidata.getAsJsonObject("aliases");

                    for (Map.Entry<String, JsonElement> entry : aliases.entrySet()) {
                        JsonArray value = entry.getValue().getAsJsonArray();

                        List<String> names = preresult.get(entry.getKey());
                        List<String> namesLower = new ArrayList<>();
                        if (names == null) {
                            names = new ArrayList<>();
                            preresult.put(entry.getKey(), names);
                        }
                        for (String onename : names) {
                            namesLower.add(onename.toLowerCase());
                        }

                        for (JsonElement v : value) {
                            if (!namesLower.contains(v.getAsJsonObject().get("value").getAsString().toLowerCase()) && !taxonValue.toLowerCase().equals(v.getAsJsonObject().get("value").getAsString().toLowerCase())) {
                                names.add(v.getAsJsonObject().get("value").getAsString());
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String language : preresult.keySet()) {
            List values = preresult.get(language);
            if (!values.isEmpty()) {
                result.put(language, values);
            }
        }

        return result;
    }

    private static Map<String, String> readWikilinks(String taxonLink) {
            Map<String, String> result = new TreeMap<>();

        try {

            String id = getWikidata(taxonLink);

            if (id != null) {

                URL url = new URL("https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json");
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject wikidata = root.getAsJsonObject().getAsJsonObject("entities").getAsJsonObject(id);

                JsonObject sitelinks = wikidata.getAsJsonObject("sitelinks");

                for (Map.Entry<String, JsonElement> entry : sitelinks.entrySet()) {
                    JsonObject value = entry.getValue().getAsJsonObject();

                    String siteName = value.get("site").getAsString();
                    if (siteName.endsWith("wiki")) {

                        siteName = siteName.substring(0, siteName.length()-4);
                        if (!"commons".equals(siteName) && !"species".equals(siteName)) {
                            result.put(siteName, value.get("url").getAsString());
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private static String getWikidata(String name) {
        try {
            Document doc = Jsoup.connect("https://species.wikimedia.org" + name).get();

            String wikiPage = doc.getElementsByAttributeValue("title", "Edit interlanguage links").attr("href");

            if (!wikiPage.isEmpty()) {
                return wikiPage.substring(wikiPage.lastIndexOf("/") + 1, wikiPage.indexOf("#"));
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String getTaxon(FirebasePlant plantBasic, String taxonName) {
        HashMap<String, String> apgiv = plantBasic.getAPGIV();
        for (String taxon : apgiv.keySet()) {
            if (taxon.endsWith(taxonName)) {
                return apgiv.get(taxon);
            }
        }

        return "";
    }

}
