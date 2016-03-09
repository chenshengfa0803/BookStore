package com.bookstore.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016/3/9.
 */
public class BookSQLiteOpenHelper extends SQLiteOpenHelper {
    //version 1: Add database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookProvider.db";
    public static final String BOOKINFO_TABLE_NAME = "BookInfo";
    private static BookSQLiteOpenHelper singleton;
    private Context mContext;

    public BookSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context.getApplicationContext();
    }

    public static BookSQLiteOpenHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new BookSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        return singleton;
    }

    //when call get<Readable/Writable>Database, if no database, it will call
    //onCreate function and create new DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_database = "create table " + BOOKINFO_TABLE_NAME + " ("
                + DB_Column.ID + " integer primary key autoincrement, "
                + DB_Column.TITLE + " text not null, "
                + DB_Column.AUTHOR + " text, "
                + DB_Column.TRANSLATOR + " text, "
                + DB_Column.PUB_DATE + " text, "
                + DB_Column.PUBLISHER + " text, "
                + DB_Column.PRICE + " text, "
                + DB_Column.PAGES + " integer, "
                + DB_Column.BINGDING + " text, "
                + DB_Column.IMG_SMALL + " text, "
                + DB_Column.IMG_MEDIUM + " text, "
                + DB_Column.IMG_LARGE + " text, "
                + DB_Column.ISBN10 + " text, "
                + DB_Column.ISBN13 + " text, "
                + DB_Column.ADD_DATE + " text" + ");";

        try {
            db.execSQL(create_database);
            Log.i("BookDatabase", create_database);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("BookDatabase", "upgrading from version " + oldVersion + " to " + newVersion + ", old data will be destroy");
        try {
            db.execSQL("DROP TABLE IF EXISTS " + BOOKINFO_TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onCreate(db);
    }
}
