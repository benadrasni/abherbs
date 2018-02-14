package sk.ab.herbsplus.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsbase.commons.BaseSetting;
import sk.ab.herbsbase.commons.PropertyDivider;
import sk.ab.herbsbase.commons.PropertyListBaseFragment;
import sk.ab.herbsbase.commons.Setting;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.FeedbackPlusActivity;
import sk.ab.herbsplus.activities.FilterPlantsPlusActivity;
import sk.ab.herbsplus.activities.SubscriptionActivity;
import sk.ab.herbsplus.activities.UserPreferencePlusActivity;
import sk.ab.herbsplus.util.Utils;

/**
 * @see PropertyListBaseFragment
 *
 * Created by adrian on 12. 3. 2017.
 */

public class PropertyListPlusFragment extends PropertyListBaseFragment {

    PropertyDivider propertyDivider;
    Setting loginSetting;
    Setting logoutSetting;
    Setting subscriptionSetting;

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
        final BaseActivity activity = (BaseActivity) getActivity();
        if (activity != null && !activity.isDestroyed()) {
            switch (setting.getName()) {
                case AndroidConstants.ITEM_LOGIN:
                    Utils.LoginActivity(activity);
                    break;
                case AndroidConstants.ITEM_SUBSCRIPTION:
                    Intent intent = new Intent(activity, SubscriptionActivity.class);
                    activity.startActivity(intent);
                    break;
                case AndroidConstants.ITEM_LOGOUT:
                    AuthUI.getInstance()
                            .signOut(activity)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    manageUserSettings();
                                    activity.handleLogout();
                                }
                            });
                    break;
            }
        }
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
            if (subscriptionSetting ==  null) {
                subscriptionSetting = new Setting(R.string.subscription, AndroidConstants.ITEM_SUBSCRIPTION);
                adapter.add(subscriptionSetting);
            }
            if (logoutSetting ==  null) {
                logoutSetting = new Setting(R.string.logout, AndroidConstants.ITEM_LOGOUT);
                adapter.add(logoutSetting);
            }
        } else {
            if (subscriptionSetting !=  null) {
                adapter.remove(subscriptionSetting);
                subscriptionSetting = null;
            }
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
