package sk.ab.common.entity;

import java.util.ArrayList;

/**
 *
 */
public class PlantFilter {

    protected String name;
    protected ArrayList<String> filterColor;
    protected ArrayList<String> filterHabitat;
    protected ArrayList<String> filterPetal;
    protected ArrayList<Integer> filterDistribution;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFilterColor() {
        return filterColor;
    }

    public void setFilterColor(ArrayList<String> filterColor) {
        this.filterColor = filterColor;
    }

    public ArrayList<String> getFilterHabitat() {
        return filterHabitat;
    }

    public void setFilterHabitat(ArrayList<String> filterHabitat) {
        this.filterHabitat = filterHabitat;
    }

    public ArrayList<String> getFilterPetal() {
        return filterPetal;
    }

    public void setFilterPetal(ArrayList<String> filterPetal) {
        this.filterPetal = filterPetal;
    }

    public ArrayList<Integer> getFilterDistribution() {
        return filterDistribution;
    }

    public void setFilterDistribution(ArrayList<Integer> filterDistribution) {
        this.filterDistribution = filterDistribution;
    }
}
