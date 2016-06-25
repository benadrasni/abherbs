package sk.ab.herbs.fragments;

import sk.ab.common.Constants;
import sk.ab.herbs.commons.BaseFilterFragment;
import sk.ab.herbs.R;

public class NumberOfPetals extends BaseFilterFragment {

  public NumberOfPetals() {
    this.attribute = Constants.NUMBER_OF_PETALS;
    this.title = R.string.number_of_petals;
    this.iconRes = R.drawable.number_of_petals;
    this.layout = R.layout.number_of_petals;
  }
}
