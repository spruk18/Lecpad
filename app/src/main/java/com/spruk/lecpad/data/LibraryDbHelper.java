package com.spruk.lecpad.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by taray on 6/14/2015.
 */
public class LibraryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION =1;
    static final String DATABASE_NAME = "library.db";

    public LibraryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LIBRARY_TABLE = "CREATE TABLE " + LibraryContract.LibraryEntry.TABLE_NAME + " (" +
                LibraryContract.LibraryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LibraryContract.LibraryEntry.COLUMN_FILENAME + " TEXT NOT NULL, " +
                LibraryContract.LibraryEntry.COLUMN_FILE_PATH + " TEXT NOT NULL, " +
                LibraryContract.LibraryEntry.COLUMN_ICON_TYLE + " TEXT NOT NULL, " +
                LibraryContract.LibraryEntry.COLUMN_USER + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_LIBRARY_TABLE);


    }

    @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + LibraryContract.LibraryEntry.TABLE_NAME);

    }
}
