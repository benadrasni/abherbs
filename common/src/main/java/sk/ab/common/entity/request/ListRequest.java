package sk.ab.common.entity.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity which represents request for filtered list
 *
 * Created by adrian on 1.9.2015.
 */
public class ListRequest {
    private String language;
    private Map<String, String> filterAttributes;
    private List<String> attributes;

    public ListRequest(String language, Map<String, String> filterAttributes, List<String> attributes) {
        this.language = language;
        this.filterAttributes = filterAttributes;
        this.attributes = attributes;
    }
}
