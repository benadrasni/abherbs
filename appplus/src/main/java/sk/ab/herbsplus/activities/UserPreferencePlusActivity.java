package sk.ab.herbsplus.activities;

import sk.ab.herbsbase.activities.UserPreferenceBaseActivity;
import sk.ab.herbsplus.fragments.UserPreferencePlusFragment;

/**
 * @see UserPreferenceBaseActivity
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferencePlusActivity extends UserPreferenceBaseActivity {

    @Override
    protected void addFragment() {
        getFragmentManager().beginTransaction().replace(sk.ab.herbsbase.R.id.content_wrapper, new UserPreferencePlusFragment()).commit();
    }
}
