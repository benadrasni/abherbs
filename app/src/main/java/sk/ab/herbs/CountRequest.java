package sk.ab.herbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by adrian on 1.9.2015.
 */
public class CountRequest {
    private Integer objectTypeId;
    private List<FilterAttribute> filterAttributes;

    public CountRequest(Integer objectTypeId, Map<Integer, Integer> filterAttributes) {
        this.objectTypeId = objectTypeId;
        this.filterAttributes = new ArrayList<FilterAttribute>();
        for(Map.Entry<Integer, Integer> entry : filterAttributes.entrySet()) {
            this.filterAttributes.add(new FilterAttribute(entry.getKey(), entry.getValue()));
        }
    }
}
