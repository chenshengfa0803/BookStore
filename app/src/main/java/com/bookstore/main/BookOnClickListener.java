package com.bookstore.main;

import android.view.View;

/**
 * Created by Administrator on 2016/4/11.
 */
public interface BookOnClickListener {
    void onBookClick(View clickedImageView, int book_id, int category_code);
}
