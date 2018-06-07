package sk.ab.common.entity;

import java.util.ArrayList;

/**
 *
 */
public class PlantHeader {

    protected String name;
    protected String url;
    protected String family;
    protected ArrayList<Integer> filterColor;
    protected ArrayList<Integer> filterHabitat;
    protected ArrayList<Integer> filterPetal;
    protected ArrayList<Integer> filterDistribution;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public ArrayList<Integer> getFilterColor() {
        return filterColor;
    }

    public void setFilterColor(ArrayList<Integer> filterColor) {
        this.filterColor = filterColor;
    }

    public ArrayList<Integer> getFilterHabitat() {
        return filterHabitat;
    }

    public void setFilterHabitat(ArrayList<Integer> filterHabitat) {
        this.filterHabitat = filterHabitat;
    }

    public ArrayList<Integer> getFilterPetal() {
        return filterPetal;
    }

    public void setFilterPetal(ArrayList<Integer> filterPetal) {
        this.filterPetal = filterPetal;
    }

    public ArrayList<Integer> getFilterDistribution() {
        return filterDistribution;
    }

    public void setFilterDistribution(ArrayList<Integer> filterDistribution) {
        this.filterDistribution = filterDistribution;
    }
}
