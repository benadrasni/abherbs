package sk.ab.herbsbase.commons;

import sk.ab.herbsbase.R;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/25/14
 * Time: 8:02 PM
 * <p/>
 * Divider
 */
public class PropertyDivider implements PropertyItem {

    @Override
    public int getPropertyLayout() {
        return R.layout.property_divider;
    }

    @Override
    public int getType() {
        return TYPE_DIVIDER;
    }
}
