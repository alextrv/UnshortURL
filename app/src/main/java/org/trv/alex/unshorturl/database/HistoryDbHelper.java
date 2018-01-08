package org.trv.alex.unshorturl.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.trv.alex.unshorturl.database.HistoryDbSchema.HistoryTable;

public class HistoryDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "base.db";

    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + HistoryTable.NAME + " (" +
                HistoryTable.Cols.ID + " integer primary key autoincrement, " +
                HistoryTable.Cols.URL + ", " +
                HistoryTable.Cols.PARENT_ID + " integer )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
