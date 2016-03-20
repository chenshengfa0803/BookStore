package com.bookstore.qr_codescan;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.connection.BookInfoRequestBase;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.douban.DoubanBookInfoUrl;
import com.bookstore.main.R;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.controller.IDanmakuView;
import com.bookstore.qr_codescan.danmakuFlame.master.flame.danmaku.danmaku.model.BaseDanmaku;
import com.bookstore.qr_codescan.zxing.camera.CameraManager;
import com.bookstore.qr_codescan.zxing.decoding.CaptureActivityHandler;
import com.bookstore.qr_codescan.zxing.decoding.InactivityTimer;
import com.bookstore.qr_codescan.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class ScanActivity extends Activity implements Callback {

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private Danmu mDanmu;
    private IDanmakuView danmu_View;

    private HashMap<Bitmap, BookData> scanBookMap = new HashMap<Bitmap, BookData>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        danmu_View = (IDanmakuView) findViewById(R.id.danmu_view);

        mDanmu = new Danmu(this);
        mDanmu.initDanmuView(danmu_View);

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        mDanmu.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

        mDanmu.pause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        mDanmu.destroy();
        super.onDestroy();
    }

    /**
     * ����ɨ����
     *
     * @param result
     */
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
//        if (resultString.equals("")) {
//            Toast.makeText(ScanActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
//        } else {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putString("result", resultString);
//            resultIntent.putExtras(bundle);
//            this.setResult(RESULT_OK, resultIntent);
//        }
        //ScanActivity.this.finish();
        //mDanmu.addDanmuText(false, resultString);
        getBookData(resultString);
        Handler restartHandler = new Handler();
        restartHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (handler != null) {
                    handler.restartPreviewAndDecode();
                }
            }
        }, 3000);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public void getBookData(final String isbn) {
        if (isbn == null) {
            Log.i("BookStore", "isbn is null");
            return;
        }
        Log.i("BookStore", "isbn is " + isbn);
        DoubanBookInfoUrl doubanBookUrl = new DoubanBookInfoUrl(isbn);
        BookInfoRequestBase bookRequest = new BookInfoRequestBase(doubanBookUrl) {
            @Override
            protected void requestPreExecute() {

            }

            @Override
            protected void requestPostExecute(String bookInfo) {
                try {
                    BookData bookData = BookInfoJsonParser.getInstance().getSimpleBookDataFromString(bookInfo);
                    Bitmap bitmap = ((BitmapDrawable) (getResources().getDrawable(R.drawable.downloading_smallcover))).getBitmap();
                    mDanmu.addDanmuWithTextAndImage(false, bookData);
                    //LoadBookSmallImage(bookData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bookRequest.requestExcute(BookInfoUrlBase.REQ_ISBN);
    }

    public void LoadBookSmallImage(final BookData bookData, final BaseDanmaku danmaku) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .build();
        ImageLoader.getInstance().loadImage(bookData.images_small, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                BitmapDrawable drawable = new BitmapDrawable(loadedImage);
                if (drawable != null) {
                    drawable.setBounds(0, 0, 100, 100);
                    SpannableStringBuilder spannable = mDanmu.createSpannable(drawable, bookData.title);
                    danmaku.text = spannable;
                    danmu_View.invalidateDanmaku(danmaku, false);
                }
            }
        });
    }
}