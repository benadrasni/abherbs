package sk.ab.herbs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
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

        SharedPreferences preferences = getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Constants.LANGUAGE_EN);
        changeLocale(language);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.about)
                        .setMessage(Html.fromHtml(getString(R.string.about_msg)))
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
