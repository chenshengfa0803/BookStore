package com.bookstore.booklist;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.main.BookOnClickListener;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListViewAdapter extends BaseAdapter {
    public static final String TAG = "BookStore";
    BookCategory bookCategory;
    private Context mContext;
    private TypedArray mColor_list;
    private ArrayList<ArrayList<AdapterItem>> dataList = null;
    private ArrayList<ArrayList<AdapterCloudItem>> cloudDataList = null;
    private ArrayList<ArrayList<AdapterCloudItem>> adapterList = null;
    private BookOnClickListener mListener = null;

    public BookListGridListViewAdapter(Context context, BookOnClickListener listener) {
        mContext = context;
        mListener = listener;
        mColor_list = mContext.getResources().obtainTypedArray(R.array.color_list);
        bookCategory = new BookCategory();
        dataList = new ArrayList<ArrayList<AdapterItem>>();
        cloudDataList = new ArrayList<>();
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
        ArrayList<AdapterCloudItem> rowList = adapterList.get(position);
        final int category_code = rowList.get(0).category_code;

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
        category_name.setText(BookCategory.getCategoryName(category_code));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        //show cover1
        final ImageView cover1 = (ImageView) listItemView.findViewById(R.id.cover1);
        if (rowList.size() > 0) {
            final AdapterCloudItem item1 = rowList.get(0);
            String cover1Url = item1.img_large;
            if (!cover1Url.equals(cover1.getTag())) {
                ImageLoader.getInstance().displayImage(cover1Url, cover1, options);
            }
            cover1.setTag(cover1Url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cover1.setTransitionName("item" + position + "cover1");
            }
            cover1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onBookClick(cover1, item1.book_id, item1.category_code);
                }
            });
        } else {
            cover1.setImageBitmap(null);
            cover1.setTag(null);
        }
        //show cover2
        final ImageView cover2 = (ImageView) listItemView.findViewById(R.id.cover2);
        if (rowList.size() > 1) {
            final AdapterCloudItem item2 = rowList.get(1);
            String cover2Url = item2.img_large;
            if (!cover2Url.equals(cover2.getTag())) {
                ImageLoader.getInstance().displayImage(cover2Url, cover2, options);
            }
            cover2.setTag(cover2Url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cover2.setTransitionName("item" + position + "cover2");
            }
            cover2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onBookClick(cover2, item2.book_id, item2.category_code);
                }
            });
        } else {
            cover2.setImageBitmap(null);
            cover2.setTag(null);
        }
        //show cover3
        final ImageView cover3 = (ImageView) listItemView.findViewById(R.id.cover3);
        if (rowList.size() > 2) {
            final AdapterCloudItem item3 = rowList.get(2);
            String cover3Url = item3.img_large;
            if (!cover3Url.equals(cover3.getTag())) {
                ImageLoader.getInstance().displayImage(cover3Url, cover3, options);
            }
            cover3.setTag(cover3Url);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cover3.setTransitionName("item" + position + "cover3");
            }
            cover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onBookClick(cover3, item3.book_id, item3.category_code);
                }
            });
        } else {
            cover3.setImageBitmap(null);
            cover3.setTag(null);
        }
    }

    public int getColor(int position) {
        return mColor_list.getColor(position % mColor_list.length(), 0);
    }

    public void registerDataCursor(int category_code, Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        int index = bookCategory.getIndexByCategoryCode(category_code);

        ArrayList<AdapterItem> dataOneRow = new ArrayList<>();
        dataCursor.moveToFirst();
        do {
            int id = dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID));
            String img_large = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
            dataOneRow.add(new AdapterItem(category_code, id, img_large));
        } while (dataCursor.moveToNext());

        dataList.set(index, dataOneRow);
        dataCursor.close();
    }

    public void registerCloudData(int category_code, List<AVObject> list) {
        int index = bookCategory.getIndexByCategoryCode(category_code);
        ArrayList<AdapterCloudItem> dataOneRow = new ArrayList<>();
        for (AVObject item : list) {
            String objectId = item.getObjectId();
            String img_large = item.getString(DB_Column.BookInfo.IMG_LARGE);
            dataOneRow.add(new AdapterCloudItem(category_code, objectId, img_large));
        }
        cloudDataList.set(index, dataOneRow);
    }

    public void buildAdapterList() {
        adapterList = new ArrayList<ArrayList<AdapterCloudItem>>();
        for (ArrayList<AdapterCloudItem> item : cloudDataList) {
            if (item.size() != 0) {
                adapterList.add(item);
            }
        }
    }

    public void reset() {
        dataList.clear();
        ArrayList<AdapterItem> list = new ArrayList<>();
        for (int i = 0; i < bookCategory.getCategoryCount(); i++) {
            dataList.add(list);
        }

        cloudDataList.clear();
        ArrayList<AdapterCloudItem> cloudList = new ArrayList<>();
        for (int i = 0; i < bookCategory.getCategoryCount(); i++) {
            cloudDataList.add(cloudList);
        }
    }

    class AdapterItem {
        public int category_code;
        public int book_id;
        public String img_large;

        public AdapterItem(int category_code, int book_id, String img_large) {
            this.category_code = category_code;
            this.book_id = book_id;
            this.img_large = img_large;
        }
    }

    class AdapterCloudItem {
        public int category_code;
        public String objectId;
        public String img_large;

        public AdapterCloudItem(int category_code, String objectId, String img_large) {
            this.category_code = category_code;
            this.objectId = objectId;
            this.img_large = img_large;
        }
    }
}
