package com.bookstore.booklist;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;
import com.bookstore.util.BookCategory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListViewAdapter extends BaseAdapter {
    public static final String TAG = "BookStore";
    public Context mContext;
    private TypedArray mColor_list;
    private Cursor mDataCursor = null;

    public BookListGridListViewAdapter(Context context) {
        mContext = context;
        mColor_list = mContext.getResources().obtainTypedArray(R.array.color_list);
    }

    @Override

    public int getCount() {
        return BookCategory.getCategoryCount();
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
            View morebooks = listItemView.findViewById(R.id.MoreBooks);
            morebooks.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Log.i("csf", "btn click");
                }

            });

            View color_panel = listItemView.findViewById(R.id.color_view);
            color_panel.setBackgroundColor(getColor(position));
        } else {
            listItemView = convertView;
        }
        bindView(listItemView, position);
        return listItemView;
    }

    public void bindView(View listItemView, int position) {
        TextView category_name = (TextView) listItemView.findViewById(R.id.category_name);
        category_name.setText(BookCategory.getCategoryName(position));

        if (position == 0) {
            if (mDataCursor == null) return;
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

            //show cover1
            mDataCursor.moveToPosition(position);
            String cover1Url = mDataCursor.getString(DataBaseProjection.COLUMN_IMG_LARGE);
            ImageView cover1 = (ImageView) listItemView.findViewById(R.id.cover1);
            ImageLoader.getInstance().displayImage(cover1Url, cover1, options);

            //show cover2
            mDataCursor.moveToPosition(position + 1);
            String cover2Url = mDataCursor.getString(DataBaseProjection.COLUMN_IMG_LARGE);
            ImageView cover2 = (ImageView) listItemView.findViewById(R.id.cover2);
            ImageLoader.getInstance().displayImage(cover2Url, cover2, options);

            //show cover3
            mDataCursor.moveToPosition(position + 2);
            String cover3Url = mDataCursor.getString(DataBaseProjection.COLUMN_IMG_LARGE);
            ImageView cover3 = (ImageView) listItemView.findViewById(R.id.cover3);
            ImageLoader.getInstance().displayImage(cover3Url, cover3, options);
        }
    }

    public int getColor(int position) {
        return mColor_list.getColor(position % mColor_list.length(), 0);
    }

    public void registerDataCursor(Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        if (mDataCursor != null) {
            mDataCursor.close();
        }
        mDataCursor = dataCursor;
    }
}
