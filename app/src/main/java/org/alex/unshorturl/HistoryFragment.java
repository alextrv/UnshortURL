package org.alex.unshorturl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private HistoryRecyclerView.ListAdapter mAdapter;

    private long mHistorySize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_urls_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    /**
     * Update {@code RecyclerView} with history URLs
     */
    public void updateUI() {

        List<HistoryURL> historyURLs = HistoryBaseLab.get(getContext()).getAllHistory();

        mHistorySize = historyURLs.size();

        if (mAdapter == null) {
            mAdapter = new HistoryRecyclerView.ListAdapter(getActivity(), historyURLs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setURLs(historyURLs);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mHistorySize == 0) {
            menu.findItem(R.id.action_clear_all_history).setEnabled(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_clear_all_history:
                new CustomDialog().show(getFragmentManager(), "Tag");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
