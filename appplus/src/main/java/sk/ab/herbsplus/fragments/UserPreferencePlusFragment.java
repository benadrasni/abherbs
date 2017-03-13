package sk.ab.herbsplus.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import sk.ab.herbsbase.fragments.UserPreferenceFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;

/**
 * @see UserPreferenceFragment
 *
 * plus preferences_plus:
 * - offline mode
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferencePlusFragment extends UserPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_plus);

    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
