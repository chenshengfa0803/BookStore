package com.bookstore.booklist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListView extends ListView {
    private ListView mGridList;

    public BookListGridListView(Context context) {
        super(context);
        initGridView(context);
    }

    public BookListGridListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGridView(context);
    }

    public BookListGridListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGridView(context);
    }

    public void initGridView(Context context) {
        //LayoutInflater mInflater;
        //mInflater = LayoutInflater.from(context);
        //View booklist_gridview = mInflater.inflate(R.layout.booklist_gridview, null);
        //mGridList = (ListView) booklist_gridview.findViewById(R.id.booklist_grid);
    }

    public ListView getGridList() {
        return mGridList;
    }
}
