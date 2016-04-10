package com.bookstore.booklist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/4/10.
 */
public class CategoryBookGridView extends GridView {
    public CategoryBookGridView(Context context) {
        super(context);
    }

    public CategoryBookGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryBookGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
