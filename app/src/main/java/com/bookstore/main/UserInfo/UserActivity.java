package com.bookstore.main.UserInfo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.bookstore.main.R;
import com.bookstore.util.SystemBarTintManager;

/**
 * Created by Administrator on 2016/6/15.
 */
public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            //SystemBarTintManager is openSource repository from github (https://github.com/jgilfelt/SystemBarTint)
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            //tintManager.setStatusBarTintColor(getResources().getColor(android.R.color.background_light));
            tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
        }
        setContentView(R.layout.activity_userinfo);

        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance();
        String tag = UserInfoFragment.class.getSimpleName();
        getSupportFragmentManager().beginTransaction().add(R.id.userinfo_container, userInfoFragment, tag).commit();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_out_right);
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(R.anim.category_fragment_right_in, R.anim.category_fragment_left_out, R.anim.category_fragment_left_in, R.anim.category_fragment_right_out)
                .hide(getSupportFragmentManager().findFragmentByTag(UserInfoFragment.class.getSimpleName()))
                .addToBackStack(null)
                .add(R.id.userinfo_container, fragment, tag)
                .commit();
    }
}
