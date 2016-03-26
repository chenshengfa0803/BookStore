package com.bookstore.main;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.bookstore.bookparser.BookCategory;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/26.
 */
public class DBHandler {
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

    public static void getBookCategory(final Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor result_cursor = null;
                result_cursor = activity.getContentResolver().query(BookProvider.BOOKCATEGORY_URI, null, null, null, null);
            }
        }).start();
    }
}
