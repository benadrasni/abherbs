package sk.ab.herbsbase.fragments;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.UserPreferenceBaseActivity;
import sk.ab.herbsbase.tools.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11/26/14
 * Time: 9:23 PM
 * <p/>
 */
public abstract class UserPreferenceBaseFragment extends PreferenceFragment {

    private ListPreference prefLanguage;
    protected EditTextPreference prefCacheSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences preferences = getSharedPreferences();

        String language = preferences.getString(AndroidConstants.LANGUAGE_DEFAULT_KEY, BaseApp.sDefSystemLocale.getLanguage());
        prefLanguage = (ListPreference)findPreference("prefLanguage");
        List<String> languageList = new ArrayList<>(Arrays.asList(AndroidConstants.LANGUAGES));
        List<String> languageCodeList = new ArrayList<>(Arrays.asList(AndroidConstants.LANGUAGE_CODES));
        if (!languageCodeList.contains(BaseApp.sDefSystemLocale.getLanguage())) {
            languageList.add(0, BaseApp.sDefSystemLocale.getDisplayLanguage() + AndroidConstants.LANGUAGE_NOT_SUPPORTED);
            languageCodeList.add(0, BaseApp.sDefSystemLocale.getLanguage());
        }
        String[] languages = languageList.toArray(new String[languageList.size()]);
        String[] languageCodes = languageCodeList.toArray(new String[languageCodeList.size()]);
        prefLanguage.setEntries(languages);
        prefLanguage.setEntryValues(languageCodes);
        prefLanguage.setValue(language);
        prefLanguage.setSummary(prefLanguage.getEntry());

        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newLanguage = (String) newValue;
                if (!newLanguage.equals(Locale.getDefault().getLanguage())) {
                    prefLanguage.setValue(newLanguage);
                    prefLanguage.setSummary(prefLanguage.getEntry());

                    SharedPreferences.Editor editor = preferences.edit();
                    changeLocale(newLanguage);

                    editor.putString(AndroidConstants.LANGUAGE_DEFAULT_KEY, newLanguage);
                    editor.apply();
                }

                return true;
            }
        });

        final Integer cacheSize = preferences.getInt(AndroidConstants.CACHE_SIZE_KEY, AndroidConstants.DEFAULT_CACHE_SIZE);
        prefCacheSize = (EditTextPreference)findPreference("cacheSize");
        prefCacheSize.setText("" + cacheSize);
        prefCacheSize.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    int newCacheSize = Integer.parseInt((String) newValue);
                    if (newCacheSize > 0) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.CACHE_SIZE_KEY, newCacheSize);
                        editor.apply();

                        BaseApp.initImageLoader(getActivity().getApplicationContext(), newCacheSize);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Cache size must be greater than 0", Toast.LENGTH_LONG).show();
                        prefCacheSize.setText("" + cacheSize);
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cache size must be integer value", Toast.LENGTH_LONG).show();
                    prefCacheSize.setText("" + cacheSize);
                }
                return true;
            }
        });
    }

    protected abstract SharedPreferences getSharedPreferences();

    private void changeLocale(String language) {
        Utils.changeLocale(getActivity().getBaseContext(), language);
        updateViews();
        ((UserPreferenceBaseActivity)getActivity()).updateViews();
    }

    protected void updateViews() {
        Resources resources = getResources();

        prefLanguage.setTitle(resources.getString(R.string.pref_language));

        prefCacheSize.setTitle(resources.getString(R.string.cache_size));
        prefCacheSize.setSummary(resources.getString(R.string.cache_size_summary));
    }

}
