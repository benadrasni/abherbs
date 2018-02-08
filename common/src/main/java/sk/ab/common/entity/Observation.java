package sk.ab.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity which represents single observation
 *
 * Created by adrian on 23. 1. 2018.
 */

public class Observation {

    private String id;
    private String plant;
    private Date date;
    private Double longitude;
    private Double latitude;
    private String note;
    private ArrayList<String> photoPaths;
    private boolean isPublic;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
