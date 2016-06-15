package sk.ab.herbs;

import java.util.Date;

/**
 * Created by adrian on 1.9.2015.
 */
public class RateSave {
    private Date date;
    private String status;

    public RateSave(String status) {
        this.date = new Date();
        this.status = status;
    }
}
