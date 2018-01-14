package org.trv.alex.unshortenurl;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private TextInputEditText mURLEditText;
    private Button mGetLongURLButton;
    private Button mGetDeepLongURLButton;
    private ImageButton mClearPasteFromClipboardButton;

    private RecyclerView mRecyclerView;
    private MainRecyclerView.ListAdapter mAdapter;

    private List<String> mURLs;

    private static final String SAVED_LIST = "savedList";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mURLEditText = view.findViewById(R.id.short_url_edit_text);
        mGetLongURLButton = view.findViewById(R.id.get_long_url_button);
        mGetDeepLongURLButton = view.findViewById(R.id.get_deep_long_url_button);

        mRecyclerView = view.findViewById(R.id.urls_list_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);

        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mClearPasteFromClipboardButton = view.findViewById(R.id.clear_paste_url_button);

        mURLEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    new AsyncURL().execute(false);
                    return true;
                }
                return false;
            }
        });

        mURLEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mClearPasteFromClipboardButton
                            .setImageResource(R.drawable.ic_content_paste_black_24dp);
                } else {
                    mClearPasteFromClipboardButton
                            .setImageResource(R.drawable.ic_clear_black_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mURLEditText.setText(getURLFromIntent(getActivity().getIntent()));

        mGetLongURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncURL().execute(false);
            }
        });

        mGetDeepLongURLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncURL().execute(true);
            }
        });

        mClearPasteFromClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEmpty = mURLEditText.getText().toString().isEmpty();
                if (isEmpty) {
                    String clipboard = getTextFromClipboard();
                    if (clipboard != null) {
                        mURLEditText.setText(clipboard);
                    }
                } else {
                    mURLEditText.getText().clear();
                    screenKeyboardVisibility(mURLEditText, true);
                }
            }
        });

        if (savedInstanceState != null) {
            mURLs = savedInstanceState.getStringArrayList(SAVED_LIST);
        }

        updateURLsUI(mURLs);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SAVED_LIST, (ArrayList<String>) mURLs);
    }

    /**
     * Set {@code RecyclerView} list from {@code urls} and update it.
     * @param   urls list of resolved (long) URLs.
     */
    private void updateURLsUI(List<String> urls) {

        if (urls == null) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new MainRecyclerView.ListAdapter(getActivity(), urls);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setURLs(urls);
            mAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Try to get long URL from {@code EditText}. If {@code getDeepURL} is {@code true} then
     * get long URL until it becomes not short and if success return list that may have
     * more than one elements, else if {@code false} then get only one level
     * and if success return list will have one element.
     * @param   getDeepURL if {@code true} then get deep URL, else not.
     * @return  list of long URLs or empty list if url already is long version
     *          {@code null} if something went wrong.
     */
    private List<String> actionGetLongURL(final boolean getDeepURL, String url) {
        List<String> urls;
        if (getDeepURL) {
            urls = ResolveShortURL.getDeepLongURL(url);
        } else {
            urls = ResolveShortURL.getOneLevelLongURL(url);
        }
        if (urls != null && urls.size() > 0) {
            long parentId = HistoryBaseLab.get(getActivity()).addItem(new HistoryURL(0, url, 0));
            for (String longUrl : urls) {
                parentId = HistoryBaseLab.get(getActivity()).addItem(new HistoryURL(0, longUrl, parentId));
            }
        }
        return urls;
    }

    /**
     * Inner AsyncTask class for updating {@code RecyclerView} in background.
     */
    private class AsyncURL extends AsyncTask<Boolean, Void, List<String>> {

        private String url;
        private int messageId;

        @Override
        protected void onPreExecute() {
            url = mURLEditText.getText().toString();
            screenKeyboardVisibility(mURLEditText, false);
        }

        @Override
        protected List<String> doInBackground(Boolean... params) {
            if (ResolveShortURL.isOnline()) {
                messageId = R.string.not_short_url_string;
                return actionGetLongURL(params[0], url);
            } else {
                messageId = R.string.no_inet_connection_string;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if (getActivity() != null && getView() != null) {
                if (result == null || result.size() == 0) {
                    Snackbar.make(getView(), messageId, Snackbar.LENGTH_SHORT).show();
                }
                ((MainActivity) getActivity()).getViewPager().getAdapter().notifyDataSetChanged();
                mURLs = result;
                updateURLsUI(result);
            }
        }
    }

    /**
     * Show or hide screen keyboard.
     * @param editText for which hide keyboard
     * @param show show or hide screen keyboard depending on value.
     *             {@code true} - show, {@code false} - hide.
     */
    private void screenKeyboardVisibility(EditText editText, boolean show) {
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null && focusedView instanceof EditText) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (show) {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

    /**
     * Get text from intent as StringExtra or as Data. If intent doesn't have any
     * text then return {@code null}.
     * @param   intent the intent.
     * @return  text from intent or {@code null} if empty.
     */
    private String getURLFromIntent(Intent intent) {
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

    /**
     * Get text from clipboard and return value. If clipboard is empty return {@code null}.
     * @return clipboard value.
     */
    private String getTextFromClipboard() {
        ClipboardManager clipboardManager =
                (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData primaryClip = clipboardManager.getPrimaryClip();
        if (primaryClip != null && primaryClip.getItemCount() > 0) {
            return primaryClip.getItemAt(0).getText().toString();
        }
        return null;
    }

}
