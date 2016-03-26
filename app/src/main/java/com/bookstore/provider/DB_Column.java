package com.bookstore.provider;

/**
 * Created by Administrator on 2016/3/9.
 */
public abstract class DB_Column {
    public class BookInfo {
        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String TRANSLATOR = "translator";
        public static final String PUB_DATE = "pub_date";
        public static final String PUBLISHER = "publisher";
        public static final String PRICE = "price";
        public static final String PAGES = "pages";
        public static final String BINGDING = "binding";
        public static final String IMG_SMALL = "img_small";
        public static final String IMG_MEDIUM = "img_medium";
        public static final String IMG_LARGE = "img_large";
        public static final String ISBN10 = "isbn10";
        public static final String ISBN13 = "isbn13";
        public static final String ADD_DATE = "timestamp";
        public static final String CATEGORY = "category";
        public static final String CLC_NUMBER = "clc_number";//Chinese Libray Classification number,中图分类号，根据这个number把图书分类，需要到国家图书馆获取该number
    }

    public class BookCategory {
        public static final String ID = "_id";
        public static final String Name = "category_name";
        public static final String Code = "category_code";
    }





}
