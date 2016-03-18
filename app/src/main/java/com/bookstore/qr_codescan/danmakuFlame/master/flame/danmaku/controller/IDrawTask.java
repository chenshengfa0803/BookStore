/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.controller;

import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.AbsDisplayer;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.BaseDanmaku;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.IDanmakus;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.renderer.IRenderer.RenderingState;

public interface IDrawTask {

    void addDanmaku(BaseDanmaku item);

    void removeAllDanmakus(boolean isClearDanmakusOnScreen);

    void removeAllLiveDanmakus();

    void clearDanmakusOnScreen(long currMillis);

    IDanmakus getVisibleDanmakusOnTime(long time);

    RenderingState draw(AbsDisplayer displayer);

    void reset();

    void seek(long mills);

    void start();

    void quit();

    void prepare();

    void requestClear();

    void setParser(BaseDanmakuParser parser);

    void invalidateDanmaku(BaseDanmaku item, boolean remeasure);

    void requestHide();

    interface TaskListener {
        void ready();

        void onDanmakuAdd(BaseDanmaku danmaku);

        void onDanmakuShown(BaseDanmaku danmaku);

        void onDanmakuConfigChanged();

        void onDanmakusDrawingFinished();
    }

}
