package org.alex.unshorturl.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import org.alex.unshorturl.HistoryURL;
import org.alex.unshorturl.database.HistoryDbSchema.HistoryTable;

public class HistoryDbCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HistoryDbCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Gets data from cursor and wraps into {@code HistoryURL}.
     * @return the {@code HistoryURL}.
     */
    public HistoryURL getHistoryURL() {
        long id = getLong(getColumnIndex(HistoryTable.Cols.ID));
        String url = getString(getColumnIndex(HistoryTable.Cols.URL));
        long parentID = getLong(getColumnIndex(HistoryTable.Cols.PARENT_ID));

        return new HistoryURL(id, url, parentID);
    }

}
