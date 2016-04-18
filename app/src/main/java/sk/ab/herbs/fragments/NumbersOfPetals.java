package sk.ab.herbs.fragments;

import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.R;

public class NumbersOfPetals extends BaseFilterFragment {

  public NumbersOfPetals() {
    this.attributeId = Constants.NUMBER_OF_PETALS_ID;
    this.title = R.string.number_of_petals;
    this.iconRes = R.drawable.number_of_petals;
    this.layout = R.layout.number_of_petals;
  }
}
