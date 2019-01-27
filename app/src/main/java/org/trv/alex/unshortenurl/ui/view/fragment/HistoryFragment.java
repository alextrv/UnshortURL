package org.trv.alex.unshortenurl.ui.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.presenter.MainPresenter;
import org.trv.alex.unshortenurl.ui.dialog.ClearHistoryDialog;
import org.trv.alex.unshortenurl.ui.view.MainViewContract;
import org.trv.alex.unshortenurl.ui.view.activity.MainActivity;

import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment implements MainActivity.TabCallback, MainViewContract {

    public static final int TAB_POSITION = 1;

    private static final int LIST_THRESHOLD = 5;

    @Override
    public void onTabReselected() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private HistoryRecyclerView.ListAdapter mAdapter;
    private View mFragmentView;
    private ProgressBar mProgressBar;
    private TextView mEmptyHistoryView;
    private Button mClearHistoryButton;

    private List<HistoryURL> mHistoryList = Collections.emptyList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_history, container, false);

        mProgressBar = mFragmentView.findViewById(R.id.update_data_progress);

        mRecyclerView = mFragmentView.findViewById(R.id.history_urls_list_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEmptyHistoryView = mFragmentView.findViewById(R.id.empty_history);

        mClearHistoryButton = mFragmentView.findViewById(R.id.clear_all_history);

        mClearHistoryButton.setOnClickListener(v ->
                new ClearHistoryDialog().show(getFragmentManager(), ClearHistoryDialog.TAG));

        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        HistoryRecyclerView.ListHolder listHolder = (HistoryRecyclerView.ListHolder) viewHolder;
                        MainPresenter.INSTANCE.deleteItem(listHolder.getItem(), listHolder.getLayoutPosition());
                    }
                });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int totalItemCount = mLayoutManager.getItemCount();
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int pastVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                    if (pastVisibleItem + visibleItemCount >= totalItemCount - LIST_THRESHOLD) {
                        MainPresenter.INSTANCE.getHistory(true);
                    }
                }
            }
        });

        return mFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainPresenter.INSTANCE.addView(TAB_POSITION, this);
        MainPresenter.INSTANCE.getHistory(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainPresenter.INSTANCE.removeView(TAB_POSITION);
    }

    /**
     * Update {@code RecyclerView} with history URLs
     */
    @Override
    public void updateListUI(List<HistoryURL> historyURLs, int insertPosition) {
        if (mAdapter == null) {
            mAdapter = new HistoryRecyclerView.ListAdapter(getActivity(), historyURLs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            if (insertPosition > 0) {
                mAdapter.insertURLs(historyURLs, insertPosition);
                mAdapter.notifyItemRangeInserted(insertPosition, historyURLs.size());
            } else {
                mAdapter.setURLs(historyURLs);
                mAdapter.notifyDataSetChanged();
            }
        }
        mHistoryList = mAdapter.getList();

        mEmptyHistoryView.setVisibility(mHistoryList.isEmpty() ? View.VISIBLE : View.GONE);
        mClearHistoryButton.setEnabled(!mHistoryList.isEmpty());
    }

    @Override
    public void deleteListItem(int position) {
        mHistoryList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(int messageId) {
        Snackbar.make(mFragmentView, messageId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageWithAction(int messageId, View.OnClickListener listener) {
        Snackbar.make(mFragmentView, messageId, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo_string, listener).show();
    }

    @Override
    public void setNewURL(String url) {
    }
}
