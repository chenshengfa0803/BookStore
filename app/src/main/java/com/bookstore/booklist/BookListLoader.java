package com.bookstore.booklist;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by Administrator on 2016/3/11.
 */
public class BookListLoader extends AsyncTaskLoader {
    public BookListLoader(Context context) {
        super(context);
    }

    @Override
    public Object loadInBackground() {
        return null;
    }
}
