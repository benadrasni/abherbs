package sk.ab.common.entity;

import java.util.HashMap;

/**
 *
 */
public class PlantHeader {

    protected String id;
    protected String url;
    protected HashMap<String, String> label = new HashMap<>();
    protected HashMap<String, String> family = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, String> getLabel() {
        return label;
    }

    public void setLabel(HashMap<String, String> label) {
        this.label = label;
    }

    public HashMap<String, String> getFamily() {
        return family;
    }

    public void setFamily(HashMap<String, String> family) {
        this.family = family;
    }
}
