package com.bookstore.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SignUpCallback;
import com.bookstore.login.sina.SinaConstants;
import com.bookstore.login.tencent.TencentConstants;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/6/4.
 */
public class LoginActivity extends Activity {
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private Tencent mTencent;
    private UserInfo mInfo;
    private Oauth2AccessToken mAccessToken;
    private UsersAPI mUsersAPI;
    private EditText userName_text;
    private EditText pwd_text;
    private BaseUIListener baseUIListener = new BaseUIListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        userName_text = (EditText) findViewById(R.id.account_username_text);
        pwd_text = (EditText) findViewById(R.id.account_password_text);

        mAuthInfo = new AuthInfo(this, SinaConstants.APP_KEY, SinaConstants.REDIRECT_URL, SinaConstants.SCOPE);
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
                final String user_name = userName_text.getText().toString().trim();
                String pwd = pwd_text.getText().toString().trim();
                if (user_name.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "邮箱地址无效", Toast.LENGTH_SHORT).show();
                    return;
                }
                AVUser.logInInBackground(user_name, pwd, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null) {
                            avUser.put("username", user_name);
                            avUser.saveInBackground();
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

        findViewById(R.id.login_with_sina).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        mUsersAPI = new UsersAPI(LoginActivity.this, SinaConstants.APP_KEY, mAccessToken);
                        long uid = Long.parseLong(mAccessToken.getUid());
                        mUsersAPI.show(uid, new RequestListener() {
                            @Override
                            public void onComplete(String s) {
                                if (!TextUtils.isEmpty(s)) {
                                    User sinaUser = User.parse(s);
                                    if (sinaUser != null) {
                                        final String sinaUserName = sinaUser.screen_name;
                                        final String profileImageUrl = sinaUser.profile_image_url;

                                        String accessToken = mAccessToken.getToken();
                                        String expiredAt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(mAccessToken.getExpiresTime()));
                                        AVUser.AVThirdPartyUserAuth userAuth = new AVUser.AVThirdPartyUserAuth(accessToken, expiredAt, "sina", mAccessToken.getUid());
                                        AVUser.loginWithAuthData(userAuth, new LogInCallback<AVUser>() {
                                            @Override
                                            public void done(AVUser avUser, AVException e) {
                                                if (e == null) {
                                                    avUser.put("username", sinaUserName);
                                                    avUser.put("profileImageUrl", profileImageUrl);
                                                    avUser.saveInBackground();
                                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                                    StarMainActivity();
                                                } else {
                                                    e.printStackTrace();
                                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onWeiboException(WeiboException e) {
                                ErrorInfo info = ErrorInfo.parse(e.getMessage());
                                Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_LONG).show();
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

        findViewById(R.id.login_with_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTencent = Tencent.createInstance(TencentConstants.APP_ID, getApplicationContext());
                if (!mTencent.isSessionValid()) {
                    mTencent.login(LoginActivity.this, "all", baseUIListener);
                } else {
                    mTencent.logout(LoginActivity.this);
                    mTencent.login(LoginActivity.this, "all", baseUIListener);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, baseUIListener);
        }
    }

    public void StarMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private class BaseUIListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            JSONObject response = (JSONObject) o;
            //Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_LONG).show();
            try {
                String openId = response.getString(Constants.PARAM_OPEN_ID);
                String access_token = response.getString(Constants.PARAM_ACCESS_TOKEN);
                String expires_in = response.getString(Constants.PARAM_EXPIRES_IN);
                if (!TextUtils.isEmpty(access_token) && !TextUtils.isEmpty(expires_in)
                        && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(access_token, expires_in);
                    mTencent.setOpenId(openId);
                }
                final AVUser.AVThirdPartyUserAuth userAuth = new AVUser.AVThirdPartyUserAuth(access_token, expires_in, "tencent", openId);
                if (mTencent != null && mTencent.isSessionValid()) {
                    mInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
                    mInfo.getUserInfo(new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            JSONObject json = (JSONObject) o;
                            String userName = null;
                            String userIcon = null;
                            try {
                                if (json.has("figureurl_qq_2")) {
                                    userIcon = json.getString("figureurl_qq_2");
                                }
                                if (json.has("nickname")) {
                                    userName = json.getString("nickname");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            final String finalUserName = userName;
                            final String finalUserIcon = userIcon;
                            AVUser.loginWithAuthData(userAuth, new LogInCallback<AVUser>() {
                                @Override
                                public void done(AVUser avUser, AVException e) {
                                    if (e == null) {
                                        avUser.put("username", finalUserName);
                                        avUser.put("profileImageUrl", finalUserIcon);
                                        avUser.saveInBackground();
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
                        public void onError(UiError uiError) {
                            Toast.makeText(LoginActivity.this, "获取QQ用户信息失败" + uiError.errorMessage, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "登录失败" + uiError.errorMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "cancle", Toast.LENGTH_LONG).show();
        }
    }
}
