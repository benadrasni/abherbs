package sk.ab.herbs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import sk.ab.commons.BaseActivity;
import sk.ab.commons.BaseFilterFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.R;
import sk.ab.herbs.TestPlants;
import sk.ab.herbs.fragments.ColorOfFlowers;
import sk.ab.herbs.fragments.Habitats;
import sk.ab.herbs.fragments.NumbersOfPetals;

import java.util.ArrayList;

public class FilterPlantsActivity extends BaseActivity {

    public FilterPlantsActivity() {
        filterAttributes = new ArrayList<BaseFilterFragment>();
        filterAttributes.add(new ColorOfFlowers());
        filterAttributes.add(new Habitats());
        filterAttributes.add(new NumbersOfPetals());
    }

    /**
     * Called when the commons is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }
        if (mContent == null && filterAttributes.size() > 0) {
            mContent = filterAttributes.get(0);
            position = 0;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.filter_content_frame, mContent)
                .commit();
    }
}
