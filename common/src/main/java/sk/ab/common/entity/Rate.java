package sk.ab.common.entity;

import java.util.Date;

public class Rate {

    Long id;
    String date;
    String status;
    String country;

    public Rate() {

    }

    public Rate(String status, String country) {
        Date date = new Date();
        this.id = date.getTime();
        this.date = date.toString();
        this.status = status;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
