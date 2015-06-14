package com.spruk.lecpad.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by taray on 6/14/2015.
 */
public class LibraryProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LibraryDbHelper dbHelper;

    static final int LIBRARY = 100;

    static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LibraryContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,LibraryContract.PATH_LIBRARY,LIBRARY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new LibraryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri))
        {
            case LIBRARY:
                retCursor = dbHelper.getReadableDatabase().query(LibraryContract.LibraryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch  (match)
        {
            case LIBRARY:
                return LibraryContract.LibraryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI : " + uri);

        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match)
        {
            case LIBRARY: {
                long _id = db.insert(LibraryContract.LibraryEntry.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri = LibraryContract.LibraryEntry.buildItineraryUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch(match)
        {
            case LIBRARY:
                rowsDeleted = db.delete(LibraryContract.LibraryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch(match)
        {
            case LIBRARY:
                rowsUpdated = db.update(LibraryContract.LibraryEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsUpdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
}
