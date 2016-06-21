package sk.ab.herbs.backend.entity;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;
import java.util.Map;

@Entity
public class Plant {

    @Id Integer plantId;
    String back_url;
    Integer height_from;
    Integer height_to;
    Integer flowering_from;
    Integer flowering_to;
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
    Map<String, List<String>> photo_urls;
    Map<String, List<String>> source_urls;

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

    public Integer getHeight_from() {
        return height_from;
    }

    public void setHeight_from(Integer height_from) {
        this.height_from = height_from;
    }

    public Integer getHeight_to() {
        return height_to;
    }

    public void setHeight_to(Integer height_to) {
        this.height_to = height_to;
    }

    public Integer getFlowering_from() {
        return flowering_from;
    }

    public void setFlowering_from(Integer flowering_from) {
        this.flowering_from = flowering_from;
    }

    public Integer getFlowering_to() {
        return flowering_to;
    }

    public void setFlowering_to(Integer flowering_to) {
        this.flowering_to = flowering_to;
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

    public Map<String, List<String>> getPhoto_urls() {
        return photo_urls;
    }

    public void setPhoto_urls(Map<String, List<String>> photo_urls) {
        this.photo_urls = photo_urls;
    }

    public Map<String, List<String>> getSource_urls() {
        return source_urls;
    }

    public void setSource_urls(Map<String, List<String>> source_urls) {
        this.source_urls = source_urls;
    }
}