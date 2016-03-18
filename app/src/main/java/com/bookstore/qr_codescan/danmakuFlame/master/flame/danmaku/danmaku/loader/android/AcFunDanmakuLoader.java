package com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.android;

import android.net.Uri;

import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.ILoader;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.loader.IllegalDataException;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.parser.android.JSONSource;

import java.io.InputStream;

/**
 * Ac danmaku loader
 *
 * @author yrom
 */
public class AcFunDanmakuLoader implements ILoader {
    private static volatile AcFunDanmakuLoader instance;
    private JSONSource dataSource;

    private AcFunDanmakuLoader() {
    }

    public static ILoader instance() {
        if (instance == null) {
            synchronized (AcFunDanmakuLoader.class) {
                if (instance == null)
                    instance = new AcFunDanmakuLoader();
            }
        }
        return instance;
    }

    @Override
    public JSONSource getDataSource() {
        return dataSource;
    }

    @Override
    public void load(String uri) throws IllegalDataException {
        try {
            dataSource = new JSONSource(Uri.parse(uri));
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }

    @Override
    public void load(InputStream in) throws IllegalDataException {
        try {
            dataSource = new JSONSource(in);
        } catch (Exception e) {
            throw new IllegalDataException(e);
        }
    }


}
