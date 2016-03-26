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
    //version 2: change DB_Column.PAGES from integer to text
    //version 3: add BookInfo table category column
    //version 4: add BookInfo table CLC_NUMBER column
    //version 5: add BookCategory table
    //version 6: add BookCategory table CODE column
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "BookProvider.db";
    public static final String BOOKINFO_TABLE_NAME = "BookInfo";
    public static final String BOOKCATEGORY_TABLE_NAME = "BookCategory";
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
        Log.d("BookStore", "create database table");
        createBookInfoTable(db);
        createBookCategoryTable(db);

    }

    public void createBookInfoTable(SQLiteDatabase db) {
        String create_database = "create table " + BOOKINFO_TABLE_NAME + " ("
                + DB_Column.BookInfo.ID + " integer primary key autoincrement, "
                + DB_Column.BookInfo.TITLE + " text not null, "//if title is null, then it will fail to insert
                + DB_Column.BookInfo.AUTHOR + " text, "
                + DB_Column.BookInfo.TRANSLATOR + " text, "
                + DB_Column.BookInfo.PUB_DATE + " text, "
                + DB_Column.BookInfo.PUBLISHER + " text, "
                + DB_Column.BookInfo.PRICE + " text, "
                + DB_Column.BookInfo.PAGES + " text, "
                + DB_Column.BookInfo.BINGDING + " text, "
                + DB_Column.BookInfo.IMG_SMALL + " text, "
                + DB_Column.BookInfo.IMG_MEDIUM + " text, "
                + DB_Column.BookInfo.IMG_LARGE + " text, "
                + DB_Column.BookInfo.ISBN10 + " text, "
                + DB_Column.BookInfo.ISBN13 + " text, "
                + DB_Column.BookInfo.ADD_DATE + " text, "
                + DB_Column.BookInfo.CATEGORY + " integer, "
                + DB_Column.BookInfo.CLC_NUMBER + " text" + ");";

        try {
            db.execSQL(create_database);
            Log.i("BookDatabase", create_database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createBookCategoryTable(SQLiteDatabase db) {
        String create_database = "create table " + BOOKCATEGORY_TABLE_NAME + " ("
                + DB_Column.BookCategory.ID + " integer primary key autoincrement, "
                + DB_Column.BookCategory.Name + " text not null, "
                + DB_Column.BookCategory.Code + " integer" + ");";

        try {
            db.execSQL(create_database);
            Log.i("BookDatabase", create_database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //when database version code change, it will call onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("BookDatabase", "upgrading from version " + oldVersion + " to " + newVersion + ", old data will be destroy");
        try {
            db.execSQL("DROP TABLE IF EXISTS " + BOOKINFO_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BOOKCATEGORY_TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onCreate(db);
    }
}
