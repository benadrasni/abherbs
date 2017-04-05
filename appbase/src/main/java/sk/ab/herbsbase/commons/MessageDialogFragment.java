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
import android.view.View;
import android.widget.TextView;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;

/**
 *
 * Created by adrian on 3. 4. 2017.
 */

public class MessageDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.message_dialog, null);
        TextView textView = (TextView)view.findViewById(R.id.message);
        textView.setText(getArguments().getString(AndroidConstants.MESSAGE_KEY));

        builder.setView(inflater.inflate(R.layout.message_dialog, null))
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
}
