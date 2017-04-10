package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.SharedPreferences;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.fragments.UserPreferenceBaseFragment;

/**
 * @see UserPreferenceBaseFragment
 *
 * Created by adrian on 9. 4. 2017.
 */

public class UserPreferenceFragment extends UserPreferenceBaseFragment {

    @Override
    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
