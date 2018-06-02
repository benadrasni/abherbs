package sk.ab.common.entity;

import java.util.ArrayList;
import java.util.HashMap;

import sk.ab.common.Constants;

public class Plant {

    protected Integer plantId;
    protected Integer gbifId;
    protected Integer kewId;
    protected String usdaId;
    protected String name;
    protected String wikiName;
    protected String illustrationUrl;
    protected Integer heightFrom;
    protected Integer heightTo;
    protected Integer floweringFrom;
    protected Integer floweringTo;
    protected Integer toxicityClass;
    protected ArrayList<String> synonyms = new ArrayList<>();
    protected ArrayList<String> photoUrls = new ArrayList<>();
    protected HashMap<String, String> label = new HashMap<>();
    protected HashMap<String, ArrayList<String>> names = new HashMap<>();

    protected ArrayList<String> filterColor;
    protected ArrayList<String> filterHabitat;
    protected ArrayList<String> filterPetal;
    protected ArrayList<Integer> filterDistribution;
    protected ArrayList<String> filterInflorescence;
    protected ArrayList<String> filterSepal;
    protected ArrayList<String> filterStem;
    protected ArrayList<String> filterLeafShape;
    protected ArrayList<String> filterLeafMargin;
    protected ArrayList<String> filterLeafVenetation;
    protected ArrayList<String> filterLeafArrangement;
    protected ArrayList<String> filterRoot;

    protected HashMap<String, String> description = new HashMap<>();
    protected HashMap<String, String> flower = new HashMap<>();
    protected HashMap<String, String> inflorescence = new HashMap<>();
    protected HashMap<String, String> fruit = new HashMap<>();
    protected HashMap<String, String> leaf = new HashMap<>();
    protected HashMap<String, String> stem = new HashMap<>();
    protected HashMap<String, String> habitat = new HashMap<>();
    protected HashMap<String, String> trivia = new HashMap<>();
    protected HashMap<String, String> toxicity = new HashMap<>();
    protected HashMap<String, String> herbalism = new HashMap<>();
    protected HashMap<String, String> wikilinks = new HashMap<>();
    protected HashMap<String, ArrayList<String>> sourceUrls = new HashMap<>();
    protected HashMap<String, String> taxonomy = new HashMap<>();

    public boolean isTranslated(String language) {
        return (description.get(Constants.LANGUAGE_EN) == null || description.get(language) != null)
                && (flower.get(Constants.LANGUAGE_EN) == null || flower.get(language) != null)
                && (inflorescence.get(Constants.LANGUAGE_EN) == null || inflorescence.get(language) != null)
                && (fruit.get(Constants.LANGUAGE_EN) == null || fruit.get(language) != null)
                && (leaf.get(Constants.LANGUAGE_EN) == null || leaf.get(language) != null)
                && (stem.get(Constants.LANGUAGE_EN) == null || stem.get(language) != null)
                && (habitat.get(Constants.LANGUAGE_EN) == null || habitat.get(language) != null)
                && (trivia.get(Constants.LANGUAGE_EN) == null || trivia.get(language) != null)
                && (toxicity.get(Constants.LANGUAGE_EN) == null || toxicity.get(language) != null)
                && (herbalism.get(Constants.LANGUAGE_EN) == null || herbalism.get(language) != null);
    }


    public String getLabel(String language) {
        String labelInLanguage = getLabel().get(language);
        if (labelInLanguage == null) {
            labelInLanguage = getLabel().get(Constants.LANGUAGE_LA);
        }
        return labelInLanguage;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
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

    public HashMap<String, String> getLabel() {
        return label;
    }

    public void setLabel(HashMap<String, String> label) {
        this.label = label;
    }

    public HashMap<String, ArrayList<String>> getNames() {
        return names;
    }

    public void setNames(HashMap<String, ArrayList<String>> names) {
        this.names = names;
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

    public ArrayList<Integer> getFilterDistribution() {
        return filterDistribution;
    }

    public void setFilterDistribution(ArrayList<Integer> filterDistribution) {
        this.filterDistribution = filterDistribution;
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

    public HashMap<String, String> getDescription() {
        return description;
    }

    public void setDescription(HashMap<String, String> description) {
        this.description = description;
    }

    public HashMap<String, String> getFlower() {
        return flower;
    }

    public void setFlower(HashMap<String, String> flower) {
        this.flower = flower;
    }

    public HashMap<String, String> getInflorescence() {
        return inflorescence;
    }

    public void setInflorescence(HashMap<String, String> inflorescence) {
        this.inflorescence = inflorescence;
    }

    public HashMap<String, String> getFruit() {
        return fruit;
    }

    public void setFruit(HashMap<String, String> fruit) {
        this.fruit = fruit;
    }

    public HashMap<String, String> getLeaf() {
        return leaf;
    }

    public void setLeaf(HashMap<String, String> leaf) {
        this.leaf = leaf;
    }

    public HashMap<String, String> getStem() {
        return stem;
    }

    public void setStem(HashMap<String, String> stem) {
        this.stem = stem;
    }

    public HashMap<String, String> getHabitat() {
        return habitat;
    }

    public void setHabitat(HashMap<String, String> habitat) {
        this.habitat = habitat;
    }

    public HashMap<String, String> getTrivia() {
        return trivia;
    }

    public void setTrivia(HashMap<String, String> trivia) {
        this.trivia = trivia;
    }

    public HashMap<String, String> getToxicity() {
        return toxicity;
    }

    public void setToxicity(HashMap<String, String> toxicity) {
        this.toxicity = toxicity;
    }

    public HashMap<String, String> getHerbalism() {
        return herbalism;
    }

    public void setHerbalism(HashMap<String, String> herbalism) {
        this.herbalism = herbalism;
    }

    public HashMap<String, String> getWikilinks() {
        return wikilinks;
    }

    public void setWikilinks(HashMap<String, String> wikilinks) {
        this.wikilinks = wikilinks;
    }

    public HashMap<String, ArrayList<String>> getSourceUrls() {
        return sourceUrls;
    }

    public void setSourceUrls(HashMap<String, ArrayList<String>> sourceUrls) {
        this.sourceUrls = sourceUrls;
    }

    public HashMap<String, String> getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(HashMap<String, String> taxonomy) {
        this.taxonomy = taxonomy;
    }
}