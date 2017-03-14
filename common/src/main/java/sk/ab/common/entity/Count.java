package sk.ab.common.entity;

/**
 * Response object for CountRequest
 *
 * Created by adrian on 25.6.2016.
 */
public class Count {
    private int count;

    public Count() {
    }

    public Count(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
