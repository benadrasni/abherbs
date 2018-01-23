package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.fragments.PropertyListPlusFragment;

/**
 * @see ListPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class ListPlantsPlusActivity extends ListPlantsBaseActivity {

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
