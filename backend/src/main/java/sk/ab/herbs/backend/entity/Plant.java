package sk.ab.herbs.backend.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Plant {

    @Id Integer plantId;
    String name;
    String wikiName;
    String illustrationUrl;
    Integer heightFrom;
    Integer heightTo;
    Integer floweringFrom;
    Integer floweringTo;
    Integer toxicityClass;
    List<String> photoUrls = new ArrayList<>();

    List<String> filterColor;
    List<String> filterHabitat;
    List<String> filterPetal;
    List<String> filterInflorescence;
    List<String> filterSepal;
    List<String> filterStem;
    List<String> filterLeafShape;
    List<String> filterLeafMargin;
    List<String> filterLeafVenetation;
    List<String> filterLeafArrangement;
    List<String> filterRoot;

    Map<String, String> description = new HashMap<>();
    Map<String, String> flower = new HashMap<>();
    Map<String, String> inflorescence = new HashMap<>();
    Map<String, String> fruit = new HashMap<>();
    Map<String, String> leaf = new HashMap<>();
    Map<String, String> stem = new HashMap<>();
    Map<String, String> habitat = new HashMap<>();
    Map<String, String> trivia = new HashMap<>();
    Map<String, String> toxicity = new HashMap<>();
    Map<String, String> herbalism = new HashMap<>();
    Map<String, List<String>> sourceUrls = new HashMap<>();

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

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getFlower() {
        return flower;
    }

    public void setFlower(Map<String, String> flower) {
        this.flower = flower;
    }

    public Map<String, String> getInflorescence() {
        return inflorescence;
    }

    public void setInflorescence(Map<String, String> inflorescence) {
        this.inflorescence = inflorescence;
    }

    public Map<String, String> getFruit() {
        return fruit;
    }

    public void setFruit(Map<String, String> fruit) {
        this.fruit = fruit;
    }

    public Map<String, String> getLeaf() {
        return leaf;
    }

    public void setLeaf(Map<String, String> leaf) {
        this.leaf = leaf;
    }

    public Map<String, String> getStem() {
        return stem;
    }

    public void setStem(Map<String, String> stem) {
        this.stem = stem;
    }

    public Map<String, String> getHabitat() {
        return habitat;
    }

    public void setHabitat(Map<String, String> habitat) {
        this.habitat = habitat;
    }

    public Map<String, String> getTrivia() {
        return trivia;
    }

    public void setTrivia(Map<String, String> trivia) {
        this.trivia = trivia;
    }

    public Map<String, String> getToxicity() {
        return toxicity;
    }

    public void setToxicity(Map<String, String> toxicity) {
        this.toxicity = toxicity;
    }

    public Map<String, String> getHerbalism() {
        return herbalism;
    }

    public void setHerbalism(Map<String, String> herbalism) {
        this.herbalism = herbalism;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public Map<String, List<String>> getSourceUrls() {
        return sourceUrls;
    }

    public void setSourceUrls(Map<String, List<String>> sourceUrls) {
        this.sourceUrls = sourceUrls;
    }

    public List<String> getFilterColor() {
        return filterColor;
    }

    public void setFilterColor(List<String> filterColor) {
        this.filterColor = filterColor;
    }

    public List<String> getFilterHabitat() {
        return filterHabitat;
    }

    public void setFilterHabitat(List<String> filterHabitat) {
        this.filterHabitat = filterHabitat;
    }

    public List<String> getFilterPetal() {
        return filterPetal;
    }

    public void setFilterPetal(List<String> filterPetal) {
        this.filterPetal = filterPetal;
    }

    public List<String> getFilterInflorescence() {
        return filterInflorescence;
    }

    public void setFilterInflorescence(List<String> filterInflorescence) {
        this.filterInflorescence = filterInflorescence;
    }

    public List<String> getFilterSepal() {
        return filterSepal;
    }

    public void setFilterSepal(List<String> filterSepal) {
        this.filterSepal = filterSepal;
    }

    public List<String> getFilterStem() {
        return filterStem;
    }

    public void setFilterStem(List<String> filterStem) {
        this.filterStem = filterStem;
    }

    public List<String> getFilterLeafShape() {
        return filterLeafShape;
    }

    public void setFilterLeafShape(List<String> filterLeafShape) {
        this.filterLeafShape = filterLeafShape;
    }

    public List<String> getFilterLeafMargin() {
        return filterLeafMargin;
    }

    public void setFilterLeafMargin(List<String> filterLeafMargin) {
        this.filterLeafMargin = filterLeafMargin;
    }

    public List<String> getFilterLeafVenetation() {
        return filterLeafVenetation;
    }

    public void setFilterLeafVenetation(List<String> filterLeafVenetation) {
        this.filterLeafVenetation = filterLeafVenetation;
    }

    public List<String> getFilterLeafArrangement() {
        return filterLeafArrangement;
    }

    public void setFilterLeafArrangement(List<String> filterLeafArrangement) {
        this.filterLeafArrangement = filterLeafArrangement;
    }

    public List<String> getFilterRoot() {
        return filterRoot;
    }

    public void setFilterRoot(List<String> filterRoot) {
        this.filterRoot = filterRoot;
    }
}