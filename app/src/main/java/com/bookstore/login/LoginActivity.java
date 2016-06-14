package com.bookstore.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SignUpCallback;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.main.sina.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/4.
 */
public class LoginActivity extends Activity {
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Oauth2AccessToken mAccessToken;
    private EditText userName_text;
    private EditText pwd_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        userName_text = (EditText) findViewById(R.id.account_username_text);
        pwd_text = (EditText) findViewById(R.id.account_password_text);

        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);

        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AVUser user = new AVUser();

                user.setUsername(userName_text.getText().toString().trim());
                user.setPassword(pwd_text.getText().toString().trim());
                user.setEmail(userName_text.getText().toString().trim());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            AVUser.logOut();
                            Toast.makeText(LoginActivity.this, "注册成功，请登录邮箱进行验证", Toast.LENGTH_SHORT).show();
                        } else {
                            int error_code = e.getCode();
                            if (203 == error_code) {
                                Toast.makeText(LoginActivity.this, "该邮箱已被注册，请直接登录", Toast.LENGTH_SHORT).show();
                            } else if (125 == error_code) {
                                Toast.makeText(LoginActivity.this, "邮箱地址无效", Toast.LENGTH_SHORT).show();
                            } else if (218 == error_code) {
                                Toast.makeText(LoginActivity.this, "密码无效，不允许空白密码", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "注册失败 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = userName_text.getText().toString().trim();
                String pwd = pwd_text.getText().toString().trim();
                if (user_name.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "邮箱地址无效", Toast.LENGTH_SHORT).show();
                    return;
                }
                AVUser.logInInBackground(user_name, pwd, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            StarMainActivity();
                        } else {
                            int error_code = e.getCode();
                            if (error_code == 216) {
                                Toast.makeText(LoginActivity.this, "未验证的邮箱地址", Toast.LENGTH_SHORT).show();
                            } else if (error_code == 211) {
                                Toast.makeText(LoginActivity.this, "用户未注册", Toast.LENGTH_SHORT).show();
                            } else if (error_code == 210) {
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            e.printStackTrace();

                        }
                    }
                });
            }
        });

        findViewById(R.id.login_with_sina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        String accessToken = mAccessToken.getToken();
                        String expiredAt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(mAccessToken.getExpiresTime()));
                        AVUser.AVThirdPartyUserAuth userAuth = new AVUser.AVThirdPartyUserAuth(accessToken, expiredAt, "sina", mAccessToken.getUid());
                        AVUser.loginWithAuthData(userAuth, new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                if (e == null) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    StarMainActivity();
                                } else {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        findViewById(R.id.reset_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_addr = userName_text.getText().toString().trim();
                AVUser.requestPasswordResetInBackground(email_addr, new RequestPasswordResetCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, "已发送邮件，请登录邮箱重设密码", Toast.LENGTH_SHORT).show();
                        } else {
                            int error_code = e.getCode();
                            if (error_code == 204) {
                                Toast.makeText(LoginActivity.this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
                            } else if (error_code == 205) {
                                Toast.makeText(LoginActivity.this, "该邮箱未注册", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "重设失败 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void StarMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
