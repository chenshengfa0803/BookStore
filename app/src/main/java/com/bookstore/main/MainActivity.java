package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListLoader;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.DataBaseProjection;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.main.animation.ViewBlur;
import com.bookstore.provider.BookProvider;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final static int SCANNING_REQUEST_CODE = 1;
    public FloatButton mainFloatButton;
    BookListGridListViewAdapter mGridListViewAdapter;
    View blurFromView = null;
    ImageView blurToView = null;
    BookListGridListView gridListView = null;
    private ViewPager bookListViewPager;
    private BookListViewPagerAdapter pagerAdapter;
    private BookListLoader mBookListLoader = null;
    private bookListLoadListener mLoadListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //沉浸式状态栏 Immersive ActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            //SystemBarTintManager is openSource repository from github (https://github.com/jgilfelt/SystemBarTint)
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            //tintManager.setStatusBarTintColor(getResources().getColor(android.R.color.background_light));
            tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
        }
        setContentView(R.layout.activity_main);
        blurFromView = findViewById(R.id.booklist_mainView);
        blurToView = (ImageView) findViewById(R.id.blur_view);
        blurToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainFloatButton != null) {
                    mainFloatButton.closeMenu();
                }
            }
        });

        List<View> viewList = new ArrayList<View>();
        View booklist_gridview = LayoutInflater.from(this).inflate(R.layout.booklist_gridview, null);
        View booklist_listview = LayoutInflater.from(this).inflate(R.layout.booklist_listview, null);
        viewList.add(booklist_gridview);
        viewList.add(booklist_listview);

        bookListViewPager = (ViewPager) findViewById(R.id.bookListPager);
        pagerAdapter = new BookListViewPagerAdapter(this, viewList);
        bookListViewPager.setAdapter(pagerAdapter);

        gridListView = (BookListGridListView) booklist_gridview.findViewById(R.id.booklist_grid);
        mGridListViewAdapter = new BookListGridListViewAdapter(this);
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

        createFloatButtonMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNING_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    //Bundle bundle = data.getExtras();
                    //String isbn = bundle.getString("result");
                    //getBookInfo(isbn);
                    refreshBookList();
                }
            }
            break;
        }
    }

    public void createFloatButtonMenu() {
        SubFloatButton subFab_camera = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_camera), null);
        SubFloatButton subFab_chat = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_chat), null);
        SubFloatButton subFab_location = new SubFloatButton(this, getResources().getDrawable(R.drawable.sub_floatbutton_location), null);
        subFab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ScanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNING_REQUEST_CODE);
                mainFloatButton.closeMenu();
            }
        });
        int startAngle = 270;//270 degree
        int endAngle = 360;//360 degree
        int menu_radio = getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
        int menu_duration = 500;//500 ms

        mainFloatButton = (FloatButton) findViewById(R.id.FloatActionButton);
        mainFloatButton.addSubFloatButton(subFab_camera)
                .addSubFloatButton(subFab_chat)
                .addSubFloatButton(subFab_location)
                .createFloatButtonMenu(startAngle, endAngle, menu_radio, menu_duration);
        mainFloatButton.addMenuStateListener(new FloatButton.MenuStateListener() {
            @Override
            public void onMenuOpened(FloatButton fb) {
                makeBlurWindow();
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
                disappearBlurWindow();
            }
        });
    }

    public void makeBlurWindow() {
        gridListView.setVerticalScrollBarEnabled(false);
        blurToView.setImageBitmap(null);
        blurToView.setVisibility(View.VISIBLE);
        ViewBlur.blur(blurFromView, blurToView, 2, 40);
        blurFromView.setVisibility(View.INVISIBLE);
    }

    public void disappearBlurWindow() {
        blurFromView.setVisibility(View.VISIBLE);
        blurToView.setVisibility(View.INVISIBLE);
        blurToView.setImageBitmap(null);
    }

    synchronized public void stopRefreshBookList() {
        if (mBookListLoader != null) {
            mBookListLoader.unregisterListener(mLoadListener);
            mBookListLoader.reset();
            mLoadListener = null;
        }
    }

    public void refreshBookList() {
        stopRefreshBookList();
        //final String[] projection = new String[]{DB_Column.TITLE, DB_Column.AUTHOR};
        mBookListLoader = new BookListLoader(this, BookProvider.CONTENT_URI, null, null, null, null);
        mLoadListener = new bookListLoadListener();
        mBookListLoader.registerListener(0, mLoadListener);
        mBookListLoader.startLoading();
    }

    public class bookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public bookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            Log.i("BookListLoader", "load complete");
            if (data == null || data.getCount() == 0) {
                return;
            }
            mGridListViewAdapter.registerDataCursor(data);
            data.moveToFirst();
            String book_title = data.getString(DataBaseProjection.COLUMN_TITLE);
            String book_author = data.getString(DataBaseProjection.COLUMN_AUTHOR);
            int pages = data.getInt(DataBaseProjection.COLUMN_PAGES);
            int p = pages;
            //remember close the cursor after using it
        }
    }
}
