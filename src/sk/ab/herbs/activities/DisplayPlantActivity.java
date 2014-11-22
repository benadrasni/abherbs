package sk.ab.herbs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import sk.ab.herbs.Constants;
import sk.ab.herbs.Plant;
import sk.ab.herbs.R;
import sk.ab.herbs.fragments.PlantCardsFragment;
import sk.ab.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 27.2.2013
 * Time: 16:59
 * <p/>
 * Activity for displaying selected plant
 */
public class DisplayPlantActivity extends Activity {
    private Plant plant;

    /**
     * Called when the commons is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_frame);

        PlantCardsFragment plantCardsFragment = new PlantCardsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.list_frame, plantCardsFragment)
                .commit();

        getActionBar().hide();
    }

    @Override
    public void onStart() {
        plant = getIntent().getExtras().getParcelable("plant");
        super.onStart();
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);
        Utils.changeLocale(this, language);
    }

    public Plant getPlant() {
        return plant;
    }
}
