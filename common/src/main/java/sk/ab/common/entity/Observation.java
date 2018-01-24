package sk.ab.common.entity;

import java.util.Date;
import java.util.List;

/**
 * Entity which represents single observation
 *
 * Created by adrian on 28.9.2016.
 */

public class Observation {

    private String id;
    private Date date;
    private double longitude;
    private double latitude;
    private List<String> photoPaths;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<String> getPhotoPaths() {
        return photoPaths;
    }

    public void setPhotoPaths(List<String> photoPaths) {
        this.photoPaths = photoPaths;
    }
}
