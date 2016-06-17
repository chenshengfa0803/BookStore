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

        return userinfo_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userinfo_toolbar != null) {
            userinfo_toolbar.setTitle("");
        }

        AVUser currentUser = AVUser.getCurrentUser();
        AVObject user = AVObject.createWithoutData("_User", currentUser.getObjectId());
        user.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    String img_url = avObject.getString("profileImageUrl");
                    String account_name = avObject.getString("username");

                    ImageView userImage = (ImageView) userinfo_fragment.findViewById(R.id.account_userimage);
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();
                    if (img_url != null) {
                        ImageLoader.getInstance().displayImage(img_url, userImage, options);
                    }

                    TextView user_name = (TextView) userinfo_fragment.findViewById(R.id.account_username);
                    user_name.setText(account_name);
                }
            }
        });

        Button logout_btn = (Button) userinfo_fragment.findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        });
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
}
