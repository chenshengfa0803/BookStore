package com.bookstore.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bookstore.bookdetail.BookDetailFragment;
import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.main.animation.BookDetailTransition;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;
import com.bookstore.util.BitmapUtil;
import com.bookstore.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class MainBookListFragment extends Fragment {
    public DBHandler dbHandler = null;
    public BookCategory mBookCategory = null;
    private Activity mActivity;
    private View booklist_fragment = null;
    private ViewPager bookListViewPager;
    private BookListViewPagerAdapter pagerAdapter;
    private BookListGridListView gridListView = null;
    private BookListGridListViewAdapter mGridListViewAdapter;
    private BookOnClickListener mListener = new BookOnClickListener() {
        @Override
        public void onBookClick(View clickedImageView, int book_id, int category_code) {
            Bitmap bitmap = null;
            int paletteColor = getResources().getColor(android.R.color.darker_gray);
            BitmapDrawable bd = (BitmapDrawable) ((ImageView) clickedImageView).getDrawable();
            if (bd != null) {
                bitmap = bd.getBitmap();
                paletteColor = BitmapUtil.getPaletteColor(bitmap);
            }
            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book_id, category_code, paletteColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                detailFragment.setSharedElementEnterTransition(new BookDetailTransition());
                setExitTransition(new Fade());
                detailFragment.setEnterTransition(new Fade());
                detailFragment.setSharedElementReturnTransition(new BookDetailTransition());
            }

            FragmentManager fragmentManager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addSharedElement(clickedImageView, getResources().getString(R.string.image_transition))
                    .add(R.id.container_view, detailFragment)
                    .addToBackStack(null)
                    .hide(fragmentManager.findFragmentByTag(MainBookListFragment.class.getSimpleName()))
                    .commit();

        }
    };

    public static MainBookListFragment newInstance() {
        MainBookListFragment fragment = new MainBookListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();

        mBookCategory = new BookCategory();
        if (mActivity instanceof MainActivity) {
            if (((MainActivity) mActivity).isFirstLaunch()) {
                ArrayList<BookCategory.CategoryItem> list = mBookCategory.getDefault_category_list();
                DBHandler.saveBookCategory(mActivity, list);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booklist_fragment = inflater.inflate(R.layout.booklist_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        Toolbar main_toolbar = (Toolbar) booklist_fragment.findViewById(R.id.main_toolbar);
        if (main_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(main_toolbar);
            main_toolbar.setNavigationIcon(R.drawable.ic_drawer_white);
            main_toolbar.setTitleTextColor(Color.WHITE);
            main_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            setHasOptionsMenu(true);
        }

        List<View> viewList = new ArrayList<View>();
        View booklist_gridview = inflater.inflate(R.layout.booklist_gridview, null);
        View booklist_listview = inflater.inflate(R.layout.booklist_listview, null);
        viewList.add(booklist_gridview);
        viewList.add(booklist_listview);

        bookListViewPager = (ViewPager) booklist_fragment.findViewById(R.id.bookListPager);
        pagerAdapter = new BookListViewPagerAdapter(mActivity, viewList);
        bookListViewPager.setAdapter(pagerAdapter);

        gridListView = (BookListGridListView) booklist_gridview.findViewById(R.id.booklist_grid);
        mGridListViewAdapter = new BookListGridListViewAdapter(mActivity, mListener);
        gridListView.setAdapter(mGridListViewAdapter);
        gridListView.setListViewListener(new ListViewListener() {
            @Override
            public void onRefresh() {
                //Execute a syncTask to Refresh
                refreshBookList();
            }

            @Override
            public void onLoadMore() {

            }
        });

        dbHandler = new DBHandler(mGridListViewAdapter);

        return booklist_fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("书名/作者");

        //searchView.setIconifiedByDefault(false);
        //searchView.setSubmitButtonEnabled(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshBookList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRefreshBookList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    synchronized public void stopRefreshBookList() {
        ArrayList<DBHandler.LoaderItem> loaders = dbHandler.getLoaders();
        for (DBHandler.LoaderItem item : loaders) {
            if (item.loader != null) {
                item.loader.unregisterListener(item.listener);
                item.loader.reset();
                item.listener = null;
            }
        }
        dbHandler.reset();
        mGridListViewAdapter.reset();
    }

    public void refreshBookList() {
        String selection = null;
        stopRefreshBookList();
        ArrayList<BookCategory.CategoryItem> categoryList = mBookCategory.getDefault_category_list();

        for (BookCategory.CategoryItem item : categoryList) {
            if (item.category_code != 'a') {//if category_code is 'a', that means all, it not need selection
                selection = DB_Column.BookInfo.CATEGORY_CODE
                        + "="
                        + item.category_code;
            }
            String[] projection = {DB_Column.BookInfo.ID, DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.CATEGORY_CODE};
            dbHandler.loadBookList(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC LIMIT 3");
        }
    }

    public void setListViewVerticalScrollBarEnable(boolean enable) {
        gridListView.setVerticalScrollBarEnabled(enable);
    }
}
