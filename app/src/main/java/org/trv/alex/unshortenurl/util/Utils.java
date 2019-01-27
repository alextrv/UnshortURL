package org.trv.alex.unshortenurl.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * This class provides useful util methods.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Returns an unmodifiable view of a given list. If given list is {@code null}
     * then empty list will be returned.
     *
     * @param list the list for which is to be returned an immutable view
     * @param <T>  type of elements in the list
     * @return an immutable view of the list
     */
    @NonNull
    public static <T> List<T> unmodifiableList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (list.size() == 1) {
            return Collections.singletonList(list.get(0));
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Gets text from clipboard and returns it. If clipboard is empty, returns empty String.
     *
     * @return clipboard value or empty String if clipboard doesn't contain any value.
     */
    @NonNull
    public static String getTextFromClipboard(Context context) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClip = clipboardManager.getPrimaryClip();
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            CharSequence text = primaryClip.getItemAt(0).getText();
            if (text != null) {
                return text.toString();
            }
        }
        return "";
    }

    /**
     * Puts given text to clipboard.
     *
     * @param context the Context.
     * @param text    the text which to be put into clipboard.
     */
    public static void setClipboardText(Context context, String text) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * Gets text from intent as StringExtra or as Data. If intent doesn't have any
     * text then return {@code null}.
     *
     * @param intent the intent.
     * @return text from intent or {@code null} if empty.
     */
    public static String getURLFromIntent(@NonNull Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText == null) {
            Uri uri = intent.getData();
            if (uri != null) {
                return uri.toString();
            }
        } else {
            return sharedText;
        }
        return null;
    }
}
