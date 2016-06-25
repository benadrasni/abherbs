package sk.ab.common.entity.request;

import java.util.Map;

/**
 * Entity which represents request for filtered count
 *
 * Created by adrian on 1.9.2015.
 */
public class CountRequest {
    private String entity;
    private Map<String, String> filterAttributes;

    public CountRequest() {

    }

    public CountRequest(String entity, Map<String, String> filterAttributes) {
        this.entity = entity;
        this.filterAttributes = filterAttributes;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Map<String, String> getFilterAttributes() {
        return filterAttributes;
    }

    public void setFilterAttributes(Map<String, String> filterAttributes) {
        this.filterAttributes = filterAttributes;
    }
}
