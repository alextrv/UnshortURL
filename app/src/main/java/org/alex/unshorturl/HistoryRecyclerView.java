package org.alex.unshorturl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Class implements {@code ViewHolder} and {@code Adapter} for {@code RecyclerView} on history screen
 */
public class HistoryRecyclerView {

    public static class ListHolder extends RecyclerView.ViewHolder {

        private String mShortURL;
        private String mLongURL;
        private TextView mShortURLTextView;
        private TextView mLongURLTextView;

        private Context mContext;

        /**
         * Constructor which initializes views on each row of RecyclerView
         * @param itemView row layout
         */
        public ListHolder(View itemView) {
            super(itemView);

            mShortURLTextView = (TextView) itemView.findViewById(R.id.short_history_url);
            mLongURLTextView = (TextView) itemView.findViewById(R.id.long_history_url);

        }

        /**
         * Bind URL from adapter to view
         * @param shortURL the short URL
         * @param longURL the long URL
         * @param context the Context
         */
        public void bindValue(String shortURL, String longURL, Context context) {
            mShortURL = shortURL;
            mLongURL = longURL;
            mShortURLTextView.setText(shortURL);
            mLongURLTextView.setText(longURL);
            mContext = context;
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
            HistoryURL longURL = HistoryBaseLab.get(mContext).getChildItem(shortUrl.getId());
            holder.bindValue(shortUrl.getUrl(), longURL.getUrl(), mContext);
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
