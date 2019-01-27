package org.trv.alex.unshortenurl.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.trv.alex.unshortenurl.database.HistoryDbCursorWrapper;
import org.trv.alex.unshortenurl.database.HistoryDbHelper;
import org.trv.alex.unshortenurl.database.HistoryDbSchema.HistoryTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class for work with history table from database. Allows to get, add, update,
 * delete items from database. Implements as Singleton, that's why you should call
 * {@code get} method first and then methods for manipulating with database.
 */
public class HistoryBaseLab {

    private final SQLiteDatabase mDatabase;

    private static HistoryBaseLab sHistoryBaseLab;

    /**
     * Private constructor. Gets database.
     *
     * @param context the Context.
     */
    private HistoryBaseLab(Context context) {
        mDatabase = new HistoryDbHelper(context).getWritableDatabase();
    }

    /**
     * Gets instance of this class. This method must calls first and then
     * other methods on instance which this method returns.
     *
     * @param context the Context.
     * @return instance of HistoryBaseLab.
     */
    public static HistoryBaseLab get(Context context) {
        if (sHistoryBaseLab == null) {
            sHistoryBaseLab = new HistoryBaseLab(context.getApplicationContext());
        }
        return sHistoryBaseLab;
    }

    /**
     * Helper that packs {@code HistoryURL} into {@code ContentValues}.
     *
     * @param historyURL the HistoryURL.
     * @return the ContentValues.
     */
    private ContentValues getContentValues(HistoryURL historyURL) {
        ContentValues contentValues = new ContentValues();
        if (historyURL.getId() > 0) {
            contentValues.put(HistoryTable.Cols.ID, historyURL.getId());
        }
        contentValues.put(HistoryTable.Cols.URL, historyURL.getUrl());
        contentValues.put(HistoryTable.Cols.PARENT_ID, historyURL.getParentId());

        return contentValues;
    }

    /**
     * Helper method that does query to database.
     *
     * @param whereClause the Where Clause.
     * @param whereArgs   the Where Args.
     * @param orderBy     the orderBy.
     * @param tableName   the table name.
     * @param limitClause the Limit Clause.
     * @return CursorWrapper for history table.
     */
    private HistoryDbCursorWrapper queryDb(String whereClause, String[] whereArgs,
                                           String orderBy, String tableName, String limitClause) {
        Cursor cursor = mDatabase.query(
                tableName,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                orderBy,
                limitClause
        );

        return new HistoryDbCursorWrapper(cursor);
    }

    /**
     * Gets history of URLs which user typed. Returns only URLs with
     * parent_id == 0 and order by DESC.
     *
     * @param offset the number of rows that will be skipped from the start.
     * @param count  number of items that less or equals {@code count}. If count == 0
     *               gets all records.
     * @return the list of URLs.
     */
    public List<HistoryURL> getHistoryWithNoParentId(long offset, long count) {

        if (offset < 0 || count < 0) {
            throw new IllegalArgumentException(String.format(Locale.getDefault(),
                    "offset and count must be >= 0. Actual offset: %d, count: %d", offset, count));
        }

        List<HistoryURL> historyList = new ArrayList<>();

        String orderBy = HistoryTable.Cols.ID + " DESC";

        String whereClause = HistoryTable.Cols.PARENT_ID + " = ?";
        String[] whereArgs = new String[]{"0"};

        String limitClause;
        if (count > 0) {
            limitClause = offset + "," + count;
        } else {
            limitClause = offset + "," + Long.MAX_VALUE;
        }

        try (HistoryDbCursorWrapper cursorWrapper = queryDb(
                whereClause, whereArgs, orderBy, HistoryTable.NAME, limitClause)) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                HistoryURL historyURL = cursorWrapper.getHistoryURL();
                historyList.add(historyURL);
                cursorWrapper.moveToNext();
            }
        }

        return historyList;

    }

    /**
     * Gets {@code HistoryURL} item from database according to {@code id} params.
     *
     * @param id the row id in database.
     * @return {@code HistoryURL} item from database or {@code null} if not found.
     */
    public HistoryURL getItemById(long id) {

        try (HistoryDbCursorWrapper cursorWrapper = queryDb(
                HistoryTable.Cols.ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                HistoryTable.NAME,
                null
        )) {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getHistoryURL();
        }

    }

    /**
     * Gets {@code HistoryURL} item from database according to {@code parentId} params.
     *
     * @param parentId the parent_id in database.
     * @return {@code HistoryURL} item from database or {@code null} if not found.
     */
    public HistoryURL getItemByParentId(long parentId) {

        try (HistoryDbCursorWrapper cursorWrapper = queryDb(
                HistoryTable.Cols.PARENT_ID + " = ?",
                new String[]{String.valueOf(parentId)},
                null,
                HistoryTable.NAME,
                null
        )) {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getHistoryURL();
        }

    }

    /**
     * Gets all of inheritors based on {@code parentId}.
     *
     * @param parentId Id of parent.
     * @return list of all inheritors.
     */
    public List<HistoryURL> getAllInheritors(long parentId) {
        List<HistoryURL> list = new ArrayList<>();
        HistoryURL historyURL = getItemByParentId(parentId);
        while (historyURL != null) {
            list.add(historyURL);
            historyURL = getItemByParentId(historyURL.getId());
        }
        return list;
    }

    /**
     * Inserts into database @{code historyURL} from params.
     *
     * @param historyURL inserts into DB.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addItem(HistoryURL historyURL) {
        ContentValues contentValues = getContentValues(historyURL);
        return mDatabase.insert(HistoryTable.NAME, null, contentValues);
    }

    /**
     * Updates record in database. All data gets from {@code historyURL}
     * and overwrite record in database where _id == {@code historyURL.getId()}.
     * According to that it's better to get record via {@code getItemById()}
     * method, create new instance with same _id and edited data, and then
     * put as a parameter to {@code updateItem()}.
     *
     * @param historyURL edited record
     */
    public void updateItem(HistoryURL historyURL) {
        ContentValues contentValues = getContentValues(historyURL);

        mDatabase.update(
                HistoryTable.NAME,
                contentValues,
                HistoryTable.Cols.ID + " = ?",
                new String[]{String.valueOf(historyURL.getId())}
        );
    }

    /**
     * Deletes record in database with the following id.
     *
     * @param id of record which needs to be deleted in database.
     */
    public void deleteItem(long id) {
        mDatabase.delete(
                HistoryTable.NAME,
                HistoryTable.Cols.ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Deletes {@code parentId} and all of its inheritors.
     *
     * @param parentId id of record that needs to be deleted in database.
     */
    public void deleteItemAndAllInheritors(long parentId) {
        List<HistoryURL> historyURLS = getAllInheritors(parentId);
        deleteItem(parentId);
        for (HistoryURL item : historyURLS) {
            deleteItem(item.getId());
        }
    }

    /**
     * Deletes all records in database.
     */
    public void deleteAllItems() {
        mDatabase.delete(
                HistoryTable.NAME,
                null,
                null
        );
    }
}
