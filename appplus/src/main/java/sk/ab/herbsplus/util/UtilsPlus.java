package sk.ab.herbsplus.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.activities.BaseActivity;
import sk.ab.herbsplus.R;
import sk.ab.herbsplus.activities.SubscriptionActivity;

/**
 *
 * Created by adrian on 2/9/2018.
 */

public class UtilsPlus {

    public static AlertDialog SubscriptionDialog(final Activity activity) {
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.subscription)
                .setMessage(R.string.subscription_info)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })
                .setPositiveButton(R.string.subscribe, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Intent intent = new Intent(activity, SubscriptionActivity.class);
                        activity.startActivity(intent);
                    }
                })

                .create();
    }

    public static AlertDialog LoginDialog(final Activity activity) {
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.login)
                .setMessage(R.string.no_observations_login)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        LoginActivity(activity);
                    }
                })

                .create();
    }

    public static void LoginActivity(Activity activity) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                AndroidConstants.REQUEST_SIGN_IN);
    }

    public static void saveToken(BaseActivity activity) {
        String token = activity.getSharedPreferences().getString(AndroidConstants.TOKEN_KEY, null);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (token != null && currentUser != null) {
            DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
            // by user, by date
            mFirebaseRef.child(AndroidConstants.FIREBASE_USERS)
                    .child(currentUser.getUid())
                    .child(AndroidConstants.FIREBASE_USERS_TOKEN)
                    .setValue(token);
        }
    }
}