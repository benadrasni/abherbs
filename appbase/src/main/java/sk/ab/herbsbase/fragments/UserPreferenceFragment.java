package sk.ab.herbsbase.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.UserPreferenceActivity;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class UserPreferenceFragment extends PreferenceFragment {

    private CheckBoxPreference prefChangeLocale;
    private ListPreference prefLanguage;
    private CheckBoxPreference prefProposeTranslation;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences preferences = getSharedPreferences();

        Boolean changeLocale = preferences.getBoolean(AndroidConstants.CHANGE_LOCALE_KEY, false);
        prefChangeLocale = (CheckBoxPreference)findPreference("changeLocale");
        prefChangeLocale.setChecked(changeLocale);

        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        prefLanguage = (ListPreference)findPreference("prefLanguage");
        prefLanguage.setEnabled(changeLocale);
        if (changeLocale) {
            prefLanguage.setValue(language);
            prefLanguage.setSummary(prefLanguage.getEntry());
        }

        prefChangeLocale.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newChangeLocale = (Boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(AndroidConstants.CHANGE_LOCALE_KEY, newChangeLocale);
                if (!newChangeLocale) {
                    editor.remove(AndroidConstants.LANGUAGE_DEFAULT_KEY);
                    changeLocale(BaseApp.sDefSystemLanguage);
                    prefLanguage.setValue("");
                    prefLanguage.setSummary("");
                }
                editor.apply();
                prefLanguage.setEnabled(newChangeLocale);
                return true;
            }
        });

        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefLanguage.setValue((String) newValue);
                prefLanguage.setSummary(prefLanguage.getEntry());

                SharedPreferences.Editor editor = preferences.edit();

                String newLanguage = (String) newValue;
                if (newLanguage.equals(sk.ab.common.Constants.LANGUAGE_ORIGINAL)) {
                    newLanguage = BaseApp.sDefSystemLanguage;
                }
                changeLocale(newLanguage);

                editor.putString(AndroidConstants.LANGUAGE_DEFAULT_KEY, newLanguage);
                editor.apply();

                return true;
            }
        });

        Boolean proposeTranslation = preferences.getBoolean(AndroidConstants.PROPOSE_TRANSLATION_KEY, false);
        prefProposeTranslation = (CheckBoxPreference)findPreference("proposeTranslation");
        prefProposeTranslation.setChecked(proposeTranslation);
        prefProposeTranslation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(AndroidConstants.PROPOSE_TRANSLATION_KEY, (Boolean) newValue);
                editor.apply();
                return true;
            }
        });
    }

    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(AndroidConstants.PACKAGE, Context.MODE_PRIVATE);
    }

    private void changeLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getActivity().getBaseContext().createConfigurationContext(config);

        updateViews();
        ((UserPreferenceActivity)getActivity()).updateViews();
    }

    private void updateViews() {
        Resources resources = getResources();

        prefChangeLocale.setTitle(resources.getString(R.string.change_locale));
        prefChangeLocale.setSummary(resources.getString(R.string.change_locale_summary));

        prefLanguage.setTitle(resources.getString(R.string.pref_language));

        prefProposeTranslation.setTitle(resources.getString(R.string.propose_translation));
        prefProposeTranslation.setSummary(resources.getString(R.string.propose_translation_summary));
    }

}
