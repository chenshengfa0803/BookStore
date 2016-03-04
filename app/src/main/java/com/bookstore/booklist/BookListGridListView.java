package com.bookstore.booklist;

import android.content.Context;
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

    private void updateHeaderViewHeight(float delta) {
        int newHeight = (int) delta + mHeaderView.getVisibleHeight();
        updateHeaderViewHeight(newHeight);
    }

    private void updateHeaderViewHeight(int height) {
        mHeaderView.setVisibleHeight(height);
        if (mHeaderView.getCurrentState() == ListViewHeader.STATE.NORMAL_STATE && height >= mHeaderView.getStretchHeight()) {
            mHeaderView.updataState(ListViewHeader.STATE.STRETCH_STATE);
        } else if (mHeaderView.getCurrentState() == ListViewHeader.STATE.STRETCH_STATE && height >= mHeaderView.getReadyHeight()) {
            mHeaderView.updataState(ListViewHeader.STATE.READY_STATE);
        } else if (mHeaderView.getCurrentState() == ListViewHeader.STATE.END_STATE && height < 2) {
            mHeaderView.updataState(ListViewHeader.STATE.NORMAL_STATE);
        }
    }

    private void resetHeaderViewHeight() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            return;
        }
        int finalHeight = 0;
        mScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (getFirstVisiblePosition() == 0 && (mHeaderView.getVisibleHeight() > 0 || deltaY > 0)) {
                    updateHeaderViewHeight(deltaY / OFFSET_RADIO);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (getFirstVisiblePosition() == 0) {
                    resetHeaderViewHeight();
                }
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

    public void initGridView(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        mHeaderView = new ListViewHeader(context);
        mHeaderView.setStateChangedListener(this);
        addHeaderView(mHeaderView);
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
            }
        }
    }

    public void setListViewListener(ListViewListener listener) {
        mListViewListener = listener;
    }
}
