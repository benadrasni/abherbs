package sk.ab.common.entity;

/**
 *
 * Created by adrian on 16.4.2017.
 */
public class PlantName {
    private String name;
    private String nameWithoutDiacritics;
    private int count;
    private String plantName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameWithoutDiacritics() {
        return nameWithoutDiacritics;
    }

    public void setNameWithoutDiacritics(String nameWithoutDiacritics) {
        this.nameWithoutDiacritics = nameWithoutDiacritics;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }
}
