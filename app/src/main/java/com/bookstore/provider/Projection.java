package com.bookstore.provider;

/**
 * Created by Administrator on 2016/3/12.
 */
public class Projection {
    public class BookInfo {
        public final static int COLUMN_ID = 0;
        public final static int COLUMN_TITLE = COLUMN_ID + 1;
        public final static int COLUMN_AUTHOR = COLUMN_ID + 2;
        public final static int COLUMN_TRANSLATOR = COLUMN_ID + 3;
        public final static int COLUMN_PUB_DATE = COLUMN_ID + 4;
        public final static int COLUMN_PUBLISHER = COLUMN_ID + 5;
        public final static int COLUMN_PRICE = COLUMN_ID + 6;
        public final static int COLUMN_PAGES = COLUMN_ID + 7;
        public final static int COLUMN_BINGDING = COLUMN_ID + 8;
        public final static int COLUMN_IMG_SMALL = COLUMN_ID + 9;
        public final static int COLUMN_IMG_MEDIUM = COLUMN_ID + 10;
        public final static int COLUMN_IMG_LARGE = COLUMN_ID + 11;
        public final static int COLUMN_ISBN10 = COLUMN_ID + 12;
        public final static int COLUMN_ISBN13 = COLUMN_ID + 13;
        public final static int COLUMN_ADD_DATE = COLUMN_ID + 14;
        public final static int COLUMN_CATEGORY_NAME = COLUMN_ID + 15;
        public final static int COLUMN_CATEGORY_CODE = COLUMN_ID + 16;
        public final static int COLUMN_CLC_NUMBER = COLUMN_ID + 17;
    }

    public class BookCategory {
        public final static int COLUMN_ID = 0;
        public final static int COLUMN_NAME = 1;
        public final static int COLUMN_CODE = 2;
    }

}
