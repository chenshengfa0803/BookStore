package com.bookstore.connection.douban;

import com.bookstore.connection.BookInfoUrlBase;

/**
 * Created by 许杰 on 2016/3/12.
 */
public class DoubanBookInfoUrl implements BookInfoUrlBase {

    private static final String ISBN_PREFIX = "https://api.douban.com/v2/book/isbn/:";

    @Override
    public String getRequestUrl(int type, Object... args) {
        switch (type) {
            case REQ_ISBN :
            {
                if (args[0] instanceof Integer) {
                    int isbn = (Integer) args[0];
                    return ISBN_PREFIX + isbn;
                } else
                    return null;
            }
        }
        return null;
    }
}
