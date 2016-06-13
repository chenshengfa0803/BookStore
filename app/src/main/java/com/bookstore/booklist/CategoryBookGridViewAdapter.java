package com.bookstore.booklist;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Item> mDataList = null;
    private ArrayList<CloudItem> mCloudDataList = null;
    private boolean mIsSelectionMode = false;
    private HashSet<Long> mSelectedItems = new HashSet<Long>();

    public CategoryBookGridViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mCloudDataList == null) {
            return 0;
        } else {
            return mCloudDataList.size();
        }
    }

    public ArrayList<Item> getDataList() {
        return mDataList;
    }

    public void removeData(int pos) {
        mDataList.remove(pos);
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
        CheckBox cb = (CheckBox) gridItemView.findViewById(R.id.book_check);
        if (mSelectedItems.contains((long) position)) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        if (mIsSelectionMode) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.GONE);
        }

        String coverUrl = mCloudDataList.get(position).img_larg;
        String book_name = mCloudDataList.get(position).title;
        ImageLoader.getInstance().displayImage(coverUrl, book_cover, options);
        book_title.setText(book_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            book_cover.setTransitionName("image" + position);
        }
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

    public void registerCloudData(List<AVObject> list) {
        mCloudDataList = new ArrayList<>();
        for (AVObject item : list) {
            String objectId = item.getObjectId();
            String img = item.getString(DB_Column.BookInfo.IMG_LARGE);
            String title = item.getString(DB_Column.BookInfo.TITLE);
            int category_code = item.getInt(DB_Column.BookInfo.CATEGORY_CODE);
            CloudItem cloudItem = new CloudItem(objectId, img, title, category_code);
            mCloudDataList.add(cloudItem);
        }
    }

    public void setSelectionMode(boolean isSelectionMode) {
        if (mIsSelectionMode != isSelectionMode) {
            mIsSelectionMode = isSelectionMode;
            notifyDataSetChanged();
        }
    }

    public void updateSelectedItems(int pos) {
        if (mSelectedItems.contains((long) pos)) {
            mSelectedItems.remove((long) pos);
        } else {
            mSelectedItems.add((long) pos);
        }
    }

    public void clearSelectedItems() {
        mSelectedItems.clear();
    }

    public HashSet<Long> getSelectedItems() {
        return mSelectedItems;
    }

    public void selectAllItems() {
        clearSelectedItems();
        for (int i = 0; i < getCount(); i++) {
            mSelectedItems.add((long) i);
        }
    }

    public int getSelectedCount() {
        return mSelectedItems.size();
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

    public class CloudItem {
        String objectId;
        String img_larg;
        String title;
        int category_code;

        public CloudItem(String objectId, String img_larg, String title, int category_code) {
            this.objectId = objectId;
            this.img_larg = img_larg;
            this.title = title;
            this.category_code = category_code;
        }
    }
}
