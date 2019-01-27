package org.trv.alex.unshortenurl.ui.view;

import android.view.View;

import org.trv.alex.unshortenurl.model.HistoryURL;

import java.util.List;

public interface MainViewContract {
    void updateListUI(List<HistoryURL> list, int insertPosition);

    void deleteListItem(int position);

    void showProgress();

    void dismissProgress();

    void showMessage(int messageId);

    void showMessageWithAction(int messageId, View.OnClickListener listener);

    void setNewURL(String url);
}
