package com.bookstore.booklist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor dataCursor = null;

    public CategoryBookGridViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (dataCursor == null) {
            return 0;
        } else {
            return dataCursor.getCount();
        }
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
        View gridItemView;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            gridItemView = mInflater.inflate(R.layout.category_list_grid_item, null);
        } else {
            gridItemView = convertView;
        }
        bindView(gridItemView, position);
        return gridItemView;
    }

    public void bindView(View gridItemView, int position) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageView book_cover = (ImageView) gridItemView.findViewById(R.id.book_cover);
        TextView book_title = (TextView) gridItemView.findViewById(R.id.book_name);
        if (dataCursor.moveToPosition(position)) {
            String coverUrl = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            String book_name = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.TITLE));
            ImageLoader.getInstance().displayImage(coverUrl, book_cover, options);
            book_title.setText(book_name);
        }

    }

    public void registerDataCursor(Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        this.dataCursor = dataCursor;
    }
}
