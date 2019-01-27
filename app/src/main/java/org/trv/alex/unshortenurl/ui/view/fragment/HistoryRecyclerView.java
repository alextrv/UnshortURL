package org.trv.alex.unshortenurl.ui.view.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implements {@code ViewHolder} and {@code Adapter} for {@code RecyclerView} on history screen
 */
class HistoryRecyclerView {

    public static class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private HistoryURL mShortURL;
        private TextView mShortURLTextView;
        private LinearLayout mLinearLayout;
        private Context mContext;

        /**
         * Constructor which initializes views on each row of RecyclerView
         *
         * @param itemView row layout
         */
        public ListHolder(View itemView) {
            super(itemView);
            mShortURLTextView = itemView.findViewById(R.id.short_history_url);
            mLinearLayout = itemView.findViewById(R.id.layout_info);
            mLinearLayout.setOnClickListener(this);

        }

        /**
         * Bind URL from adapter to view
         *
         * @param shortURL   the short URL
         * @param inheritors the list of inheritors
         * @param context    the Context
         */
        public void bindValue(HistoryURL shortURL,
                              List<HistoryURL> inheritors, Context context) {
            mShortURL = shortURL;
            mShortURLTextView.setText(mShortURL.getUrl());
            mContext = context;

            mLinearLayout.removeViews(1, mLinearLayout.getChildCount() - 1);

            List<HistoryURL> list = new ArrayList<>(inheritors);

            // Create dynamically TextViews with URLs
            for (HistoryURL historyURL : list) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                LinearLayout layout = (LinearLayout) layoutInflater
                        .inflate(R.layout.text_view_item, null);
                TextView textView = layout.findViewById(R.id.long_url_item);
                textView.setText(historyURL.getUrl());
                mLinearLayout.addView(layout);
                layout.setOnClickListener(this);
            }

        }

        @Override
        public void onClick(View v) {
            if (v instanceof LinearLayout) {
                TextView textView = v.findViewById(R.id.short_history_url);
                if (textView == null) {
                    textView = v.findViewById(R.id.long_url_item);
                }
                String url = textView.getText().toString();
                MainPresenter.INSTANCE.urlSelected(url);
            }
        }

        public HistoryURL getItem() {
            return mShortURL;
        }
    }

    public static class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private Context mContext;
        private List<HistoryURL> mURLs;

        /**
         * Constructor which initializes data for {@code RecyclerView}
         *
         * @param context the Context
         * @param urls    the URLs list
         */
        public ListAdapter(Context context, List<HistoryURL> urls) {
            mContext = context;
            mURLs = new ArrayList<>(urls);
        }

        @NonNull
        @Override
        public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.history_urls_list_item, parent, false);
            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            HistoryURL shortUrl = mURLs.get(position);
            MainPresenter.INSTANCE.getAllInheritors(shortUrl.getId(),
                    (result) -> holder.bindValue(shortUrl, result, mContext));
        }

        @Override
        public int getItemCount() {
            return mURLs.size();
        }

        public void setURLs(List<HistoryURL> urls) {
            mURLs.clear();
            mURLs.addAll(urls);
        }

        public void insertURLs(List<HistoryURL> urls, int position) {
            mURLs.addAll(position, urls);
        }

        public List<HistoryURL> getList() {
            return mURLs;
        }
    }

}
