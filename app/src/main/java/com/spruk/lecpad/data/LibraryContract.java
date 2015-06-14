package com.spruk.lecpad.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by taray on 6/14/2015.
 */
public class LibraryContract {
    public static final String CONTENT_AUTHORITY = "com.spruk.lecpad";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIBRARY = "library";

    public static final class LibraryEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIBRARY).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIBRARY;

        public static final String TABLE_NAME = "library";

        public static final String COLUMN_FILENAME = "filename";
        public static final String COLUMN_FILE_PATH = "file_path";
        public static final String COLUMN_ICON_TYLE = "icon_type";
        public static final String COLUMN_USER = "us";

        public static Uri buildItineraryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
