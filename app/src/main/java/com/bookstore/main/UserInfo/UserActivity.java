package com.bookstore.main.UserInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.bookstore.main.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/6/15.
 */
public class UserActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        AVUser currentUser = AVUser.getCurrentUser();
        String img_url = currentUser.getString("profileImageUrl");
        String account_name = currentUser.getUsername();

        ImageView userImage = (ImageView) findViewById(R.id.account_userimage);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        if (img_url != null) {
            ImageLoader.getInstance().displayImage(img_url, userImage, options);
        }

        TextView user_name = (TextView) findViewById(R.id.account_username);
        user_name.setText(account_name);

        Button logout_btn = (Button) findViewById(R.id.logout);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();
                UserActivity.this.setResult(RESULT_OK);
                UserActivity.this.finish();
            }
        });
    }
}
