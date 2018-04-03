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

import java.util.Locale;

import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.fragments.UserPreferenceBaseFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.activities.TransparentActivity;

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
                Toast.makeText(getActivity(), R.string.wait_until_finish, Toast.LENGTH_LONG).show();
                Boolean newOfflineMode = (Boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SpecificConstants.OFFLINE_MODE_KEY, newOfflineMode);
                editor.apply();

                if (newOfflineMode) {
                    editor.remove(SpecificConstants.LAST_UPDATE_TIME_KEY);
                    editor.apply();

                    Intent intent = new Intent(getActivity(), TransparentActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    // delete offline files
                    Utils.deleteRecursive(getActivity().getFilesDir());
                }

                DatabaseReference taxonomyRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_APG_IV);
                taxonomyRef.keepSynced(newOfflineMode);
                String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
                DatabaseReference searchInLanguageRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                        + AndroidConstants.SEPARATOR + language);
                searchInLanguageRef.keepSynced(newOfflineMode);
                DatabaseReference searchInLatinRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                        + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_LA);
                searchInLatinRef.keepSynced(newOfflineMode);
                DatabaseReference translationsInLanguage = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                        + AndroidConstants.SEPARATOR + language);
                translationsInLanguage.keepSynced(newOfflineMode);
                DatabaseReference translationsInLanguageGT = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                        + AndroidConstants.SEPARATOR + language + AndroidConstants.LANGUAGE_GT_SUFFIX);
                translationsInLanguageGT.keepSynced(newOfflineMode);
                DatabaseReference translationsInEnglish = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                        + AndroidConstants.SEPARATOR + AndroidConstants.LANGUAGE_EN);
                translationsInEnglish.keepSynced(newOfflineMode);

                return true;
            }
        });
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
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
            DatabaseReference searchInLanguageRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + newLanguage);
            searchInLanguageRef.keepSynced(true);
            searchInLanguageRef = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_SEARCH
                    + AndroidConstants.SEPARATOR + oldLanguage);
            searchInLanguageRef.keepSynced(false);

            DatabaseReference translationsInLanguage = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + newLanguage);
            translationsInLanguage.keepSynced(true);
            translationsInLanguage = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + oldLanguage);
            translationsInLanguage.keepSynced(false);
            DatabaseReference translationsInLanguageGT = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + newLanguage + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(true);
            translationsInLanguageGT = FirebaseDatabase.getInstance().getReference(AndroidConstants.FIREBASE_TRANSLATIONS
                    + AndroidConstants.SEPARATOR + oldLanguage + AndroidConstants.LANGUAGE_GT_SUFFIX);
            translationsInLanguageGT.keepSynced(false);
        }

    }
}
