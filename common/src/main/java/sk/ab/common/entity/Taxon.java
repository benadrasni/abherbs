package sk.ab.common.entity;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by adrian on 9. 3. 2017.
 */

public class Taxon {
    String type;
    Map<String, List<String>> names;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, List<String>> getNames() {
        return names;
    }

    public void setNames(Map<String, List<String>> names) {
        this.names = names;
    }
}
