package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.SharedPreferences;

import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsplus.SpecificConstants;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantPlusActivity extends DisplayPlantBaseActivity {

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

}
