package sk.ab.common.entity;

import java.util.ArrayList;

/**
 *
 * Created by adrian on 8. 4. 2017.
 */

public class PlantTranslation {
    private String label;
    private ArrayList<String> names;
    private ArrayList<String> sourceUrls;
    private String description;
    private String flower;
    private String inflorescence;
    private String fruit;
    private String leaf;
    private String stem;
    private String habitat;
    private String trivia;
    private String toxicity;
    private String herbalism;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getSourceUrls() {
        return sourceUrls;
    }

    public void setSourceUrls(ArrayList<String> sourceUrls) {
        this.sourceUrls = sourceUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getInflorescence() {
        return inflorescence;
    }

    public void setInflorescence(String inflorescence) {
        this.inflorescence = inflorescence;
    }

    public String getFruit() {
        return fruit;
    }

    public void setFruit(String fruit) {
        this.fruit = fruit;
    }

    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getHabitat() {
        return habitat;
    }

    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }

    public String getTrivia() {
        return trivia;
    }

    public void setTrivia(String trivia) {
        this.trivia = trivia;
    }

    public String getToxicity() {
        return toxicity;
    }

    public void setToxicity(String toxicity) {
        this.toxicity = toxicity;
    }

    public String getHerbalism() {
        return herbalism;
    }

    public void setHerbalism(String herbalism) {
        this.herbalism = herbalism;
    }
}
