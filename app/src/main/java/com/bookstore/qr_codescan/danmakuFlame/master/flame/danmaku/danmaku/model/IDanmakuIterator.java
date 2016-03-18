package com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model;

public interface IDanmakuIterator {

    BaseDanmaku next();

    boolean hasNext();

    void reset();

    void remove();

}
