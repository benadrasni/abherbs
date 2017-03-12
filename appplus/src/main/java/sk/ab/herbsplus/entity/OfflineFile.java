package sk.ab.herbsplus.entity;

import java.util.Date;

/**
 * Entity for storing offline file in db
 *
 * Created by adrian on 11. 3. 2017.
 */

public class OfflineFile {
    private String name;
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
