package sk.ab.herbs.fragments;

import sk.ab.commons.BaseFilterFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.R;

public class ColorOfFlowers extends BaseFilterFragment {

    public ColorOfFlowers() {
        this.attributeId = Constants.COLOR_OF_FLOWERS_ID;
        this.title = R.string.color_of_flower;
        this.iconRes = R.drawable.color_of_flowers;
        this.layout = R.layout.color_of_flowers;
    }
}
