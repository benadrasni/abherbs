package sk.ab.herbsplus.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Locale;

import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.fragments.UserPreferenceBaseFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.activities.MyFilterActivity;
import sk.ab.herbsplus.activities.MyRegionPlusActivity;
import sk.ab.herbsplus.services.OfflineService;

/**
 * @see UserPreferenceBaseFragment
 *
 * plus preferences_plus:
 * - offline mode
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferencePlusFragment extends UserPreferenceBaseFragment {

    private CheckBoxPreference prefOfflineMode;
    private Preference prefMyFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_plus);

        final SharedPreferences preferences = getSharedPreferences();

        Boolean offlineMode = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        prefOfflineMode = (CheckBoxPreference)findPreference("offlineMode");
        prefOfflineMode.setChecked(offlineMode);

        prefOfflineMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newOfflineMode = (Boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SpecificConstants.OFFLINE_MODE_KEY, newOfflineMode);
                editor.apply();

                if (!newOfflineMode) {
                    // delete offline files
                    Utils.deleteRecursive(new File(getActivity().getFilesDir() + AndroidConstants.SEPARATOR + AndroidConstants.STORAGE_PHOTOS));
                    editor.remove(SpecificConstants.OFFLINE_PLANT_KEY);
                    editor.remove(SpecificConstants.OFFLINE_FAMILY_KEY);
                    editor.apply();
                } else {
                    Toast.makeText(getActivity(), R.string.synchronization_on_background, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), OfflineService.class);
                    OfflineService.enqueueWork(getActivity().getApplicationContext(), intent);
                }

                // Firebase synchronization
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference countsRef = database.getReference(AndroidConstants.FIREBASE_COUNTS);
                countsRef.keepSynced(newOfflineMode);
                DatabaseReference listsRef = database.getReference(AndroidConstants.FIREBASE_LISTS);
                listsRef.keepSynced(newOfflineMode);
                DatabaseReference plantsRef = database.getReference(AndroidConstants.FIREBASE_PLANTS);
                plantsRef.keepSynced(newOfflineMode);
                DatabaseReference plantsHeadersRef = database.getReference(AndroidConstants.FIREBASE_PLANTS_HEADERS);
                plantsHeadersRef.keepSynced(newOfflineMode);
                DatabaseReference taxonomyRef = database.getReference(AndroidConstants.FIREBASE_APG_IV);
                taxonomyRef.keepSynced(newOfflineMode);

                // language dependent
                String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
                DatabaseReference taxonomyInLanguageRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS_TAXONOMY
                        + AndroidConstants.SEPARATOR + language);
                taxonomyInLanguageRef.keepSynced(newOfflineMode);
                DatabaseReference searchInLanguageRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                        + AndroidConstants.SEPARATOR + language);
                searchInLanguageRef.keepSynced(newOfflineMode);
                DatabaseReference searchInLatinRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                        + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_LA);
                searchInLatinRef.keepSynced(newOfflineMode);
                DatabaseReference translationsInLanguage = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                   + AndroidConstants.SEPARATOR + language);
                translationsInLanguage.keepSynced(newOfflineMode);
                DatabaseReference translationsInLanguageGT = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                        + AndroidConstants.SEPARATOR + language + AndroidConstants.LANGUAGE_GT_SUFFIX);
                translationsInLanguageGT.keepSynced(newOfflineMode);
                DatabaseReference translationsInEnglish = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                        + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_EN);
                translationsInEnglish.keepSynced(newOfflineMode);

                return true;
            }
        });

        prefMyFilter = findPreference("myFilter");
        prefMyFilter.setIntent(new Intent(this.getActivity(), MyFilterActivity.class));
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    @Override
    protected Intent getMyRegionIntent() {
        return new Intent(this.getActivity(), MyRegionPlusActivity.class);
    }

    @Override
    protected void updateViews() {
        super.updateViews();

        Resources resources = getResources();

        prefOfflineMode.setTitle(resources.getString(R.string.offline_mode));
        prefOfflineMode.setSummary(resources.getString(R.string.offline_mode_summary));
    }

    @Override
    protected void updateLanguagePreferences(String oldLanguage, String newLanguage) {
        final SharedPreferences preferences = getSharedPreferences();

        Boolean offlineMode = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        if (offlineMode) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference taxonomyInLanguageRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS_TAXONOMY
                    + AndroidConstants.SEPARATOR + newLanguage);
            taxonomyInLanguageRef.keepSynced(true);
            taxonomyInLanguageRef = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS_TAXONOMY
                    + AndroidConstants.SEPARATOR + oldLanguage);
            taxonomyInLanguageRef.keepSynced(false);
            DatabaseReference searchInLanguageRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + newLanguage);
            searchInLanguageRef.keepSynced(true);
            searchInLanguageRef = database.getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + oldLanguage);
            searchInLanguageRef.keepSynced(false);

            DatabaseReference translationsInLanguage = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + newLanguage);
            translationsInLanguage.keepSynced(true);
            translationsInLanguage = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + oldLanguage);
            translationsInLanguage.keepSynced(false);
            DatabaseReference translationsInLanguageGT = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + newLanguage + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(true);
            translationsInLanguageGT = database.getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + oldLanguage + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(false);
        }

    }
}
