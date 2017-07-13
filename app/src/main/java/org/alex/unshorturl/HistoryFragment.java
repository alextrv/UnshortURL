package org.alex.unshorturl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private HistoryRecyclerView.ListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_urls_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateURLsUI();

        return view;
    }

    /**
     * Update {@code RecyclerView} with history URLs
     */
    private void updateURLsUI() {

        List<HistoryURL> historyURLs = HistoryBaseLab.get(getContext()).getAllHistory();

        if (mAdapter == null) {
            mAdapter = new HistoryRecyclerView.ListAdapter(getActivity(), historyURLs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setURLs(historyURLs);
            mAdapter.notifyDataSetChanged();
        }

    }
}
