package com.bookstore.main;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListLoader;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.BookSQLiteOpenHelper;
import com.bookstore.provider.DB_Column;
import com.bookstore.provider.Projection;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/26.
 */
public class DBHandler {
    public static int loadCompleteTimes = 0;
    public ArrayList<LoaderItem> loaders;
    private BookListGridListViewAdapter adapter = null;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;

    public DBHandler(BookListGridListViewAdapter adapter) {
        this.adapter = adapter;
        loaders = new ArrayList<LoaderItem>();
    }

    public static void saveBookCategory(final Activity activity, final ArrayList<BookCategory.CategoryItem> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (BookCategory.CategoryItem item : list) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DB_Column.BookCategory.Name, item.category_name);
                    contentValues.put(DB_Column.BookCategory.Code, item.category_code);
                    try {
                        activity.getContentResolver().insert(BookProvider.BOOKCATEGORY_URI, contentValues);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void addSearchHistory(final Activity activity, final String book_name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String projection[] = {DB_Column.SearchHistory.BOOK_NAME};
                String selection = DB_Column.SearchHistory.BOOK_NAME + "='" + book_name + "'";
                Cursor result_cursor = activity.getContentResolver().query(BookProvider.SEARCH_HISTORY_URI, projection, selection, null, null);
                //If exist history, delete and insert again
                if (result_cursor != null && result_cursor.getCount() != 0) {
                    activity.getContentResolver().delete(BookProvider.SEARCH_HISTORY_URI, selection, null);
                }
                result_cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DB_Column.SearchHistory.BOOK_NAME, book_name);
                contentValues.put(DB_Column.SearchHistory.TIMESTAMP, System.currentTimeMillis() + "");
                try {
                    activity.getContentResolver().insert(BookProvider.SEARCH_HISTORY_URI, contentValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Cursor cursor = activity.getContentResolver().query(BookProvider.SEARCH_HISTORY_URI, projection, null, null, null);
                //most search history count is 15
                if (cursor != null && cursor.getCount() > 15) {
                    String deleteSelection = DB_Column.SearchHistory.ID + "=" + "(select min(" + DB_Column.SearchHistory.ID + ") from " + BookSQLiteOpenHelper.SEARCH_HISTORY_TABLE_NAME + ")";
                    activity.getContentResolver().delete(BookProvider.SEARCH_HISTORY_URI, deleteSelection, null);
                }
            }
        }).start();
    }

    @Deprecated
    public static void getBookCategory(final Activity activity, final BookCategory category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor result_cursor = null;
                result_cursor = activity.getContentResolver().query(BookProvider.BOOKCATEGORY_URI, null, null, null, null);
                ArrayList<BookCategory.CategoryItem> list = new ArrayList<BookCategory.CategoryItem>();
                if (result_cursor != null) {
                    if (result_cursor.moveToFirst()) {
                        do {
                            String category_name = result_cursor.getString(Projection.BookCategory.COLUMN_NAME);
                            int category_code = result_cursor.getInt(Projection.BookCategory.COLUMN_CODE);
                            list.add(new BookCategory.CategoryItem(category_code, category_name));
                        } while (result_cursor.moveToNext());
                        category.setUser_category_list(list);
//                        if (((MainActivity) activity).mGridListViewAdapter != null) {
//                            //this code is error, it will throw exception "Only the original thread that created a view hierarchy can touch its views"
//                            //use sendMessage replace notifyDataSetChanged
//                            ((MainActivity) activity).mGridListViewAdapter.notifyDataSetChanged();
//                        }
                    }
                    result_cursor.close();
                }

            }
        }).start();
    }

    public void loadBookList(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mlistLoader = new BookListLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();

        LoaderItem item = new LoaderItem(mlistLoader, mLoadListener);
        loaders.add(item);
    }

    public ArrayList<LoaderItem> getLoaders() {
        return loaders;
    }

    public void reset() {
        loaders.clear();
        loadCompleteTimes = 0;
    }

    public static class LoaderItem {
        BookListLoader loader;
        BookListLoadListener listener;

        public LoaderItem(BookListLoader loader, BookListLoadListener listener) {
            this.loader = loader;
            this.listener = listener;
        }
    }

    public class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            Log.i("BookListLoader", "load complete " + ((BookListLoader) loader).getLoaderSelection());
            loadCompleteTimes++;
            if (data == null || data.getCount() == 0) {
                if (loadCompleteTimes == loaders.size()) {
                    adapter.buildAdapterList();
                    adapter.notifyDataSetChanged();
                }
                return;
            }
            if (((BookListLoader) loader).getLoaderSelection() == null) {//if selection is null, then it is all book list loader
                adapter.registerDataCursor('a', data);
            } else {
                data.moveToFirst();
                int category_code = data.getInt(data.getColumnIndex(DB_Column.BookInfo.CATEGORY_CODE));
                adapter.registerDataCursor(category_code, data);
            }

            if (loadCompleteTimes == loaders.size()) {
                adapter.buildAdapterList();
                adapter.notifyDataSetChanged();
            }
        }
    }
}
