package com.bookstore.connection.douban;

import com.bookstore.connection.BookInfoUrlBase;

/**
 * Created by 许杰 on 2016/3/12.
 */
public class DoubanBookInfoUrl implements BookInfoUrlBase {

    private static final String ISBN_PREFIX = "https://api.douban.com/v2/book/isbn/:";
    private String bookUrl = null;

    public DoubanBookInfoUrl(String isbn) {
        bookUrl = ISBN_PREFIX + isbn;
    }

    @Override
    public String getRequestUrl(int type) {
        switch (type) {
            case REQ_ISBN :
            {
                if (bookUrl != null) {
                    return bookUrl;
                }
            }
        }
        return null;
    }
}
