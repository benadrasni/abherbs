package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbs.fragments.PropertyListFragment;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;

/**
 * @see ListPlantsBaseActivity
 *
 * Created by adrian on 11. 3. 2017.
 */

public class ListPlantsActivity extends ListPlantsBaseActivity {

    @Override
    protected Intent getDisplayPlantActivityIntent() {
        return new Intent(getBaseContext(), DisplayPlantActivity.class);
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsActivity.class;
    }

    @Override
    protected PropertyListBaseFragment getMenuFragment() {
        return new PropertyListFragment();
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
