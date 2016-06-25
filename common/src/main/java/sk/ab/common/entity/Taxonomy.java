package sk.ab.common.entity;

import java.util.List;

/**
 * Wrapper for list of taxons
 */
public class Taxonomy {
    private List<PlantTaxon> items;

    public List<PlantTaxon> getItems() {
        return items;
    }
}
