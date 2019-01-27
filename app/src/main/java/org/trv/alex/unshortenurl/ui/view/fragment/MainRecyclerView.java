package org.trv.alex.unshortenurl.ui.view.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.trv.alex.unshortenurl.R;
import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.presenter.MainPresenter;

import java.util.List;

/**
 * Class implements {@code ViewHolder} and {@code Adapter} for {@code RecyclerView} on main screen
 */
class MainRecyclerView {

    public static class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mURL;
        private TextView mURLTextView;

        /**
         * Constructor which initializes views on each row of RecyclerView
         *
         * @param itemView row layout
         */
        public ListHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mURLTextView = itemView.findViewById(R.id.long_url);

        }

        /**
         * Bind URL from adapter to view
         *
         * @param url the URL
         */
        public void bindValue(String url) {
            mURL = url;
            mURLTextView.setText(url);
        }

        @Override
        public void onClick(View v) {
            MainPresenter.INSTANCE.urlSelected(mURL);
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
            mURLs = urls;
        }

        @NonNull
        @Override
        public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View view = layoutInflater.inflate(R.layout.urls_list_item, parent, false);
            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListHolder holder, int position) {
            String url = mURLs.get(position).getUrl();
            holder.bindValue(url);
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
