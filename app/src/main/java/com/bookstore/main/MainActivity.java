package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.bookstore.booklist.BookListGridListView;
import com.bookstore.booklist.BookListGridListViewAdapter;
import com.bookstore.booklist.BookListViewPagerAdapter;
import com.bookstore.booklist.ListViewListener;
import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.qr_codescan.ScanActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    public FloatButton mainFloatButton;
    private ViewPager bookListViewPager;
    private BookListViewPagerAdapter pagerAdapter;
    private MessageHandler handler = new MessageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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
        BookListGridListViewAdapter adapter = new BookListGridListViewAdapter(this);
        gridListView.setAdapter(adapter);
        gridListView.setListViewListener(new ListViewListener() {
            @Override
            public void onRefresh() {
                //Execute a syncTask to Refresh
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createFloatButtonMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Log.d("QR code", bundle.getString("result"));
                    String isbn = bundle.getString("result");
                    String urlstr = "https://api.douban.com/v2/book/isbn/:" + isbn;
                    //String urlstr = "https://api.douban.com/v2/book/isbn/:" + "9787101063981";// for test
                    getBookInfo(urlstr);
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
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
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

    public void getBookInfo(final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL realUrl = new URL(url);
                    final URLConnection connection = realUrl.openConnection();

                    String result = "";
                    String line;
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    while ((line = in.readLine()) != null) {
                        result += line;
                    }
                    if (in != null) {
                        in.close();
                    }

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("bookinfo", result);
                    msg.what = MessageHandler.MSG_GET_BOOKINFO;
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class MessageHandler extends Handler {
        private static final int MSG_GET_BOOKINFO = 1;
        private MainActivity mainActivity;

        public MessageHandler(MainActivity activity) {
            mainActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_BOOKINFO: {
                    String bookInfo = msg.getData().getString("bookinfo");
                    //EditText et = (EditText) findViewById(R.id.bookInfo);
                    //et.setText(bookInfo);
                    try {
                        BookData bookData = BookInfoJsonParser.getInstance().getSimpleBookDataFromString(bookInfo);
                        Log.i("csf", "book id is ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            }
        }
    }
}
