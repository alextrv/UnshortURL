package org.trv.alex.unshortenurl.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.presenter.MainPresenter;

public class URLInfoDialog extends DialogFragment {

    public static final String URL_KEY = "urlKey";
    public static final String TAG = "URLInfoDialog";

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

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.url_string)
                .setMessage(url)
                // Copies url to clipboard
                .setPositiveButton(R.string.copy_string, (dialog, which) -> MainPresenter.INSTANCE.copyUrl(url))
                // Opens url in browser or any other external app
                .setNeutralButton(R.string.open_string, (dialog, which) -> MainPresenter.INSTANCE.openUrl(url))
                // Shares url with another app
                .setNegativeButton(R.string.share_string, (dialog, which) -> MainPresenter.INSTANCE.shareUrl(url))
                .create();
    }
}
