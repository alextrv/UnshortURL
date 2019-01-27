package org.trv.alex.unshortenurl.presenter;

import android.content.Intent;

import org.trv.alex.unshortenurl.model.HistoryURL;
import org.trv.alex.unshortenurl.ui.view.MainViewContract;
import org.trv.alex.unshortenurl.ui.view.activity.MainActivity;

import java.util.List;

public interface MainPresenterContract {

    interface ResultListener {
        void onResultReceived(List<HistoryURL> result);
    }

    void resolveURL(CharSequence url, boolean deep);

    void attachActivity(MainActivity activity);

    void detachActivity();

    void addView(int position, MainViewContract view);

    void removeView(int position);

    void getHistory(boolean loadMore);

    void clearAllHistory();

    void getAllInheritors(long id, ResultListener listener);

    void deleteItem(HistoryURL historyURL, final int viewPosition);

    void undoDeleteItem();

    void urlSelected(String url);

    void copyUrl(String url);

    void shareUrl(String url);

    void openUrl(String url);

    void newIntent(Intent intent);
}
