package com.bookstore.qr_codescan;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.bookstore.bookparser.BookData;
import com.bookstore.main.R;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.controller.DrawHandler;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.controller.IDanmakuView;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.ILoader;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.IllegalDataException;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.BaseDanmaku;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.DanmakuTimer;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.IDisplayer;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.android.DanmakuContext;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.android.Danmakus;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.parser.IDataSource;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/3/20.
 */
public class Danmu {
    private ScanActivity scanActivity;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;
    private BaseDanmakuParser mDanmakuParser;
    private BookData curBookData = null;
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
        @Override
        public void prepareDrawing(BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { //如果弹幕是图片+文字的，则需要更新
                scanActivity.LoadBookSmallImage(curBookData, danmaku);
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {

        }
    };

    public Danmu(ScanActivity activity) {
        scanActivity = activity;
    }

    public void initDanmuView(IDanmakuView danmu_view) {
        mDanmakuView = danmu_view;

        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter)//如果弹幕包含图片和文字，必须设置该属性，否则图片显示不出来
                        //.setCacheStuffer(new SpannedCacheStuffer(), null)//用这条语句测试加载不了图书封面时的弹幕
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmakuView != null) {
            //mDanmakuParser = createParser(this.getResources().openRawResource(R.raw.comments));
            mDanmakuParser = createParser(null);
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {

                }

                @Override
                public void drawingFinished() {

                }
            });

            mDanmakuView.prepare(mDanmakuParser, mDanmakuContext);
        }
    }

    private BaseDanmakuParser createParser(InputStream stream) {
        if (stream == null) {
            return new BaseDanmakuParser() {
                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    public void pause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    public void resume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
    }

    public void destroy() {
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    public void addDanmuText(int type, boolean islive, String text) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(type);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        danmaku.text = text;
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.isLive = islive;
        danmaku.time = mDanmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = Color.WHITE;
        danmaku.borderColor = Color.GREEN;
        mDanmakuView.addDanmaku(danmaku);
    }

    public void addDanmuWithTextAndImage(boolean islive, BookData bookData) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = scanActivity.getResources().getDrawable(R.drawable.downloading_smallcover);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable, bookData.title);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.isLive = islive;
        danmaku.time = mDanmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mDanmakuParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0;
        danmaku.underlineColor = Color.GREEN;
        curBookData = bookData;
        mDanmakuView.addDanmaku(danmaku);
    }

    public SpannableStringBuilder createSpannable(Drawable drawable, String bookTitle) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);//default is ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(bookTitle);
        //spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }
}
