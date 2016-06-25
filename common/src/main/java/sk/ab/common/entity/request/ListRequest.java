package sk.ab.common.entity.request;

import java.util.List;
import java.util.Map;

/**
 * Entity which represents request for filtered list
 *
 * Created by adrian on 1.9.2015.
 */
public class ListRequest {
    private String language;
    private String entity;
    private Map<String, String> filterAttributes;

    public ListRequest() {

    }

    public ListRequest(String language, String entity, Map<String, String> filterAttributes) {
        this.language = language;
        this.entity = entity;
        this.filterAttributes = filterAttributes;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(Map<String, String> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }
}
