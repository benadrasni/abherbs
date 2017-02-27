package sk.ab.herbsbase.fragments;

import sk.ab.common.Constants;
import sk.ab.herbsbase.commons.BaseFilterFragment;
import sk.ab.herbsbase.R;

public class NumberOfPetals extends BaseFilterFragment {

  public NumberOfPetals() {
    this.attribute = Constants.NUMBER_OF_PETALS;
    this.title = R.string.number_of_petals;
    this.iconRes = R.drawable.number_of_petals;
    this.layout = R.layout.number_of_petals;
  }
}
