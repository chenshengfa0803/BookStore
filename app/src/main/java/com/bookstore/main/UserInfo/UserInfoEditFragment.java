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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bookstore.main.R;
import com.bookstore.util.SystemBarTintManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/6/18.
 */
public class UserInfoEditFragment extends Fragment {
    public static final String ARG_IMG_URL = "imgUrl";
    public static final String ARG_NICK_NAME = "nickname";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_SIGN = "sign";

    private Activity mActivity;
    private View userinfo_edit_fragment = null;
    private Toolbar userinfo_edit_toolbar = null;
    private ScrollView userinfo_edit_scroll = null;
    private ImageView userImg = null;
    private View userNameContainer = null;
    private View userSignContainer = null;
    private TextView userNameTextView = null;
    private TextView userSignTextView = null;
    private TextView userLocationTextView = null;

    private String user_img_url = null;
    private String user_nick_name = null;
    private String user_location = null;
    private String user_sign = null;
    private UserInfoListener userInfoListener = new UserInfoListener() {
        @Override
        public void onNickNameChange(String nickName) {
            userNameTextView.setText(nickName);
        }

        @Override
        public void onSignChange(String sign) {
            userSignTextView.setText(sign);
        }
    };

    public static UserInfoEditFragment newInstance(Bundle arg) {
        UserInfoEditFragment fragment = new UserInfoEditFragment();
        Bundle args = arg;
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        Bundle arg = getArguments();
        user_img_url = arg.getString(ARG_IMG_URL);
        user_nick_name = arg.getString(ARG_NICK_NAME);
        user_location = arg.getString(ARG_LOCATION);
        user_sign = arg.getString(ARG_SIGN);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        userinfo_edit_fragment = inflater.inflate(R.layout.userinfo_edit_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        userinfo_edit_toolbar = (Toolbar) userinfo_edit_fragment.findViewById(R.id.userinfo_edit_toolbar);
        if (userinfo_edit_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(userinfo_edit_toolbar);
            userinfo_edit_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            userinfo_edit_toolbar.setTitleTextColor(Color.WHITE);
            userinfo_edit_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            userinfo_edit_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
            TextView title_middle = (TextView) userinfo_edit_toolbar.findViewById(R.id.toolbar_middle_title);
            title_middle.setVisibility(View.VISIBLE);
            title_middle.setText(getResources().getString(R.string.personal_info_edit));
            title_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userinfo_edit_scroll != null) {
                        userinfo_edit_scroll.smoothScrollTo(0, 0);
                    }
                }
            });

            setHasOptionsMenu(true);
        }
        userinfo_edit_scroll = (ScrollView) userinfo_edit_fragment.findViewById(R.id.scroll_detail);

        userImg = (ImageView) userinfo_edit_fragment.findViewById(R.id.userimg_image);
        if (user_img_url != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(user_img_url, userImg, options);
        }

        userNameContainer = userinfo_edit_fragment.findViewById(R.id.username_container);
        userNameTextView = (TextView) userNameContainer.findViewById(R.id.username_text);
        if (user_nick_name != null) {
            userNameTextView.setText(user_nick_name);
        }
        userNameContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = userNameTextView.getText().toString().trim();
                Bundle arg = new Bundle();
                arg.putInt(MyDialogFragment.ARGS_DIALOG_TYPE, MyDialogFragment.DIALOG_TYPE_USERNAME);
                arg.putString("nickName", nickName);
                MyDialogFragment dialog = MyDialogFragment.newInstance(arg);
                dialog.registerUserInfoListener(userInfoListener);
                dialog.show(mActivity.getFragmentManager(), "EditUserName");
            }
        });

        userSignTextView = (TextView) userinfo_edit_fragment.findViewById(R.id.usersign_text);
        if (user_sign != null) {
            userSignTextView.setText(user_sign);
        }
        userSignContainer = userinfo_edit_fragment.findViewById(R.id.usersign_container);
        userSignContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sign = userSignTextView.getText().toString().trim();
                Bundle arg = new Bundle();
                arg.putInt(MyDialogFragment.ARGS_DIALOG_TYPE, MyDialogFragment.DIALOG_TYPE_USERSIGN);
                arg.putString("sign", sign);
                MyDialogFragment signDialog = MyDialogFragment.newInstance(arg);
                signDialog.registerUserInfoListener(userInfoListener);
                signDialog.show(mActivity.getFragmentManager(), "EditUserSign");
            }
        });

        userLocationTextView = (TextView) userinfo_edit_fragment.findViewById(R.id.userplace_text);
        if (user_location != null) {
            userLocationTextView.setText(user_location);
        }


        return userinfo_edit_fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userinfo_edit_toolbar != null) {
            userinfo_edit_toolbar.setTitle("");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface UserInfoListener {
        void onNickNameChange(String nickName);

        void onSignChange(String sign);
    }
}
