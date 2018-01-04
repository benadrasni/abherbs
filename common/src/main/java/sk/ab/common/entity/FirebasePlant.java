package sk.ab.common.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;

@Entity
public class FirebasePlant {

    @Id private Integer plantId;
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

    private ArrayList<String> filterColor;
    private ArrayList<String> filterHabitat;
    private ArrayList<String> filterPetal;
    private ArrayList<String> filterInflorescence;
    private ArrayList<String> filterSepal;
    private ArrayList<String> filterStem;
    private ArrayList<String> filterLeafShape;
    private ArrayList<String> filterLeafMargin;
    private ArrayList<String> filterLeafVenetation;
    private ArrayList<String> filterLeafArrangement;
    private ArrayList<String> filterRoot;

    private HashMap<String, String> wikilinks = new HashMap<>();
    private HashMap<String, String> taxonomy = new HashMap<>();
    private HashMap<String, String> APGIV = new HashMap<>();

    public FirebasePlant() {

    }

    public FirebasePlant(Plant plant) {
        this.plantId = plant.getPlantId();
        this.name = plant.getName();
        this.wikiName = plant.getWikiName();
        this.illustrationUrl = plant.getIllustrationUrl();
        this.heightFrom = plant.getHeightFrom();
        this.heightTo = plant.getHeightTo();
        this.floweringFrom = plant.getFloweringFrom();
        this.floweringTo = plant.getFloweringTo();
        this.toxicityClass = plant.getToxicityClass();
        this.synonyms = plant.getSynonyms();
        this.photoUrls = plant.getPhotoUrls();
        this.sourceUrls = plant.getSourceUrls().get("");
        this.filterColor = plant.getFilterColor();
        this.filterHabitat = plant.getFilterHabitat();
        this.filterPetal = plant.getFilterPetal();
        this.filterInflorescence = plant.getFilterInflorescence();
        this.filterSepal = plant.getFilterSepal();
        this.filterStem = plant.getFilterStem();
        this.filterLeafShape = plant.getFilterLeafShape();
        this.filterLeafMargin = plant.getFilterLeafMargin();
        this.filterLeafVenetation = plant.getFilterLeafVenetation();
        this.filterLeafArrangement = plant.getFilterLeafArrangement();
        this.filterRoot = plant.getFilterRoot();
        this.taxonomy = plant.getTaxonomy();
        this.wikilinks = new HashMap<>();
        if (plant.getWikilinks().get("commons") != null) {
            this.wikilinks.put("commons", plant.getWikilinks().get("commons"));
        }
        if (plant.getWikilinks().get("species") != null) {
            this.wikilinks.put("species", plant.getWikilinks().get("species"));
        }

    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
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

    public ArrayList<String> getFilterColor() {
        return filterColor;
    }

    public void setFilterColor(ArrayList<String> filterColor) {
        this.filterColor = filterColor;
    }

    public ArrayList<String> getFilterHabitat() {
        return filterHabitat;
    }

    public void setFilterHabitat(ArrayList<String> filterHabitat) {
        this.filterHabitat = filterHabitat;
    }

    public ArrayList<String> getFilterPetal() {
        return filterPetal;
    }

    public void setFilterPetal(ArrayList<String> filterPetal) {
        this.filterPetal = filterPetal;
    }

    public ArrayList<String> getFilterInflorescence() {
        return filterInflorescence;
    }

    public void setFilterInflorescence(ArrayList<String> filterInflorescence) {
        this.filterInflorescence = filterInflorescence;
    }

    public ArrayList<String> getFilterSepal() {
        return filterSepal;
    }

    public void setFilterSepal(ArrayList<String> filterSepal) {
        this.filterSepal = filterSepal;
    }

    public ArrayList<String> getFilterStem() {
        return filterStem;
    }

    public void setFilterStem(ArrayList<String> filterStem) {
        this.filterStem = filterStem;
    }

    public ArrayList<String> getFilterLeafShape() {
        return filterLeafShape;
    }

    public void setFilterLeafShape(ArrayList<String> filterLeafShape) {
        this.filterLeafShape = filterLeafShape;
    }

    public ArrayList<String> getFilterLeafMargin() {
        return filterLeafMargin;
    }

    public void setFilterLeafMargin(ArrayList<String> filterLeafMargin) {
        this.filterLeafMargin = filterLeafMargin;
    }

    public ArrayList<String> getFilterLeafVenetation() {
        return filterLeafVenetation;
    }

    public void setFilterLeafVenetation(ArrayList<String> filterLeafVenetation) {
        this.filterLeafVenetation = filterLeafVenetation;
    }

    public ArrayList<String> getFilterLeafArrangement() {
        return filterLeafArrangement;
    }

    public void setFilterLeafArrangement(ArrayList<String> filterLeafArrangement) {
        this.filterLeafArrangement = filterLeafArrangement;
    }

    public ArrayList<String> getFilterRoot() {
        return filterRoot;
    }

    public void setFilterRoot(ArrayList<String> filterRoot) {
        this.filterRoot = filterRoot;
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