package com.bookstore.booklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListViewAdapter extends BaseAdapter {
    public Context mContext;

    public BookListGridListViewAdapter(Context context) {
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
        View listItemView;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            listItemView = mInflater.inflate(R.layout.booklist_gridview_list_item, null);
            View color_panel = listItemView.findViewById(R.id.color_view);
            color_panel.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            listItemView = convertView;
        }
        return listItemView;
    }
}
