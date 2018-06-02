package sk.ab.common.entity;

import java.util.List;

public class Translation {

    String translationId;
    Integer plantId;
    String language;
    List<String> texts;

    public Translation() {

    }

    public Translation(Integer plantId, String language, List<String> texts) {
        this.translationId = plantId.toString() + "_" + language;
        this.plantId = plantId;
        this.language = language;
        this.texts = texts;
    }

    public String getTranslationId() {
        return translationId;
    }

    public void setTranslationId(String translationId) {
        this.translationId = translationId;
    }

    public Integer getPlantId() {
        return plantId;
    }

    public void setPlantId(Integer plantId) {
        this.plantId = plantId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}