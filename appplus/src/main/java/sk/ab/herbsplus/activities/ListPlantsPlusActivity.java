package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;
import sk.ab.herbsplus.util.UtilsPlus;

/**
 * @see ListPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class ListPlantsPlusActivity extends ListPlantsBaseActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case AndroidConstants.REQUEST_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    getMenuFragment().manageUserSettings();
                    UtilsPlus.saveToken(this);
                } else {
                    // Sign in failed, check response for error code
                    Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected Intent getDisplayPlantActivityIntent() {
        return new Intent(getBaseContext(), DisplayPlantPlusActivity.class);
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected PropertyListBaseFragment getNewMenuFragment() {
        return new PropertyListPlusFragment();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
