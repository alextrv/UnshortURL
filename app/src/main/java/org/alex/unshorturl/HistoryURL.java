package org.alex.unshorturl;

/**
 * Represents data from database from table "history". Instances of
 * this class are immutable.
 */
public class HistoryURL {

    private final int mId;
    private final String mUrl;
    private final int mParentId;

    public HistoryURL(int id, String url, int parentId) {
        mId = id;
        mUrl = url;
        mParentId = parentId;
    }

    public int getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getParentId() {
        return mParentId;
    }
}
