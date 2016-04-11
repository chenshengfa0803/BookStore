package com.bookstore.booklist;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bookstore.bookparser.BookCategory;
import com.bookstore.main.BookOnClickListener;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListViewAdapter extends BaseAdapter {
    public static final String TAG = "BookStore";
    BookCategory bookCategory;
    private Context mContext;
    private TypedArray mColor_list;
    private ArrayList<AdapterItem> dataList = null;
    private ArrayList<AdapterItem> adapterList = null;
    private BookOnClickListener mListener = null;

    public BookListGridListViewAdapter(Context context, BookOnClickListener listener) {
        mContext = context;
        mListener = listener;
        mColor_list = mContext.getResources().obtainTypedArray(R.array.color_list);
        bookCategory = new BookCategory();
        dataList = new ArrayList<AdapterItem>();
    }

    @Override

    public int getCount() {
        //return bookCategory.getCategoryCount();
        if (adapterList != null) {
            return adapterList.size();
        } else {
            return 0;
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
        View listItemView;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            listItemView = mInflater.inflate(R.layout.booklist_gridview_list_item, null);

            View color_panel = listItemView.findViewById(R.id.color_view);
            color_panel.setBackgroundColor(getColor(position));
        } else {
            listItemView = convertView;
        }
        bindView(listItemView, position);
        return listItemView;
    }

    public void bindView(View listItemView, int position) {
        final int category_code = adapterList.get(position).category_code;
        final Cursor dataCursor = adapterList.get(position).dataCursor;

        LinearLayout category_line = (LinearLayout) listItemView.findViewById(R.id.book_category);
        category_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryBookListFragment fragment = CategoryBookListFragment.newInstance(category_code);
                String tag = CategoryBookListFragment.class.getSimpleName();
                ((MainActivity) mContext).replaceFragment(fragment, tag);
            }
        });

        TextView category_name = (TextView) listItemView.findViewById(R.id.category_name);
        category_name.setText(bookCategory.getCategoryName(category_code));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //show cover1
        final ImageView cover1 = (ImageView) listItemView.findViewById(R.id.cover1);
        if (dataCursor.moveToPosition(0)) {
            //String cover1Url = mDataCursor.getString(Projection.BookInfo.COLUMN_IMG_LARGE);
            String cover1Url = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            if (!cover1Url.equals(cover1.getTag())) {
                ImageLoader.getInstance().displayImage(cover1Url, cover1, options);
            }
            cover1.setTag(cover1Url);
            cover1.setTransitionName("item" + position + "cover1");
            cover1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataCursor.moveToPosition(0);
                    mListener.onBookClick(cover1, dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID)));
                }
            });
        } else {
            cover1.setImageBitmap(null);
            cover1.setTag(null);
        }
        //show cover2
        final ImageView cover2 = (ImageView) listItemView.findViewById(R.id.cover2);
        if (dataCursor.moveToPosition(1)) {
            //String cover2Url = mDataCursor.getString(Projection.BookInfo.COLUMN_IMG_LARGE);
            String cover2Url = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            if (!cover2Url.equals(cover2.getTag())) {
                ImageLoader.getInstance().displayImage(cover2Url, cover2, options);
            }
            cover2.setTag(cover2Url);
            cover2.setTransitionName("item" + position + "cover2");
            cover2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataCursor.moveToPosition(1);
                    mListener.onBookClick(cover2, dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID)));
                }
            });
        } else {
            cover2.setImageBitmap(null);
            cover2.setTag(null);
        }
        //show cover3
        final ImageView cover3 = (ImageView) listItemView.findViewById(R.id.cover3);
        if (dataCursor.moveToPosition(2)) {
            //String cover3Url = mDataCursor.getString(Projection.BookInfo.COLUMN_IMG_LARGE);
            String cover3Url = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            if (!cover3Url.equals(cover3.getTag())) {
                ImageLoader.getInstance().displayImage(cover3Url, cover3, options);
            }
            cover3.setTag(cover3Url);
            cover3.setTransitionName("item" + position + "cover3");
            cover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataCursor.moveToPosition(2);
                    mListener.onBookClick(cover3, dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID)));
                }
            });
        } else {
            cover3.setImageBitmap(null);
            cover3.setTag(null);
        }
        //dataCursor.close();
    }

    public int getColor(int position) {
        return mColor_list.getColor(position % mColor_list.length(), 0);
    }

    public void registerDataCursor(int category_code, Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        int index = bookCategory.getIndexByCategoryCode(category_code);
        //dataList.add(index, new AdapterItem(category_code, dataCursor));
        dataList.set(index, new AdapterItem(category_code, dataCursor));
    }

    public void buildAdapterList() {
        adapterList = new ArrayList<AdapterItem>();
        for (AdapterItem item : dataList) {
            if (item.category_code != -1) {
                adapterList.add(item);
            }
        }
    }

    public void reset() {
        for (AdapterItem item : dataList) {
            if (item.dataCursor != null && !item.dataCursor.isClosed()) {
                item.dataCursor.close();
            }
        }
        dataList.clear();
        for (int i = 0; i < bookCategory.getCategoryCount(); i++) {
            dataList.add(new AdapterItem(-1, null));
        }
    }

    class AdapterItem {
        public int category_code;
        public Cursor dataCursor;

        public AdapterItem(int category_code, Cursor dataCursor) {
            this.category_code = category_code;
            this.dataCursor = dataCursor;
        }
    }
}
