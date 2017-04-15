package sk.ab.herbs.fragments;

import sk.ab.herbs.activities.FilterPlantsActivity;
import sk.ab.herbs.activities.UserPreferenceActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;

/**
 * @see PropertyListBaseFragment
 *
 * Created by adrian on 12. 3. 2017.
 */

public class PropertyListFragment extends PropertyListBaseFragment {

    @Override
    protected Class getUserPreferenceActivityClass() {
        return UserPreferenceActivity.class;
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsActivity.class;
    }
}
