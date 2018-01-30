package sk.ab.herbsplus.fragments;

import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsbase.commons.BaseSetting;
import sk.ab.herbsbase.commons.PropertyDivider;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.commons.Setting;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.DisplayPlantPlusActivity;
import sk.ab.herbsplus.activities.FeedbackPlusActivity;
import sk.ab.herbsplus.activities.FilterPlantsPlusActivity;
import sk.ab.herbsplus.activities.UserPreferencePlusActivity;

/**
 * @see PropertyListBaseFragment
 *
 * Created by adrian on 12. 3. 2017.
 */

public class PropertyListPlusFragment extends PropertyListBaseFragment {

    PropertyDivider propertyDivider;
    Setting loginSetting;
    Setting logoutSetting;

    @Override
    protected Class getUserPreferenceActivityClass() {
        return UserPreferencePlusActivity.class;
    }

    @Override
    protected Class getFeedbackActivityClass() {
        return FeedbackPlusActivity.class;
    }

    @Override
    protected Class getFilterPlantActivityClass() {
        return FilterPlantsPlusActivity.class;
    }

    @Override
    protected void handleUserSettings(BaseSetting setting) {
        switch (setting.getName()) {
            case AndroidConstants.ITEM_LOGIN:
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
                getActivity().startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        AndroidConstants.REQUEST_SIGN_IN);
                break;
            case AndroidConstants.ITEM_LOGOUT:
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                manageUserSettings();
                                ((DisplayPlantPlusActivity)PropertyListPlusFragment.this.getActivity()).handleLogout();
                            }
                        });
                break;
        };
    }

    @Override
    public void manageUserSettings() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (propertyDivider == null) {
            propertyDivider = new PropertyDivider();
            adapter.add(propertyDivider);
        }

        if (currentUser != null) {
            if (loginSetting !=  null) {
                adapter.remove(loginSetting);
                loginSetting = null;
            }
            if (logoutSetting ==  null) {
                logoutSetting = new Setting(R.string.logout, AndroidConstants.ITEM_LOGOUT);
                adapter.add(logoutSetting);
            }
        } else {
            if (logoutSetting != null) {
                adapter.remove(logoutSetting);
                logoutSetting = null;
            }
            if (loginSetting ==  null) {
                loginSetting = new Setting(R.string.login, AndroidConstants.ITEM_LOGIN);
                adapter.add(loginSetting);
            }
        }
    }
}
