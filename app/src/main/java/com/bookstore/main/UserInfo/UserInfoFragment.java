package com.bookstore.main.UserInfo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.bookstore.main.R;
import com.bookstore.util.SystemBarTintManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/6/18.
 */
public class UserInfoFragment extends Fragment {
    private Activity mActivity;
    private View userinfo_fragment = null;
    private Toolbar userinfo_toolbar = null;
    private ScrollView userinfo_scroll = null;
    private String account_img_url = null;
    private String account_name = null;
    private String account_nick_name = null;
    private String account_location = null;
    private String account_sign = null;

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        userinfo_fragment = inflater.inflate(R.layout.userinfo_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        userinfo_toolbar = (Toolbar) userinfo_fragment.findViewById(R.id.userinfo_toolbar);
        if (userinfo_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(userinfo_toolbar);
            userinfo_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            userinfo_toolbar.setTitleTextColor(Color.WHITE);
            userinfo_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            userinfo_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            TextView title_middle = (TextView) userinfo_toolbar.findViewById(R.id.toolbar_middle_title);
            title_middle.setVisibility(View.VISIBLE);
            title_middle.setText(getResources().getString(R.string.personal_info));
            title_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userinfo_scroll != null) {
                        userinfo_scroll.smoothScrollTo(0, 0);
                    }
                }
            });

            setHasOptionsMenu(true);
        }
        userinfo_scroll = (ScrollView) userinfo_fragment.findViewById(R.id.scroll_detail);

        Button logout_btn = (Button) userinfo_fragment.findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        });

        return userinfo_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userinfo_toolbar != null) {
            userinfo_toolbar.setTitle("");
        }
        updateUserInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.userinfo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_userinfo:
                Bundle arg = new Bundle();
                arg.putString(UserInfoEditFragment.ARG_IMG_URL, account_img_url);
                if (account_nick_name != null) {
                    arg.putString(UserInfoEditFragment.ARG_NICK_NAME, account_nick_name);
                } else {
                    arg.putString(UserInfoEditFragment.ARG_NICK_NAME, account_name);
                }
                arg.putString(UserInfoEditFragment.ARG_LOCATION, account_location);
                arg.putString(UserInfoEditFragment.ARG_SIGN, account_sign);
                UserInfoEditFragment fragment = UserInfoEditFragment.newInstance(arg);
                String tag = UserInfoEditFragment.class.getSimpleName();
                if (mActivity instanceof UserActivity) {
                    ((UserActivity) mActivity).replaceFragment(fragment, tag);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateUserInfo() {
        AVUser currentUser = AVUser.getCurrentUser();
        final AVObject user = AVObject.createWithoutData("_User", currentUser.getObjectId());
        user.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    account_img_url = avObject.getString("profileImageUrl");
                    account_name = avObject.getString("username");
                    account_nick_name = avObject.getString("nickName");
                    account_location = avObject.getString("location");
                    account_sign = avObject.getString("userSignature");

                    ImageView userImage = (ImageView) userinfo_fragment.findViewById(R.id.account_userimage);
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    if (account_img_url != null) {
                        ImageLoader.getInstance().displayImage(account_img_url, userImage, options);
                    }

                    TextView user_name = (TextView) userinfo_fragment.findViewById(R.id.account_username);
                    if (account_nick_name != null) {
                        user_name.setText(account_nick_name);
                    } else {
                        user_name.setText(account_name);
                    }

                    TextView user_location = (TextView) userinfo_fragment.findViewById(R.id.account_location);
                    if (account_location != null) {
                        user_location.setText(account_location);
                    }

                    TextView user_signature = (TextView) userinfo_fragment.findViewById(R.id.account_sign);
                    if (account_sign != null) {
                        user_signature.setText(account_sign);
                    }
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateUserInfo();
        }
    }
}
