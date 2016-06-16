package sk.ab.herbs;

import java.util.Date;

/**
 * Created by adrian on 1.9.2015.
 */
public class RateSave {
    private Long rateId;
    private String date;
    private String status;

    public RateSave(String status) {
        Date date = new Date();
        this.rateId = date.getTime();
        this.date = date.toString();
        this.status = status;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
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
}
