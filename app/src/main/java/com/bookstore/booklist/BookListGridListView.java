package com.bookstore.booklist;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BookListGridListView extends ListView implements AbsListView.OnScrollListener, ListViewHeader.IStateChangedListener {
    private final static float OFFSET_RADIO = 1.8f;
    private final static int SCROLL_DURATION = 400; // scroll back duration
    private ListView mGridList;
    private ListViewListener mListViewListener;
    private float mLastY = -1;
    private ListViewHeader mHeaderView;
    private Scroller mScroller;
    private boolean isTouchingScreen = false;

    public BookListGridListView(Context context) {
        super(context);
        initGridView(context);
    }

    public BookListGridListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGridView(context);
    }

    public BookListGridListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGridView(context);
    }

    public void initGridView(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        mHeaderView = new ListViewHeader(context);
        mHeaderView.setStateChangedListener(this);
        addHeaderView(mHeaderView);
    }

    private void updateHeaderViewHeight(float delta) {
        int newHeight = (int) delta + mHeaderView.getVisibleHeight();
        updateHeaderViewHeight(newHeight);
    }

    private void updateHeaderViewHeight(int height) {
/*        if (mHeaderView.getCurrentState() == ListViewHeader.STATE.NORMAL_STATE && height >= mHeaderView.getStretchHeight()) {
            mHeaderView.updateState(ListViewHeader.STATE.STRETCH_STATE);
        } else if (mHeaderView.getCurrentState() == ListViewHeader.STATE.STRETCH_STATE && height >= mHeaderView.getReadyHeight()) {
            mHeaderView.updateState(ListViewHeader.STATE.READY_STATE);
        } else if (mHeaderView.getCurrentState() == ListViewHeader.STATE.STRETCH_STATE && height < mHeaderView.getStretchHeight()) {
            mHeaderView.updateState(ListViewHeader.STATE.NORMAL_STATE);
        } else if (mHeaderView.getCurrentState() == ListViewHeader.STATE.END_STATE && height < 2) {
            mHeaderView.updateState(ListViewHeader.STATE.NORMAL_STATE);
        }*/
        mHeaderView.setVisibleHeight(height);
    }

    private void resetHeaderViewHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            return;
        }
        int finalHeight = 0;
        if ((mHeaderView.getCurrentState() == ListViewHeader.STATE.REFRESHING_STATE || mHeaderView.getCurrentState() == ListViewHeader.STATE.READY_STATE)
                && height > mHeaderView.getStretchHeight()) {
            finalHeight = mHeaderView.getStretchHeight();
        }
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        invalidate();//will call computeScroll
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                isTouchingScreen = true;
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                    updateHeaderViewHeight(deltaY / OFFSET_RADIO);
                }
                setVerticalScrollBarEnabled(true);
                break;
            //case MotionEvent.ACTION_UP:
            default:
                mLastY = -1;
                isTouchingScreen = false;
                if (getFirstVisiblePosition() == 0) {
                    resetHeaderViewHeight();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            updateHeaderViewHeight(mScroller.getCurrY());
        }
        postInvalidate();
        super.computeScroll();
    }



    public ListView getGridList() {
        return mGridList;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void notifyStateChanged(ListViewHeader.STATE oldState, ListViewHeader.STATE newState) {
        if (newState == ListViewHeader.STATE.REFRESHING_STATE) {
            if (mListViewListener != null) {
                mListViewListener.onRefresh();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopRefreshAnimation();
                    }
                }, 2000);
            }
        }
    }

    public void stopRefreshAnimation() {
        if (mHeaderView.getCurrentState() == ListViewHeader.STATE.REFRESHING_STATE) {
            mHeaderView.updateState(ListViewHeader.STATE.END_STATE);
            if (!isTouchingScreen) {
                resetHeaderViewHeight();
            }
        } else {
            throw new IllegalStateException("can not stop refresh while it is not refreshing!");
        }
    }

    public void setListViewListener(ListViewListener listener) {
        mListViewListener = listener;
    }
}
