package sk.ab.herbs.backend;

import java.util.List;

/**
 * Created by adrian on 5.5.2016.
 */
public class Taxon {
    String type;
    List<String> name;
    List<String> latinName;

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
