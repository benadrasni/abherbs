package sk.ab.common.entity;

import java.util.List;
import java.util.Map;

/**
 * Wrapper for list of plant names
 */
public class PlantNameList {
    private Map<String, Boolean> names;

    public Map<String, Boolean> getNames() {
        return names;
    }

    public void setNames(Map<String, Boolean> names) {
        this.names = names;
    }
}
