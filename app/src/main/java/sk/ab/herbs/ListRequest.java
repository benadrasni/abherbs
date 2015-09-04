package sk.ab.herbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by adrian on 1.9.2015.
 */
public class ListRequest {
    private Integer langId;
    private List<FilterAttribute> filterAttributes;
    private List<Integer> attributes;
    private Integer from;
    private Integer number;

    public ListRequest(Integer langId, Map<Integer, Integer> filterAttributes, List<Integer> attributes, Integer from,
                       Integer number) {
        this.langId = langId;
        this.filterAttributes = new ArrayList<FilterAttribute>();
        for(Map.Entry<Integer, Integer> entry : filterAttributes.entrySet()) {
            this.filterAttributes.add(new FilterAttribute(entry.getKey(), entry.getValue()));
        }
        this.attributes = attributes;
        this.from = from;
        this.number = number;
    }
}
