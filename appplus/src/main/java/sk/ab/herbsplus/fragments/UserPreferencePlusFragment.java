package sk.ab.herbsplus.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;

import sk.ab.common.util.Utils;
import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.BaseApp;
import sk.ab.herbsbase.fragments.UserPreferenceFragment;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.SpecificConstants;
import sk.ab.herbsplus.StorageLoading;
import sk.ab.herbsplus.activities.FilterPlantsPlusActivity;
import sk.ab.herbsplus.activities.SplashActivity;
import sk.ab.herbsplus.activities.UserPreferencePlusActivity;

/**
 * @see UserPreferenceFragment
 *
 * plus preferences_plus:
 * - offline mode
 *
 * Created by adrian on 12. 3. 2017.
 */

public class UserPreferencePlusFragment extends UserPreferenceFragment {

    private CheckBoxPreference prefOfflineMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_plus);

        final SharedPreferences preferences = getSharedPreferences();

        Boolean offlineMode = preferences.getBoolean(SpecificConstants.OFFLINE_MODE_KEY, false);
        prefOfflineMode = (CheckBoxPreference)findPreference("offlineMode");
        prefOfflineMode.setChecked(offlineMode);
        prefCacheSize.setEnabled(!offlineMode);

        prefOfflineMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean newOfflineMode = (Boolean) newValue;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SpecificConstants.OFFLINE_MODE_KEY, newOfflineMode);
                editor.apply();
                if (newOfflineMode) {
                    editor.remove(SpecificConstants.LAST_UPDATE_TIME_KEY);
                    editor.apply();
                    StorageLoading storageLoading = new StorageLoading(getActivity(), null);
                    storageLoading.downloadOfflineFiles();
                    ((BaseApp)getActivity().getApplication()).initImageLoader(getActivity().getApplicationContext(), 0);
                } else {
                    // delete offline files
                    Utils.deleteRecursive(getActivity().getFilesDir());
                    Integer cacheSize = preferences.getInt(AndroidConstants.CACHE_SIZE_KEY, AndroidConstants.DEFAULT_CACHE_SIZE);
                    ((BaseApp)getActivity().getApplication()).initImageLoader(getActivity().getApplicationContext(), cacheSize);
                }
                prefCacheSize.setEnabled(!newOfflineMode);
                return true;
            }
        });
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(SpecificConstants.PACKAGE, Context.MODE_PRIVATE);
    }
}
