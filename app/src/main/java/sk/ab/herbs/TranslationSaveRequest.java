package sk.ab.herbs;

import java.util.List;

/**
 * Created by adrian on 1.9.2015.
 */
public class TranslationSaveRequest {
    private String translationId;
    private Integer plantId;
    private Integer langId;
    private List<String> texts;

    public TranslationSaveRequest(Integer plantId, Integer langId, List<String> texts) {
        this.translationId = plantId.toString() + "_" + langId.toString();
        this.plantId = plantId;
        this.langId = langId;
        this.texts = texts;
    }

    public String getTranslationId() {
        return translationId;
    }

    public List<String> getTexts() {
        return texts;
    }
}
