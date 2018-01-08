package org.trv.alex.unshorturl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class URLInfoDialog extends DialogFragment {

    public static final String URL_KEY = "urlKey";

    public static final int DIALOG_ID = 2;

    public static URLInfoDialog newInstance(String url) {
        URLInfoDialog dialog = new URLInfoDialog();

        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String url = getArguments().getString(URL_KEY);

        final Bundle args = getArguments();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.long_url)
                .setMessage(url)
                // Button which copy url to clipboard
                .setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ((ButtonActionListener) getActivity()).onPositive(args, DialogType.URL_INFO_DIALOG);


                    }
                })
                // Button which open url in browser or any other external app
                .setNeutralButton(R.string.open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ButtonActionListener) getActivity()).onNeutral(args, DialogType.URL_INFO_DIALOG);
                    }
                })
                .create();
    }
}
