package org.trv.alex.unshorturl;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class CustomDialog extends DialogFragment {

    public interface ButtonActionListener {
        void onPositive();

        void onNegative();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.clear_all_history)
                .setMessage(R.string.are_you_sure_clear_history)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ButtonActionListener) getActivity()).onPositive();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ButtonActionListener) getActivity()).onNegative();
                    }
                })
                .create();
    }
}
