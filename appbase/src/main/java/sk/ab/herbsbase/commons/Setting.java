package sk.ab.herbsbase.commons;

import sk.ab.herbsbase.R;

public class Setting extends BaseSetting {

    public Setting(int title, String name) {
        this.propertyLayout = R.layout.property_setting;
        this.title = title;
        this.name = name;
    }
}
