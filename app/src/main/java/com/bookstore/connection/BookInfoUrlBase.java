package com.bookstore.connection;

import java.util.List;

/**
 * Created by 许杰 on 2016/3/10.
 */

public interface BookInfoUrlBase {

    final static int REQ_ISBN = 1; //isbn
    final static int REQ_NAME = REQ_ISBN + 1; //name
    final static int REQ_AUTH = REQ_NAME + 1; //author
    //TODO :  request type
    final static int REQ_END = 30; // the end

    String getRequestUrl(int type, Object... args);

}
