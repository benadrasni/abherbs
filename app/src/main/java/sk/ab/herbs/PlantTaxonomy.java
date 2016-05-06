package sk.ab.herbs;

import java.util.List;

/**
 * Created by adrian on 1.9.2015.
 */
public class PlantTaxonomy {
    private String type;
    private List<String> name;
    private List<String> latinName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getLatinName() {
        return latinName;
    }

    public void setLatinName(List<String> latinName) {
        this.latinName = latinName;
    }
}
