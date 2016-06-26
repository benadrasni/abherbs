package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.Locale;

import sk.ab.herbs.Constants;
import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public class UserPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);

        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());
        final ListPreference prefLanguage = (ListPreference)findPreference("prefLanguage");
        prefLanguage.setValue(language);
        prefLanguage.setSummary(prefLanguage.getEntry());

        Boolean proposeTranslation = preferences.getBoolean(Constants.PROPOSE_TRANSLATION_KEY, false);
        final CheckBoxPreference prefProposeTranslation = (CheckBoxPreference)findPreference("proposeTranslation");
        prefProposeTranslation.setChecked(proposeTranslation);

        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefLanguage.setValue((String) newValue);
                prefLanguage.setSummary(prefLanguage.getEntry());

                SharedPreferences.Editor editor = preferences.edit();

                String newLanguage = (String) newValue;
                if (newLanguage.equals(sk.ab.common.Constants.LANGUAGE_ORIGINAL)) {
                    newLanguage = HerbsApp.sDefSystemLanguage;
                }
                changeLocale(newLanguage);

                editor.putString(Constants.LANGUAGE_DEFAULT_KEY, newLanguage);
                editor.apply();

                return true;
            }
        });

        prefProposeTranslation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.PROPOSE_TRANSLATION_KEY, (Boolean) newValue);
                editor.apply();
                return true;
            }
        });


    }

    private void changeLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        getActivity().recreate();
    }

}
