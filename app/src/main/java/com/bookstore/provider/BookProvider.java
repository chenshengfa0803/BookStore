package com.bookstore.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2016/3/7.
 */
public class BookProvider extends ContentProvider {
    public static final String AUTHORITY = "com.bookstore.provider.BookProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private static final int URI_MATCHER_CODE_BOOKINFO = 0x1000;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, "BookInfo", URI_MATCHER_CODE_BOOKINFO);
    }

    private BookSQLiteOpenHelper dbHelper;
    private SQLiteDatabase mDatabase;

    public synchronized SQLiteDatabase getDatabase(final Context context) {
        if (dbHelper == null) {
            dbHelper = BookSQLiteOpenHelper.getInstance(getContext());
        }
        if (mDatabase != null) {
            return mDatabase;
        }
        try {
            mDatabase = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            mDatabase = dbHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    public int findMatch(Uri uri) {
        int match = sURIMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Unknown or Invalid URI: " + uri);
        }
        return match;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri = null;
        int match = findMatch(uri);
        SQLiteDatabase database = getDatabase(getContext());

        try {
            long id = database.insert("BookInfo", null, values);
            resultUri = ContentUris.withAppendedId(uri, id);
        } catch (SQLiteException e) {
            throw e;
        }
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
