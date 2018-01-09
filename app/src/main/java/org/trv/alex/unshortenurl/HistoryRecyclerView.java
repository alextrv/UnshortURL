package org.trv.alex.unshortenurl;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implements {@code ViewHolder} and {@code Adapter} for {@code RecyclerView} on history screen
 */
public class HistoryRecyclerView {

    public static class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mShortURL;
        private TextView mShortURLTextView;
        private LinearLayout mLinearLayout;

        private Context mContext;

        /**
         * Constructor which initializes views on each row of RecyclerView
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
         * @param shortURL the short URL
         * @param inheritors the list of inheritors
         * @param context the Context
         */
        public void bindValue(HistoryURL shortURL,
                              List<HistoryURL> inheritors, Context context) {
            mShortURL = shortURL.getUrl();
            mShortURLTextView.setText(mShortURL);
            mContext = context;

            mLinearLayout.removeViews(1, mLinearLayout.getChildCount() - 1);

            List<HistoryURL> list = new ArrayList<>();
            list.addAll(inheritors);

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
                FragmentManager fm = ((Activity) mContext).getFragmentManager();
                URLInfoDialog.newInstance(url).show(fm, "tag");
            }
        }
    }

    public static class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private Context mContext;
        private List<HistoryURL> mURLs;

        /**
         * Constructor which initializes data for {@code RecyclerView}
         * @param context the Context
         * @param urls the URLs list
         */
        public ListAdapter(Context context, List<HistoryURL> urls) {
            mContext = context;
            mURLs = urls;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);

            View view = layoutInflater.inflate(R.layout.history_urls_list_item, parent, false);

            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            HistoryURL shortUrl = mURLs.get(position);
            List<HistoryURL> inheritors = HistoryBaseLab.get(mContext)
                    .getAllInheritors(shortUrl.getId());
            holder.bindValue(shortUrl, inheritors, mContext);
        }

        @Override
        public int getItemCount() {
            return mURLs.size();
        }

        public void setURLs(List<HistoryURL> urls) {
            mURLs = urls;
        }
    }

}
