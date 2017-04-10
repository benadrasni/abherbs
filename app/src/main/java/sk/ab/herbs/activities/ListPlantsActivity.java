package sk.ab.herbs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;

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
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
