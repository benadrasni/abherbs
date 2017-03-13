package sk.ab.herbsplus.fragments;

import sk.ab.herbsbase.commons.PropertyListFragment;
import sk.ab.herbsplus.activities.UserPreferencePlusActivity;

/**
 * @see PropertyListFragment
 *
 * Created by adrian on 12. 3. 2017.
 */

public class PropertyListPlusFragment extends PropertyListFragment {

    @Override
    protected Class getUserPreferenceActivityClass() {
        return UserPreferencePlusActivity.class;
    }
}
