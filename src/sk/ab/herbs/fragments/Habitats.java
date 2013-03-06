package sk.ab.herbs.fragments;

import sk.ab.commons.BaseFilterFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.R;

public class Habitats extends BaseFilterFragment {

  public Habitats() {
    this.attributeId = Constants.HABITATS_ID;
    this.title = R.string.habitats;
    this.iconRes = R.drawable.habitats;
    this.layout = R.layout.habitats;
  }
}
