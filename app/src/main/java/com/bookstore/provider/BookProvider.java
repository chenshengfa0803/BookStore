package com.bookstore.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/3/7.
 */
public class BookProvider extends ContentProvider {
    public static final String AUTHORITY = "com.bookstore.provider.BookProvider";
    public static final Uri BOOKINFO_URI = Uri.parse("content://" + AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
    public static final Uri BOOKCATEGORY_URI = Uri.parse("content://" + AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKCATEGORY_TABLE_NAME);
    private static final int URI_MATCHER_CODE_BOOKINFO = 0x1000;
    private static final int URI_MATCHER_CODE_BOOKINFO_ID = URI_MATCHER_CODE_BOOKINFO + 1;

    private static final int URI_MATCHER_CODE_BOOKCATEGORY = URI_MATCHER_CODE_BOOKINFO + 0x1000;
    private static final int URI_MATCHER_CODE_BOOKCATEGORY_ID = URI_MATCHER_CODE_BOOKCATEGORY + 1;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, "BookInfo", URI_MATCHER_CODE_BOOKINFO);
        sURIMatcher.addURI(AUTHORITY, "BookInfo/#", URI_MATCHER_CODE_BOOKINFO_ID);
        sURIMatcher.addURI(AUTHORITY, "BookCategory", URI_MATCHER_CODE_BOOKCATEGORY);
        sURIMatcher.addURI(AUTHORITY, "BookCategory/#", URI_MATCHER_CODE_BOOKCATEGORY_ID);
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

    public synchronized int findMatch(Uri uri) {
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
        Cursor result_cursor = null;
        int match = findMatch(uri);
        SQLiteDatabase database = getDatabase(getContext());
        try {
            switch (match) {
                case URI_MATCHER_CODE_BOOKINFO:
                    String query_str1 = SQLiteQueryBuilder.buildQueryString(false, BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, projection, selection, null, null, sortOrder, null);
                    result_cursor = database.rawQuery(query_str1, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKINFO_ID:
                    String bookinfo_id = uri.getPathSegments().get(1);
                    String select = DB_Column.BookInfo.ID + "=" + bookinfo_id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    String query_str2 = SQLiteQueryBuilder.buildQueryString(false, BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, projection, select, null, null, sortOrder, null);
                    result_cursor = database.rawQuery(query_str2, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY:
                    String query_str3 = SQLiteQueryBuilder.buildQueryString(false, BookSQLiteOpenHelper.BOOKCATEGORY_TABLE_NAME, projection, selection, null, null, sortOrder, null);
                    result_cursor = database.rawQuery(query_str3, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY_ID:
                    String bookcategory_id = uri.getPathSegments().get(1);
                    String category_select = DB_Column.BookCategory.ID + "=" + bookcategory_id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    String query_str4 = SQLiteQueryBuilder.buildQueryString(false, BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, projection, category_select, null, null, sortOrder, null);
                    result_cursor = database.rawQuery(query_str4, selectionArgs);
                    break;
            }
        } catch (SQLiteException e) {
            throw e;
        }
        result_cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return result_cursor;
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
            switch (match) {
                case URI_MATCHER_CODE_BOOKINFO:
                    long id = database.insert("BookInfo", null, values);
                    resultUri = ContentUris.withAppendedId(uri, id);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY:
                    long category_id = database.insert("BookCategory", null, values);
                    resultUri = ContentUris.withAppendedId(uri, category_id);
                    getContext().getContentResolver().notifyChange(resultUri, null);
                    break;
            }
        } catch (SQLiteException e) {
            throw e;
        }
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int result_count = -1;
        int match = findMatch(uri);
        SQLiteDatabase database = getDatabase(getContext());

        try {
            switch (match) {
                case URI_MATCHER_CODE_BOOKINFO:
                    result_count = database.delete(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, selection, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKINFO_ID:
                    String id = uri.getPathSegments().get(1);
                    String select = DB_Column.BookInfo.ID + "=" + id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    result_count = database.delete(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, select, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY:
                    result_count = database.delete(BookSQLiteOpenHelper.BOOKCATEGORY_TABLE_NAME, selection, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY_ID:
                    String category_id = uri.getPathSegments().get(1);
                    String category_select = DB_Column.BookCategory.ID + "=" + category_id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    result_count = database.delete(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, category_select, selectionArgs);
                    break;
            }
        } catch (SQLiteException e) {
            result_count = -1;
            throw e;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result_count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int result_count = -1;
        int match = findMatch(uri);
        SQLiteDatabase database = getDatabase(getContext());

        try {
            switch (match) {
                case URI_MATCHER_CODE_BOOKINFO:
                    result_count = database.update(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, values, selection, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKINFO_ID:
                    String id = uri.getPathSegments().get(1);
                    String select = DB_Column.BookInfo.ID + "=" + id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    result_count = database.update(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, values, select, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY:
                    result_count = database.update(BookSQLiteOpenHelper.BOOKCATEGORY_TABLE_NAME, values, selection, selectionArgs);
                    break;
                case URI_MATCHER_CODE_BOOKCATEGORY_ID:
                    String category_id = uri.getPathSegments().get(1);
                    String category_select = DB_Column.BookCategory.ID + "=" + category_id + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                    result_count = database.update(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME, values, category_select, selectionArgs);
                    break;
            }
        } catch (SQLiteException e) {
            result_count = -1;
            throw e;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return result_count;
    }
}
