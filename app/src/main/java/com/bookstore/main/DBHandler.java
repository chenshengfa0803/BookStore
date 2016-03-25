package com.bookstore.main;

import android.app.Activity;
import android.content.ContentValues;

import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/26.
 */
public class DBHandler {
    public static void saveBookCategory(final Activity activity, final ArrayList<String> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String category_name : list) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DB_Column.BookCategory.Name, category_name);
                    try {
                        activity.getContentResolver().insert(BookProvider.BOOKCATEGORY_URI, contentValues);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
