package com.bookstore.booklist;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.BookOnClickListener;
import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private BookOnClickListener mListener = null;
    private ArrayList<Item> mDataList = null;

    public CategoryBookGridViewAdapter(Context context, BookOnClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    public ArrayList<Item> getDataList() {
        return mDataList;
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

    public void bindView(View gridItemView, final int position) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        final ImageView book_cover = (ImageView) gridItemView.findViewById(R.id.book_cover);
        TextView book_title = (TextView) gridItemView.findViewById(R.id.book_name);

        String coverUrl = mDataList.get(position).img_larg;
        String book_name = mDataList.get(position).title;
        ImageLoader.getInstance().displayImage(coverUrl, book_cover, options);
        book_title.setText(book_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            book_cover.setTransitionName("image" + position);
        }
        gridItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBookClick(book_cover,
                        mDataList.get(position).book_id, mDataList.get(position).category_code);
            }
        });

    }

    public void registerDataCursor(Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        mDataList = new ArrayList<>();
        dataCursor.moveToFirst();
        do {
            int id = dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID));
            String img = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            String title = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.TITLE));
            int category_code = dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.CATEGORY_CODE));
            Item item = new Item(id, img, title, category_code);
            mDataList.add(item);
        } while (dataCursor.moveToNext());

        dataCursor.close();
    }

    public class Item {
        int book_id;
        String img_larg;
        String title;
        int category_code;

        public Item(int book_id, String img_larg, String title, int category_code) {
            this.book_id = book_id;
            this.img_larg = img_larg;
            this.title = title;
            this.category_code = category_code;
        }
    }
}
