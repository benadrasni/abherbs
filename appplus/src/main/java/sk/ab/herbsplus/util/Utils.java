package sk.ab.herbsplus.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseUser;

import sk.ab.herbsplus.R;

/**
 *
 * Created by adrian on 2/9/2018.
 */

public class Utils {

    public static boolean isSubscription(FirebaseUser user) {
        return true;
    }

    public static AlertDialog SubscriptionDialog(Activity activity) {
        return new AlertDialog.Builder(activity)
                //set message, title, and icon
                .setTitle(R.string.subscription)
                .setMessage(R.string.subscription_info)
                .setIcon(R.drawable.ic_flower_black_24dp)

                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }

                })

                .create();
    }
}
