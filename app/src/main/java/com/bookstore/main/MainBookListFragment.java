package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bookstore.bookdetail.BookDetailFragment;
import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.main.SearchView.SearchAdapter;
import com.bookstore.main.SearchView.SearchCloudItem;
import com.bookstore.main.SearchView.SearchView;
import com.bookstore.main.animation.BookDetailTransition;
import com.bookstore.main.residemenu.ResideMenu;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.BookSQLiteOpenHelper;
import com.bookstore.provider.DB_Column;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.BitmapUtil;
import com.bookstore.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class MainBookListFragment extends Fragment {
    private final static int SCANNING_REQUEST_CODE = 1;
    public static int queryCompleteTimes = 0;

    public DBHandler dbHandler = null;
    public BookCategory mBookCategory = null;
    Toolbar main_toolbar = null;
    private Activity mActivity;
    private View booklist_fragment = null;
    private ViewPager bookListViewPager;
    private BookListViewPagerAdapter pagerAdapter;
    private BookListGridListView gridListView = null;
    private BookListGridListViewAdapter mGridListViewAdapter;
    private SearchView mSearchView = null;

    private BookOnClickListener mListener = new BookOnClickListener() {
        @Override
        public void onBookClick(View clickedImageView, String objectId, int category_code) {
            Bitmap bitmap = null;
            int paletteColor = getResources().getColor(android.R.color.darker_gray);
            BitmapDrawable bd = (BitmapDrawable) ((ImageView) clickedImageView).getDrawable();
            if (bd != null) {
                bitmap = bd.getBitmap();
                paletteColor = BitmapUtil.getPaletteColor(bitmap);
            }
            BookDetailFragment detailFragment = BookDetailFragment.newInstance(objectId, category_code, paletteColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                detailFragment.setSharedElementEnterTransition(new BookDetailTransition());
                setExitTransition(new Fade());
                detailFragment.setEnterTransition(new Fade());
                detailFragment.setSharedElementReturnTransition(new BookDetailTransition());
            }

            FragmentManager fragmentManager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addSharedElement(clickedImageView, getResources().getString(R.string.image_transition))
                    .add(R.id.container_view, detailFragment, BookDetailFragment.class.getSimpleName())
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        booklist_fragment = inflater.inflate(R.layout.booklist_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        main_toolbar = (Toolbar) booklist_fragment.findViewById(R.id.main_toolbar);
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
            TextView title_middle = (TextView) main_toolbar.findViewById(R.id.toolbar_middle_title);
            title_middle.setVisibility(View.VISIBLE);
            title_middle.setText(getResources().getString(R.string.app_name));
            title_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gridListView != null) {
                        gridListView.smoothScrollToPosition(0);
                    }
                }
            });
            setHasOptionsMenu(true);
            ((MainActivity) mActivity).getResideMenu().addIgnoredView(main_toolbar);
        }

        List<View> viewList = new ArrayList<View>();
        View booklist_gridview = inflater.inflate(R.layout.booklist_gridview, null);
        View booklist_listview = inflater.inflate(R.layout.booklist_listview, null);
        viewList.add(booklist_gridview);
        viewList.add(booklist_listview);
        ((MainActivity) mActivity).getResideMenu().addIgnoredView(booklist_listview);

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
                loadBookListFromCloud();
            }

            @Override
            public void onLoadMore() {

            }
        });
        mSearchView = (SearchView) booklist_fragment.findViewById(R.id.searchView);
        mSearchView.registerSearchViewStateListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
                mainFloatButton.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
                mainFloatButton.setVisibility(View.VISIBLE);
            }
        });
        final SearchAdapter adapter = new SearchAdapter(mActivity);
        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mSearchView.hide();
                List<SearchCloudItem> list = adapter.getSearchList();
                SearchCloudItem item = list.get(position);
                ImageView book_cover = (ImageView) view.findViewById(R.id.search_item_bookcover);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    book_cover.setTransitionName("item" + position + "small_cover");
                }
                String objectId = item.getObjectId();
                int category_code = item.getCategory_code();
                if (mListener != null) {
                    mListener.onBookClick(book_cover, objectId, category_code);
                }
                DBHandler.addSearchHistory(mActivity, item.getBook_title());
            }
        });
        mSearchView.setAdapter(adapter);
        ((MainActivity) mActivity).getResideMenu().addIgnoredView(mSearchView);

        dbHandler = new DBHandler(mGridListViewAdapter);

        floatButtonLoadAnimation();

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
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //searchView.setQueryHint("书名/作者");

        //searchView.setIconifiedByDefault(false);
        //searchView.setSubmitButtonEnabled(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showSearchView() {
        mSearchView.show();
    }

    public void hideSearchView() {
        mSearchView.hide();
    }

    public boolean isSearchOpened() {
        return mSearchView.isSearchOpen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Toast.makeText(mActivity, "慢工出细活，未实现", Toast.LENGTH_SHORT).show();
                //MyDialogFragment dialog = new MyDialogFragment();
                //dialog.show(mActivity.getFragmentManager(), "myDialogFragment");
                ((MainActivity) mActivity).getResideMenu().openMenu(ResideMenu.DIRECTION_LEFT);
                break;
            case R.id.menu_search:
                showSearchView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (main_toolbar != null) {
            main_toolbar.setTitle("");
        }
        //refreshBookList();
        loadBookListFromCloud();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setExitTransition(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            updateFloatButton();
            //refreshBookList();
            loadBookListFromCloud();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        //stopRefreshBookList();
        stopLoadBookListFromCloud();
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

    synchronized public void stopLoadBookListFromCloud() {
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

    public void loadBookListFromCloud() {
        stopLoadBookListFromCloud();
        loadAllFromCloud();
        loadEachCategoryFromCloud();
    }

    public void loadAllFromCloud() {
        AVQuery<AVObject> query = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
        query.whereEqualTo("userId", MainActivity.getCurrentUserId());
        query.limit(3);
        query.selectKeys(Arrays.asList("objectId", DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.CATEGORY_CODE));
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mGridListViewAdapter.registerCloudData('a', list);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void loadEachCategoryFromCloud() {
        final ArrayList<BookCategory.CategoryItem> categoryList = mBookCategory.getDefault_category_list();
        for (BookCategory.CategoryItem item : categoryList) {
            if (item.category_code != 'a') {
                AVQuery<AVObject> userQuery = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
                userQuery.whereEqualTo("userId", MainActivity.getCurrentUserId());

                AVQuery<AVObject> categoryQuery = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
                categoryQuery.whereEqualTo(DB_Column.BookInfo.CATEGORY_CODE, item.category_code);
                AVQuery<AVObject> query = AVQuery.and(Arrays.asList(userQuery, categoryQuery));

                query.limit(3);
                query.selectKeys(Arrays.asList("objectId", DB_Column.BookInfo.ID, DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.CATEGORY_CODE));
                query.orderByDescending("updatedAt");

                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        queryCompleteTimes++;
                        if (e == null) {
                            if (list.size() > 0) {
                                int category_code = list.get(0).getInt(DB_Column.BookInfo.CATEGORY_CODE);
                                mGridListViewAdapter.registerCloudData(category_code, list);
                            }
                        } else {
                            e.printStackTrace();
                        }
                        if (queryCompleteTimes == categoryList.size() - 1) {
                            updateFloatButton();
                            mGridListViewAdapter.buildAdapterList();
                            mGridListViewAdapter.notifyDataSetChanged();
                            queryCompleteTimes = 0;
                        }
                    }
                });
            }
        }
    }

    public void setListViewVerticalScrollBarEnable(boolean enable) {
        gridListView.setVerticalScrollBarEnabled(enable);
    }

    private void floatButtonLoadAnimation() {
        final FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black));
        mainFloatButton.setFloatButtonIcon(imageView);
        mainFloatButton.registerClickListener(new FloatButton.FloatButtonClickListener() {
            @Override
            public void onFloatButtonClick(View floatButton) {

            }
        });
        mainFloatButton.setEnabled(false);

        mainFloatButton.getContentView().setRotation(0);
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 360);
        ObjectAnimator loadAnimator = ObjectAnimator.ofPropertyValuesHolder(mainFloatButton.getContentView(), rotation);
        loadAnimator.setRepeatMode(ValueAnimator.RESTART);
        loadAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadAnimator.setDuration(1000);
        loadAnimator.start();
    }

    public void updateFloatButton() {
        final FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();

        ImageView imageView = new ImageView(mActivity);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.main_floatbutton_add));
        mainFloatButton.setFloatButtonIcon(imageView);

        int size = getResources().getDimensionPixelSize(R.dimen.sub_float_button_icon_size);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.sub_float_button_margin_left);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.sub_float_button_margin_top);
        SubFloatButton subFab_camera = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_camera), params);
        SubFloatButton subFab_chat = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_chat), params);
        SubFloatButton subFab_location = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.sub_floatbutton_location), params);
        subFab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mActivity, ScanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNING_REQUEST_CODE);
                mainFloatButton.closeMenu();
            }
        });
        int startAngle = 270;//270 degree
        int endAngle = 360;//360 degree
        int menu_radio = getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
        int menu_duration = 500;//500 ms

        mainFloatButton.createFloatButtonMenu(startAngle, endAngle, menu_radio, menu_duration)
                .addSubFloatButton(subFab_camera)
                .addSubFloatButton(subFab_chat)
                .addSubFloatButton(subFab_location);

        mainFloatButton.addMenuStateListener(new FloatButton.MenuStateListener() {
            @Override
            public void onMenuOpened(FloatButton fb) {
                ((MainActivity) mActivity).makeBlurWindow();
                fb.getContentView().setRotation(0);
                PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), rotation);
                animator.start();
            }

            @Override
            public void onMenuClosed(FloatButton fb) {
                fb.getContentView().setRotation(45);
                PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), rotation);
                animator.start();
                ((MainActivity) mActivity).disappearBlurWindow();
            }
        });

        mainFloatButton.registerClickListener(new FloatButton.FloatButtonClickListener() {
            @Override
            public void onFloatButtonClick(View floatButton) {
                if (mainFloatButton.isMenuOpened()) {
                    mainFloatButton.closeMenu();
                } else {
                    mainFloatButton.openMenu();
                }
            }
        });
    }
}
