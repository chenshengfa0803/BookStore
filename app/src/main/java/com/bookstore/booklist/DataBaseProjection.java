package com.bookstore.booklist;

/**
 * Created by Administrator on 2016/3/12.
 */
public interface DataBaseProjection {
    int COLUMN_ID = 0;
    int COLUMN_TITLE = COLUMN_ID + 1;
    int COLUMN_AUTHOR = COLUMN_ID + 2;
    int COLUMN_TRANSLATOR = COLUMN_ID + 3;
    int COLUMN_PUB_DATE = COLUMN_ID + 4;
    int COLUMN_PUBLISHER = COLUMN_ID + 5;
    int COLUMN_PRICE = COLUMN_ID + 6;
    int COLUMN_PAGES = COLUMN_ID + 7;
    int COLUMN_BINGDING = COLUMN_ID + 8;
    int COLUMN_IMG_SMALL = COLUMN_ID + 9;
    int COLUMN_IMG_MEDIUM = COLUMN_ID + 10;
    int COLUMN_IMG_LARGE = COLUMN_ID + 11;
    int COLUMN_ISBN10 = COLUMN_ID + 12;
    int COLUMN_ISBN13 = COLUMN_ID + 13;
    int COLUMN_ADD_DATE = COLUMN_ID + 14;
}
