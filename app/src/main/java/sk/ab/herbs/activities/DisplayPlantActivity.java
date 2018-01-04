package sk.ab.herbs.activities;

import android.content.Context;
import android.content.SharedPreferences;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbs.fragments.PropertyListFragment;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantActivity extends DisplayPlantBaseActivity {

    @Override
    protected PropertyListBaseFragment getMenuFragment() {
        return new PropertyListFragment();
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsActivity.class;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
