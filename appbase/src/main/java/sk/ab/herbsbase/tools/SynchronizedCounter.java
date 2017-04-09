package sk.ab.herbsbase.tools;

/**
 *
 * Created by adrian on 8. 4. 2017.
 */

public class SynchronizedCounter {
    private int c = 0;

    public synchronized void increment() {
        c++;
    }

    public synchronized void decrement() {
        c--;
    }

    public synchronized int value() {
        return c;
    }
}
