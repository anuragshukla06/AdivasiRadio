package com.microsoft.melange.gondispeak;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDB extends SQLiteOpenHelper {

    public static final String DATABSE_NAME = "history.db";
    public static final int DATABSE_VERSION = 1;
    public static final String TABLE_NAME = "USERTEXT";
    private static final String TEXT_TYPE = "TEXT";
    private static final String INTEGER_TYPE = "INTEGER";

    interface COLUMNS_NAME{
        String _ID = "id";
        String TEXT = "usertext";
        String RATING = "rating";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMNS_NAME._ID + " INTEGER PRIMARY KEY," +
                    COLUMNS_NAME.TEXT + " " + TEXT_TYPE + ", " +
                    COLUMNS_NAME.RATING + " " + INTEGER_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public HistoryDB(Context context) {
        super(context, DATABSE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
