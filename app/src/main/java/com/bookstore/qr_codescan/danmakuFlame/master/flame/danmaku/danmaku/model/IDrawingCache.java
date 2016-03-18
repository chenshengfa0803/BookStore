
package com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model;

public interface IDrawingCache<T> {

    void build(int w, int h, int density, boolean checkSizeEquals);

    void erase();

    T get();

    void destroy();

    int size();

    int width();

    int height();

    boolean hasReferences();

    void increaseReference();

    void decreaseReference();

}
