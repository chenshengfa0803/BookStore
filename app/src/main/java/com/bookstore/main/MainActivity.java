package com.bookstore.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bookstore.main.animation.ViewBlur;
import com.bookstore.qr_codescan.ScanActivity;
import com.bookstore.util.SystemBarTintManager;

public class MainActivity extends Activity {
    public static final String PREFERENCE_FILE_NAME = "config_preference";
    public static final int MSG_GET_BOOK_CATEGORY = 100;
    private final static int SCANNING_REQUEST_CODE = 1;
    public FloatButton mainFloatButton;

    View blurFromView = null;
    ImageView blurToView = null;

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
        getFragmentManager().beginTransaction().replace(R.id.container_view, bookListFragment, tag).commit();

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
        Fragment fragment = getFragmentManager().findFragmentByTag(MainBookListFragment.class.getSimpleName());
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
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.category_fragment_right_in, R.anim.category_fragment_left_out, R.anim.category_fragment_left_in, R.anim.category_fragment_right_out)
                .replace(R.id.container_view, fragment, tag)
                .addToBackStack(null)
                .commit();
    }


}
