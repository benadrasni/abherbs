package sk.ab.herbs.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import sk.ab.herbs.Constants;
import sk.ab.herbs.R;
import sk.ab.tools.Utils;

import java.util.Locale;

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

        SharedPreferences preferences = getActivity().getSharedPreferences("sk.ab.herbs", Context.MODE_PRIVATE);
        String language = preferences.getString(Constants.LANGUAGE_DEFAULT_KEY, Locale.getDefault().getLanguage());

        ListPreference prefLanguage = (ListPreference)findPreference("prefLanguage");
        prefLanguage.setValue(language);
        prefLanguage.setSummary(prefLanguage.getEntry());
        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Utils.changeLocale(getActivity(), (String)newValue);
                getActivity().recreate();
                return true;
            }
        });
    }

}
