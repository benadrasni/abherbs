package sk.ab.herbsplus.activities;

import sk.ab.herbsbase.activities.UserPreferenceActivity;
import sk.ab.herbsplus.fragments.UserPreferencePlusFragment;

/**
 * @see UserPreferenceActivity
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferencePlusActivity extends UserPreferenceActivity {

    protected void addFragment() {
        getFragmentManager().beginTransaction().replace(sk.ab.herbsbase.R.id.content_wrapper, new UserPreferencePlusFragment()).commit();
    }
}
