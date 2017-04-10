package sk.ab.herbsplus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.WindowManager;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.activities.ListPlantsBaseActivity;
import sk.ab.herbsbase.entity.PlantParcel;
import sk.ab.herbsbase.entity.PlantTranslationParcel;
import sk.ab.herbsplus.SpecificConstants;

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
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
