package sk.ab.herbsplus.fragments;

import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.activities.FilterPlantsPlusActivity;
import sk.ab.herbsplus.activities.UserPreferencePlusActivity;

/**
 * @see PropertyListBaseFragment
 *
 * Created by adrian on 12. 3. 2017.
 */

public class PropertyListPlusFragment extends PropertyListBaseFragment {

    @Override
    protected Class getUserPreferenceActivityClass() {
        return UserPreferencePlusActivity.class;
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsPlusActivity.class;
    }
}
