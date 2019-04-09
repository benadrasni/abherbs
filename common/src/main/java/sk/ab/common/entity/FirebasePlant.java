package sk.ab.common.entity;

import java.util.ArrayList;
import java.util.HashMap;


public class FirebasePlant {

    private Integer id;
    private Integer gbifId;
    private Integer kewId;
    private String usdaId;
    private String freebaseId;
    private String name;
    private String wikiName;
    private String illustrationUrl;
    private Integer heightFrom;
    private Integer heightTo;
    private Integer floweringFrom;
    private Integer floweringTo;
    private Integer toxicityClass;
    private ArrayList<String> synonyms = new ArrayList<>();
    private ArrayList<String> photoUrls = new ArrayList<>();
    private ArrayList<String> sourceUrls = new ArrayList<>();

    private HashMap<String, String> wikilinks = new HashMap<>();
    private HashMap<String, String> taxonomy = new HashMap<>();
    private HashMap<String, String> APGIV = new HashMap<>();

    public FirebasePlant() {

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGbifId() {
        return gbifId;
    }

    public void setGbifId(Integer gbifId) {
        this.gbifId = gbifId;
    }

    public Integer getKewId() {
        return kewId;
    }

    public void setKewId(Integer kewId) {
        this.kewId = kewId;
    }

    public String getUsdaId() {
        return usdaId;
    }

    public void setUsdaId(String usdaId) {
        this.usdaId = usdaId;
    }

    public String getFreebaseId() {
        return freebaseId;
    }

    public void setFreebaseId(String freebaseId) {
        this.freebaseId = freebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        this.wikiName = wikiName;
    }

    public String getIllustrationUrl() {
        return illustrationUrl;
    }

    public void setIllustrationUrl(String illustrationUrl) {
        this.illustrationUrl = illustrationUrl;
    }

    public Integer getHeightFrom() {
        return heightFrom;
    }

    public void setHeightFrom(Integer heightFrom) {
        this.heightFrom = heightFrom;
    }

    public Integer getHeightTo() {
        return heightTo;
    }

    public void setHeightTo(Integer heightTo) {
        this.heightTo = heightTo;
    }

    public Integer getFloweringFrom() {
        return floweringFrom;
    }

    public void setFloweringFrom(Integer floweringFrom) {
        this.floweringFrom = floweringFrom;
    }

    public Integer getFloweringTo() {
        return floweringTo;
    }

    public void setFloweringTo(Integer floweringTo) {
        this.floweringTo = floweringTo;
    }

    public Integer getToxicityClass() {
        return toxicityClass;
    }

    public void setToxicityClass(Integer toxicityClass) {
        this.toxicityClass = toxicityClass;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(ArrayList<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public ArrayList<String> getSourceUrls() {
        return sourceUrls;
    }

    public void setSourceUrls(ArrayList<String> sourceUrls) {
        this.sourceUrls = sourceUrls;
    }

    public HashMap<String, String> getWikilinks() {
        return wikilinks;
    }

    public void setWikilinks(HashMap<String, String> wikilinks) {
        this.wikilinks = wikilinks;
    }

    public HashMap<String, String> getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(HashMap<String, String> taxonomy) {
        this.taxonomy = taxonomy;
    }

    public HashMap<String, String> getAPGIV() {
        return APGIV;
    }

    public void setAPGIV(HashMap<String, String> APGIV) {
        this.APGIV = APGIV;
    }
}