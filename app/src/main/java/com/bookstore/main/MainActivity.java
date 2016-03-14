package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListLoader;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.DataBaseProjection;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.connection.BookInfoRequestBase;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.douban.DoubanBookInfoUrl;
import com.bookstore.main.animation.Blur;
import com.bookstore.main.animation.ViewBlur;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.SystemBarTintManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private final static int SCANNING_REQUEST_CODE = 1;
    public FloatButton mainFloatButton;
    BookListGridListViewAdapter mGridListViewAdapter;
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

        List<View> viewList = new ArrayList<View>();
        View booklist_gridview = LayoutInflater.from(this).inflate(R.layout.booklist_gridview, null);
        View booklist_listview = LayoutInflater.from(this).inflate(R.layout.booklist_listview, null);
        viewList.add(booklist_gridview);
        viewList.add(booklist_listview);

        bookListViewPager = (ViewPager) findViewById(R.id.bookListPager);
        pagerAdapter = new BookListViewPagerAdapter(this, viewList);
        bookListViewPager.setAdapter(pagerAdapter);

        BookListGridListView gridListView = (BookListGridListView) booklist_gridview.findViewById(R.id.booklist_grid);
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
                    Bundle bundle = data.getExtras();
                    String isbn = bundle.getString("result");
                    getBookInfo(isbn);
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
                makeBlurWindow2();
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
            }
        });
    }

    public void makeBlurWindow1() {
        ImageView blurView = new ImageView(this);
        View fromView = findViewById(R.id.booklist_mainView);
        fromView.buildDrawingCache();
        Bitmap bitmapFromView = fromView.getDrawingCache();
        blurView.setImageBitmap(Blur.fastblur(this, bitmapFromView, 12));
        float alpha = 1.0f;
        blurView.setAlpha(1.0F);
        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // 全屏显示特征
                PixelFormat.TRANSPARENT);
        mWindowManager.addView(blurView, mLayoutParams);
        blurView.setVisibility(View.VISIBLE);
    }

    public void makeBlurWindow2() {
        View fromView = findViewById(R.id.booklist_mainView);
        ImageView toView = (ImageView) findViewById(R.id.blur_view);
        ViewBlur.blur(fromView, toView, 2, 8);
    }

    public void getBookInfo(final String isbn) {
        if (isbn == null) {
            Log.i("BookStore", "isbn is null");
            return;
        }
        Log.i("BookStore", "isbn is " + isbn);
        DoubanBookInfoUrl doubanBookUrl = new DoubanBookInfoUrl(isbn);
        BookInfoRequestBase bookRequest = new BookInfoRequestBase(doubanBookUrl) {
            @Override
            protected void requestPreExecute() {

            }

            @Override
            protected void requestPostExecute(String bookInfo) {
                try {
                    BookData bookData = BookInfoJsonParser.getInstance().getSimpleBookDataFromString(bookInfo);
                    insertBookDataToDB(bookData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bookRequest.requestExcute(BookInfoUrlBase.REQ_ISBN);
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

    public void insertBookDataToDB(final BookData bookData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DB_Column.TITLE, bookData.title);
                contentValues.put(DB_Column.AUTHOR, bookData.authors.get(0));
                contentValues.put(DB_Column.TRANSLATOR, "HAHA");
                contentValues.put(DB_Column.PUB_DATE, bookData.pub_date);
                contentValues.put(DB_Column.PUBLISHER, bookData.publisher);
                contentValues.put(DB_Column.PRICE, bookData.price);
                contentValues.put(DB_Column.PAGES, bookData.pages);
                contentValues.put(DB_Column.BINGDING, bookData.binding);
                contentValues.put(DB_Column.IMG_SMALL, bookData.images_small);
                contentValues.put(DB_Column.IMG_MEDIUM, bookData.images_medium);
                contentValues.put(DB_Column.IMG_LARGE, bookData.images_large);
                contentValues.put(DB_Column.ISBN10, bookData.isbn10);
                contentValues.put(DB_Column.ISBN13, bookData.isbn13);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = sDateFormat.format(new Date());
                contentValues.put(DB_Column.ADD_DATE, date);
                try {
                    getContentResolver().insert(BookProvider.CONTENT_URI, contentValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class bookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public bookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            Log.i("BookListLoader", "load complete");
            if (data == null) {
                return;
            }
            mGridListViewAdapter.registerDataCursor(data);
            data.moveToFirst();
            String book_title = data.getString(DataBaseProjection.COLUMN_TITLE);
            String book_author = data.getString(DataBaseProjection.COLUMN_AUTHOR);
            int pages = data.getInt(DataBaseProjection.COLUMN_PAGES);
            int p = pages;
        }
    }
}
