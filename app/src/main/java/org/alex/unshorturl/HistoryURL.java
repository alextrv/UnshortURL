package org.alex.unshorturl;

/**
 * Represents data from database from table "history". Instances of
 * this class are immutable.
 */
public class HistoryURL {

    private final long mId;
    private final String mUrl;
    private final long mParentId;

    public HistoryURL(long id, String url, long parentId) {
        mId = id;
        mUrl = url;
        mParentId = parentId;
    }

    public long getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getParentId() {
        return mParentId;
    }
}
