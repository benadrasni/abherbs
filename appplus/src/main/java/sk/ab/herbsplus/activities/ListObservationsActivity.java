package sk.ab.herbsplus.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import sk.ab.herbsplus.R;
import sk.ab.herbsplus.fragments.ObservationFragment;

/**
 *
 * Created by adrian on 2/10/2018.
 */

public class ListObservationsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_observations_activity);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ObservationFragment observationsListFragment = (ObservationFragment) fm.findFragmentByTag("ObservationsList");
        if (observationsListFragment == null) {
            ft.replace(R.id.list_content, new ObservationFragment(), "ObservationsList");
        }
        ft.commit();
    }
}
