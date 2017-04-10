package sk.ab.herbs.activities;

import android.content.Context;
import android.content.SharedPreferences;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantActivity extends DisplayPlantBaseActivity {

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
