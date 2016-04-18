package sk.ab.herbs.commons;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/25/14
 * Time: 7:46 PM
 * <p/>
 * Interface for ListView item in DrawerLayout
 */
public interface PropertyItem {
    public static int TYPE_DIVIDER = 0;
    public static int TYPE_FILTER = 1;
    public static int TYPE_SETTING = 2;

    public int getPropertyLayout();
    public int getType();
}
