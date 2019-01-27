package org.trv.alex.unshortenurl.ui.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.presenter.MainPresenter;
import org.trv.alex.unshortenurl.ui.view.MainViewContract;
import org.trv.alex.unshortenurl.util.Utils;

import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment implements MainViewContract {

    public static final int TAB_POSITION = 0;

    private TextInputEditText mURLEditText;
    private Button mGetLongURLButton;
    private Button mGetDeepLongURLButton;
    private ImageButton mClearPasteFromClipboardButton;
    private View mFragmentView;

    private RecyclerView mRecyclerView;
    private MainRecyclerView.ListAdapter mAdapter;

    private ProgressBar mProgressBar;

    private boolean mTaskRunning;
    private List<HistoryURL> mLastActionItems = Collections.emptyList();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        mURLEditText = mFragmentView.findViewById(R.id.short_url_edit_text);
        mGetLongURLButton = mFragmentView.findViewById(R.id.get_long_url_button);
        mGetDeepLongURLButton = mFragmentView.findViewById(R.id.get_deep_long_url_button);
        mClearPasteFromClipboardButton = mFragmentView.findViewById(R.id.clear_paste_url_button);

        mProgressBar = mFragmentView.findViewById(R.id.update_data_progress);
        mProgressBar.setVisibility(mTaskRunning ? View.VISIBLE : View.GONE);

        mRecyclerView = mFragmentView.findViewById(R.id.urls_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mURLEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                MainPresenter.INSTANCE.resolveURL(mURLEditText.getText(), false);
                return true;
            }
            return false;
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

        mGetLongURLButton.setOnClickListener(v ->
                MainPresenter.INSTANCE.resolveURL(mURLEditText.getText(), false));

        mGetDeepLongURLButton.setOnClickListener(v ->
                MainPresenter.INSTANCE.resolveURL(mURLEditText.getText(), true));

        mClearPasteFromClipboardButton.setOnClickListener(v -> {
            boolean isEmpty = TextUtils.isEmpty(mURLEditText.getText());
            if (isEmpty) {
                mURLEditText.setText(Utils.getTextFromClipboard(getActivity()));
            } else {
                mURLEditText.getText().clear();
                screenKeyboardVisibility(mURLEditText, true);
            }
        });

        return mFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainPresenter.INSTANCE.addView(TAB_POSITION, this);
        MainPresenter.INSTANCE.newIntent(getActivity().getIntent());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (!mLastActionItems.isEmpty()) {
            updateListUI(mLastActionItems, -1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        MainPresenter.INSTANCE.removeView(TAB_POSITION);
    }

    /**
     * Shows or hides screen keyboard.
     *
     * @param editText for which hide keyboard
     * @param show     show or hide screen keyboard depending on value.
     *                 {@code true} - show, {@code false} - hide.
     */
    private void screenKeyboardVisibility(EditText editText, boolean show) {
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView instanceof EditText) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (show) {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void updateListUI(List<HistoryURL> list, int insertPosition) {
        mLastActionItems = list;
        if (mAdapter == null) {
            mAdapter = new MainRecyclerView.ListAdapter(getActivity(), list);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setURLs(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteListItem(int position) {
    }

    @Override
    public void showProgress() {
        screenKeyboardVisibility(mURLEditText, false);
        mTaskRunning = true;
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgress() {
        mTaskRunning = false;
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int messageId) {
        Snackbar.make(mFragmentView, messageId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageWithAction(int messageId, View.OnClickListener listener) {
    }

    @Override
    public void setNewURL(String url) {
        mURLEditText.setText(url);
    }
}
