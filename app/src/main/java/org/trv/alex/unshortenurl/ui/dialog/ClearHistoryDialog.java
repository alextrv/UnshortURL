package org.trv.alex.unshortenurl.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.presenter.MainPresenter;

public class ClearHistoryDialog extends DialogFragment {

    public static final String TAG = "ClearHistoryDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.clear_all_history_string)
                .setMessage(R.string.sure_clear_history_string)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) ->
                                MainPresenter.INSTANCE.clearAllHistory())
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
