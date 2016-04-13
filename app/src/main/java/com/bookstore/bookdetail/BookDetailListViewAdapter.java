package com.bookstore.bookdetail;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/4/13.
 */
public class BookDetailListViewAdapter extends BaseAdapter {
    private Context mContext;

    public BookDetailListViewAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View detail_item_view = null;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        switch (position) {
            case 0:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item0, null);
                    TextView book_title = (TextView) detail_item_view.findViewById(R.id.detail_book_title);
                    book_title.setText("ttttttt");

                    ImageView book_cover = (ImageView) detail_item_view.findViewById(R.id.detail_book_cover);
                    book_cover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity) mContext).getFragmentManager().popBackStack();
                        }
                    });
                } else {
                    detail_item_view = convertView;
                }
                break;
            case 1:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item1, null);
                    TextView book_intro = (TextView) detail_item_view.findViewById(R.id.detail_book_introdution);
                    book_intro.setText("简介");
                } else {
                    detail_item_view = convertView;
                }
                break;
            case 2:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item2, null);
                    TextView book_catalog = (TextView) detail_item_view.findViewById(R.id.catalog_header);
                    book_catalog.setText("目录");
                } else {
                    detail_item_view = convertView;
                }
                break;
        }
        return detail_item_view;
    }
}
