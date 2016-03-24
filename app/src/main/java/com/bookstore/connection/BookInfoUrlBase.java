package com.bookstore.connection;

/**
 * Created by 许杰 on 2016/3/10.
 */

public interface BookInfoUrlBase {

    int REQ_ISBN = 1; //isbn
    int REQ_NAME = REQ_ISBN + 1; //name
    int REQ_AUTH = REQ_NAME + 1; //author
    int REQ_CATEGORY = REQ_AUTH + 1;//category
    //TODO :  request type
    int REQ_END = 30; // the end

    String getRequestUrl(int type);

}
