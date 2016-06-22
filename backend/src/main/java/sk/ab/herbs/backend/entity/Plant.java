package sk.ab.herbs.backend.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;
import java.util.Map;

@Entity
public class Plant {

    @Id Integer plantId;
    String back_url;
    Integer heightFrom;
    Integer heightTo;
    Integer floweringFrom;
    Integer floweringTo;
    Integer toxicity_class;
    Map<String, String> title;
    Map<String, String> description;
    Map<String, String> flower;
    Map<String, String> inflorescence;
    Map<String, String> fruit;
    Map<String, String> leaf;
    Map<String, String> stem;
    Map<String, String> habitat;
    Map<String, String> trivia;
    Map<String, String> toxicity;
    Map<String, String> herbalism;

    Map<String, List<String>> names;
    Map<String, List<String>> photoUrls;
    Map<String, List<String>> sourceUrls;

    List<String> filterColor;
    List<String> filterHabitat;
    List<String> filterPetal;

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }

    public String getBack_url() {
        return back_url;
    }

    public void setBack_url(String back_url) {
        this.back_url = back_url;
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

    public Integer getToxicity_class() {
        return toxicity_class;
    }

    public void setToxicity_class(Integer toxicity_class) {
        this.toxicity_class = toxicity_class;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
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

    public Map<String, List<String>> getNames() {
        return names;
    }

    public void setNames(Map<String, List<String>> names) {
        this.names = names;
    }

    public Map<String, List<String>> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(Map<String, List<String>> photoUrls) {
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
}