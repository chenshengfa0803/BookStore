package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bookstore.main.animation.ViewBlur;
import com.bookstore.main.residemenu.ResideMenu;
import com.bookstore.main.residemenu.ResideMenuItem;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.SystemBarTintManager;

public class MainActivity extends AppCompatActivity {
    public static final String PREFERENCE_FILE_NAME = "config_preference";
    public static final int MSG_GET_BOOK_CATEGORY = 100;
    private final static int SCANNING_REQUEST_CODE = 1;
    public FloatButton mainFloatButton;

    View blurFromView = null;
    ImageView blurToView = null;

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemCalendar;
    private ResideMenuItem itemSettings;

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

        MainBookListFragment bookListFragment = MainBookListFragment.newInstance();
        String tag = MainBookListFragment.class.getSimpleName();
        getSupportFragmentManager().beginTransaction().add(R.id.container_view, bookListFragment, tag).commit();

        blurFromView = findViewById(R.id.container_view);
        blurToView = (ImageView) findViewById(R.id.blur_view);
        blurToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainFloatButton != null) {
                    mainFloatButton.closeMenu();
                }
            }
        });

        setUpResideMenu();
        createFloatButtonMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshBookList();
//                handler.postDelayed(this, 5000);
//            }
//        }, 5000);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mainFloatButton.isMenuOpened()) {
            mainFloatButton.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
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
                    //refreshBookList();
                }
            }
            break;
        }
    }

    public boolean isFirstLaunch() {
        final SharedPreferences mSharedPreferences = getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = mSharedPreferences.getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }
        return isFirstLaunch;
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
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MainBookListFragment.class.getSimpleName());
        if (fragment != null) {
            ((MainBookListFragment) fragment).setListViewVerticalScrollBarEnable(false);//do not show scroll bar
        }
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

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(R.anim.category_fragment_right_in, R.anim.category_fragment_left_out, R.anim.category_fragment_left_in, R.anim.category_fragment_right_out)
                .hide(getSupportFragmentManager().findFragmentByTag(MainBookListFragment.class.getSimpleName()))
                .addToBackStack(null)
                .add(R.id.container_view, fragment, tag)
                .commit();
    }

    private void setUpResideMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.8f);

        // create menu items;
        itemProfile = new ResideMenuItem(this, R.drawable.icon_profile, "账号");
        itemHome = new ResideMenuItem(this, R.drawable.icon_home, "图书世界");
        itemCalendar = new ResideMenuItem(this, R.drawable.icon_calendar, "深度好文");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "设置");

        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }


}
