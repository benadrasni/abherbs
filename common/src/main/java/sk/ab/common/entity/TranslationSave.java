package sk.ab.common.entity;

import java.util.List;

/**
 * Created by adrian on 1.9.2015.
 */
public class TranslationSave {
    private String translationId;
    private Integer plantId;
    private String language;
    private List<String> texts;

    public TranslationSave(Integer plantId, String language, List<String> texts) {
        this.translationId = plantId.toString() + "_" + language;
        this.plantId = plantId;
        this.language = language;
        this.texts = texts;
    }

    public String getTranslationId() {
        return translationId;
    }

    public List<String> getTexts() {
        return texts;
    }
}
