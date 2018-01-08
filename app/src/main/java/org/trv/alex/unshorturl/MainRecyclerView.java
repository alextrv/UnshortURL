package org.trv.alex.unshorturl;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Class implements {@code ViewHolder} and {@code Adapter} for {@code RecyclerView} on main screen
 */
public class MainRecyclerView {

    public static class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mURL;
        private TextView mURLTextView;

        private Context mContext;

        /**
         * Constructor which initializes views on each row of RecyclerView
         * @param itemView row layout
         */
        public ListHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mURLTextView = itemView.findViewById(R.id.long_url);

        }

        /**
         * Bind URL from adapter to view
         * @param url the URL
         * @param context the Context
         */
        public void bindValue(String url, Context context) {
            mURL = url;
            mURLTextView.setText(url);
            mContext = context;
        }

        @Override
        public void onClick(View v) {
            FragmentManager fm = ((Activity) mContext).getFragmentManager();
            URLInfoDialog.newInstance(mURL).show(fm, "tag");
        }
    }

    public static class ListAdapter extends RecyclerView.Adapter<ListHolder> {

        private Context mContext;
        private List<String> mURLs;

        /**
         * Constructor which initializes data for {@code RecyclerView}
         * @param context the Context
         * @param urls the URLs list
         */
        public ListAdapter(Context context, List<String> urls) {
            mContext = context;
            mURLs = urls;
        }

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);

            View view = layoutInflater.inflate(R.layout.urls_list_item, parent, false);

            return new ListHolder(view);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            String url = mURLs.get(position);
            holder.bindValue(url, mContext);
        }

        @Override
        public int getItemCount() {
            return mURLs.size();
        }

        public void setURLs(List<String> urls) {
            mURLs = urls;
        }
    }

}
