package com.bookstore.booklist;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookstore.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListViewPagerAdapter extends PagerAdapter {

    private List<View> mViewList = new ArrayList<>();
    private LayoutInflater mInflater;

    public BookListViewPagerAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        View booklist_gridview = mInflater.inflate(R.layout.booklist_gridview, null);
        View booklist_listview = mInflater.inflate(R.layout.booklist_listview, null);
        mViewList.add(booklist_gridview);
        mViewList.add(booklist_listview);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
