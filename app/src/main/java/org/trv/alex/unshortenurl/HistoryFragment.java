package org.trv.alex.unshortenurl;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements MainActivity.Callback {

    @Override
    public void runCallBack() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    private RecyclerView mRecyclerView;
    private HistoryRecyclerView.ListAdapter mAdapter;

    private Button mClearHistoryButton;

    private List<HistoryURL> mTempHistoryURLList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mRecyclerView = view.findViewById(R.id.history_urls_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mClearHistoryButton = view.findViewById(R.id.clear_all_history);

        mClearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClearHistoryDialog().show(getFragmentManager(), "Tag");
            }
        });

        ItemTouchHelper itemTouchHelper =
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                HistoryURL historyURL = HistoryBaseLab.get(getActivity())
                        .getHistory(position + 1)
                        .get(position);
                mTempHistoryURLList = new ArrayList<>();
                mTempHistoryURLList.add(historyURL);
                mTempHistoryURLList.addAll(
                        HistoryBaseLab.get(getActivity()).getAllInheritors(historyURL.getId()));
                HistoryBaseLab.get(getActivity()).deleteItemAndAllInheritors(historyURL.getId());
                updateUI();
                Snackbar.make(getView(), R.string.item_deleted, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (HistoryURL item : mTempHistoryURLList) {
                                    HistoryBaseLab.get(getActivity()).addItem(item);
                                }
                                updateUI();
                            }
                }).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        updateUI();

        return view;
    }

    /**
     * Update {@code RecyclerView} with history URLs
     */
    public void updateUI() {

        List<HistoryURL> historyURLs = HistoryBaseLab.get(getActivity()).getHistory(0);

        if (mAdapter == null) {
            mAdapter = new HistoryRecyclerView.ListAdapter(getActivity(), historyURLs);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setURLs(historyURLs);
            mAdapter.notifyDataSetChanged();
        }

        mClearHistoryButton.setEnabled(historyURLs.size() > 0);

    }

}
