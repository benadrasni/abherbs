package sk.ab.herbs.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;

@Entity
public class Translation {

    @Id String translationId;
    Integer plantId;
    Integer langId;
    List<String> texts;

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

    public Integer getLangId() {
        return langId;
    }

    public void setLangId(Integer langId) {
        this.langId = langId;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }
}