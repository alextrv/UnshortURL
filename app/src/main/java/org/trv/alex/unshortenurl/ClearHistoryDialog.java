package org.trv.alex.unshortenurl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class ClearHistoryDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.clear_all_history)
                .setMessage(R.string.are_you_sure_clear_history)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ButtonActionListener) getActivity()).onPositive(args, DialogType.CLEAR_HISTORY_DIALOG);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ButtonActionListener) getActivity()).onNegative(args, DialogType.CLEAR_HISTORY_DIALOG);
                    }
                })
                .create();
    }
}
