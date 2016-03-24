package com.bookstore.connection;

/**
 * Created by Administrator on 2016/3/25.
 */
public class ChineseLibraryURL implements BookInfoUrlBase {
    private static final String ISBN_PREFIX = "http://opac.nlc.gov.cn/F//LDUXGVI4LDYUK5J21EDBXHLDYDN8BK2PM353H8UQ7AXNYI9CQL-22557?func=find-b&\\find_code=ISB&request=";
    private static final String ISBN_SUFFIX = "&local_base=&filter_code_1=WLN&filter_request_1=&filter_code_2=WYR&filter_request_2=&filter_code_3=WYR&filter_request_3=&filter_code_4=WFM&filter_request_4=&filter_code_5=WSL&filter_request_5=";
    private String bookUrl = null;

    public ChineseLibraryURL(String isbn) {
        bookUrl = ISBN_PREFIX + isbn;
    }

    @Override
    public String getRequestUrl(int type) {
        switch (type) {
            case REQ_CATEGORY: {
                if (bookUrl != null) {
                    return bookUrl;
                }
            }
        }
        return null;
    }
}
