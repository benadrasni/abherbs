package sk.ab.herbsbase.fragments;

import sk.ab.common.Constants;
import sk.ab.herbsbase.commons.BaseFilterFragment;
import sk.ab.herbsbase.R;

public class ColorOfFlowers extends BaseFilterFragment {

    public ColorOfFlowers() {
        this.attribute = Constants.COLOR_OF_FLOWERS;
        this.title = R.string.color_of_flower;
        this.iconRes = R.drawable.color_of_flowers;
        this.layout = R.layout.color_of_flowers;
    }
}
