package sk.ab.herbs.activities;

import sk.ab.commons.BaseActivity;
import sk.ab.herbs.fragments.ColorOfFlowers;
import sk.ab.herbs.fragments.Habitats;
import sk.ab.herbs.fragments.NumbersOfPetals;

import java.util.ArrayList;

public class FilterPlantsActivity extends BaseActivity {

    public FilterPlantsActivity() {
        filterAttributes = new ArrayList<>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumbersOfPetals());
    }
}
