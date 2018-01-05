package sk.ab.herbs.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbsbase.activities.SplashBaseActivity;


/**
 * Splash activity
 *
 * Created by adrian on 11.3.2017.
 */
public class SplashActivity extends SplashBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startApplication();
    }

    @Override
    protected Class getFilterPlantsActivityClass() {
        return FilterPlantsActivity.class;
    }

    @Override
    protected Class getListPlantsActivityClass() {
        return ListPlantsActivity.class;
    }

    @Override
    protected Class getDisplayPlantActivityClass() {
        return DisplayPlantActivity.class;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
