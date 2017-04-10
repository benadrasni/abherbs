package sk.ab.herbs.activities;

import sk.ab.herbs.fragments.UserPreferenceFragment;
import sk.ab.herbsbase.activities.UserPreferenceBaseActivity;

/**
 * @see UserPreferenceBaseActivity
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferenceActivity extends UserPreferenceBaseActivity {

    @Override
    protected void addFragment() {
        getFragmentManager().beginTransaction().replace(sk.ab.herbsbase.R.id.content_wrapper, new UserPreferenceFragment()).commit();
    }
}
