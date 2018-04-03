package sk.ab.herbs.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;

import sk.ab.herbs.SpecificConstants;
import sk.ab.herbs.fragments.PropertyListFragment;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.DisplayPlantBaseActivity;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.fragments.GalleryFragment;
import sk.ab.herbsbase.fragments.InfoFragment;
import sk.ab.herbsbase.fragments.SourcesFragment;
import sk.ab.herbsbase.fragments.TaxonomyFragment;

/**
 * @see DisplayPlantBaseActivity
 *
 * Created by adrian on 9. 4. 2017.
 */

public class DisplayPlantActivity extends DisplayPlantBaseActivity {

    @Override
    protected PropertyListBaseFragment getNewMenuFragment() {
        return new PropertyListFragment();
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsActivity.class;
    }

    @Override
    protected void addFragments(FragmentTransaction ft) {
        ft.replace(sk.ab.herbsbase.R.id.taxonomy_fragment, new TaxonomyFragment(), "Taxonomy");
        ft.replace(sk.ab.herbsbase.R.id.info_fragment, new InfoFragment(), "Info");
        ft.replace(sk.ab.herbsbase.R.id.gallery_fragment, new GalleryFragment(), "Gallery");
        ft.replace(sk.ab.herbsbase.R.id.sources_fragment, new SourcesFragment(), "Sources");
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected void showWizard() {
        final SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        Boolean showWizard1 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_2_7, false);
        Boolean showWizard2 = !preferences.getBoolean(AndroidConstants.SHOWCASE_DISPLAY_KEY + AndroidConstants.VERSION_1_3_1, false);

        if (showWizard1) {
            showWizardIllustration(editor);
        } else if (showWizard2) {
            showWizardTaxonomy(editor);
        }
    }
}
