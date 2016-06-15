package com.bookstore.main.UserInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
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
        AVObject user = AVObject.createWithoutData("_User", currentUser.getObjectId());
        user.fetchInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    String img_url = avObject.getString("profileImageUrl");
                    String account_name = avObject.getString("username");

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
                }
            }
        });

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
