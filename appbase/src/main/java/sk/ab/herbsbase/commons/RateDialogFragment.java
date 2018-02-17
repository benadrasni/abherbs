package sk.ab.herbsbase.commons;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;

/**
 *
 * Created by adrian on 3. 4. 2017.
 */

public class RateDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.rate_dialog, null))
                .setNegativeButton(R.string.rate_never, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = ((FilterPlantsBaseActivity)getActivity()).getSharedPreferences();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NEVER);
                        editor.apply();
                    }
                })
                .setNeutralButton(R.string.rate_later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = ((FilterPlantsBaseActivity)getActivity()).getSharedPreferences();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_NO);
                        editor.putInt(AndroidConstants.RATE_COUNT_KEY, AndroidConstants.RATE_COUNTER);
                        editor.apply();
                    }
                })
                .setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = ((FilterPlantsBaseActivity)getActivity()).getSharedPreferences();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AndroidConstants.RATE_STATE_KEY, AndroidConstants.RATE_DONE);
                        editor.apply();

                        Uri uri = Uri.parse("market://details?id=" + getActivity().getBaseContext().getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getBaseContext().getPackageName())));
                        }
                    }
                });
        return builder.create();
    }
}
