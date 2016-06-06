package com.bookstore;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Administrator on 2016/3/21.
 */
public class UIApplication extends Application {
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .writeDebugLogs();
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(getApplicationContext());
        AVOSCloud.useAVCloudCN();
        AVOSCloud.initialize(this, "XabzuOyp41S5IqFHeqYaSVGz-gzGzoHsz", "GLIqjYDRngajLO0sPW6WyfK0");

        //Test leanCloud, data will be save to leanCloud
        //AVObject testObject = new AVObject("TestObject");
        //testObject.put("foo", "bar");
        //testObject.saveInBackground();

/*
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    AVOSCloud.requestSMSCode("18922219783", "我的书架", "注册", 10);
                } catch (AVException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
*/


    }
}
