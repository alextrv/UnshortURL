package org.trv.alex.unshortenurl.model;

import java.util.List;

public interface HistoryModelContract {
    List<HistoryURL> getAllInheritors(long id);

    List<HistoryURL> getItems(long offset, long count);

    HistoryURL getItemByParentId(long parentId);

    long addItem(HistoryURL item);

    void addItems(List<HistoryURL> items);

    void clearAllItems();

    void deleteItem(long id);

    List<HistoryURL> getLongURL(String url, boolean deep);
}
