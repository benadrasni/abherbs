package sk.ab.commons;

public abstract class BaseSetting implements PropertyItem {

    protected int title;
    protected int propertyLayout;

    public BaseSetting() {
    }

    @Override
    public int getPropertyLayout() {
        return propertyLayout;
    }

    @Override
    public int getType() {
        return TYPE_SETTING;
    }

    public int getTitle() {
        return title;
    }
}
