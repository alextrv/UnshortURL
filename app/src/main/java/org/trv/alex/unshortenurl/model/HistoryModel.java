package org.trv.alex.unshortenurl.model;

import android.content.Context;
import android.support.annotation.NonNull;

import org.trv.alex.unshortenurl.util.ResolveShortURL;

import java.util.List;

public class HistoryModel implements HistoryModelContract {

    private static HistoryModelContract INSTANCE;

    private HistoryBaseLab mBaseLab;

    private HistoryModel(Context context) {
        mBaseLab = HistoryBaseLab.get(context);
    }

    public static HistoryModelContract get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new HistoryModel(context.getApplicationContext());
        }
        return INSTANCE;
    }

    @Override
    public List<HistoryURL> getAllInheritors(long id) {
        return mBaseLab.getAllInheritors(id);
    }

    @NonNull
    @Override
    public List<HistoryURL> getItems(long offset, long count) {
        return mBaseLab.getHistoryWithNoParentId(offset, count);
    }

    @Override
    public HistoryURL getItemByParentId(long parentId) {
        return mBaseLab.getItemByParentId(parentId);
    }

    @Override
    public long addItem(HistoryURL item) {
        return mBaseLab.addItem(item);
    }

    @Override
    public void addItems(List<HistoryURL> items) {
        for (HistoryURL item : items) {
            mBaseLab.addItem(item);
        }
    }

    @Override
    public void clearAllItems() {
        mBaseLab.deleteAllItems();
    }

    @Override
    public void deleteItem(long id) {
        mBaseLab.deleteItem(id);
    }

    /**
     * Tries to get long URL from {@code url}. If {@code deep} is {@code true} then
     * gets long URL until it becomes not short and if success returns list that may have
     * more than one elements, else if {@code false} then gets only one level
     * and if success returned list will have only one element.
     *
     * @param url  the URL
     * @param deep if {@code true} then get deep URL, else not.
     * @return list of long URLs or empty list if url already is long version
     * {@code null} if something went wrong.
     */
    public List<HistoryURL> getLongURL(String url, boolean deep) {
        List<HistoryURL> urls = ResolveShortURL.resolveURL(url, deep);
        if (urls != null && !urls.isEmpty()) {
            long id = addItem(new HistoryURL(0, url, 0));
            for (HistoryURL longUrl : urls) {
                id = addItem(new HistoryURL(0, longUrl.getUrl(), id));
            }
        }
        return urls;
    }

}
