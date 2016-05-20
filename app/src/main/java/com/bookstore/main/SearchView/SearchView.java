package com.bookstore.main.SearchView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/5/16.
 */
public class SearchView extends FrameLayout implements View.OnClickListener {
    private static int ANIMATION_DURATION = 300;
    private Context mContext;
    private ImageView mBackImageView;
    private ImageView mClearImageView;
    private View mBackGroundView;
    private CardView mCardView;
    private EditText mEditText;
    private SearchViewListener mSearchViewListener;
    private boolean mIsSearchOpen = false;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate((R.layout.search_view_layout), this, true);
        mBackImageView = (ImageView) findViewById(R.id.searchView_navi_back);
        mBackImageView.setOnClickListener(this);

        mClearImageView = (ImageView) findViewById(R.id.search_view_clear);
        mClearImageView.setOnClickListener(this);
        mClearImageView.setVisibility(View.GONE);

        mBackGroundView = findViewById(R.id.search_background);
        mBackGroundView.setOnClickListener(this);
        //mBackGroundView.setVisibility(View.GONE);

        mCardView = (CardView) findViewById(R.id.search_card_view);

        mEditText = (EditText) findViewById(R.id.editText_input);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //be called when press search button on the soft keyboard
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence text = mEditText.getText();
                boolean hasText = !TextUtils.isEmpty(text);

                if (hasText) {
                    mClearImageView.setVisibility(VISIBLE);
                } else {
                    mClearImageView.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == mBackImageView || v == mBackGroundView) {
            hide();
        } else if (v == mClearImageView) {
            mEditText.setText(null);
        }
    }

    public void show() {
        setVisibility(VISIBLE);
        mEditText.requestFocus();
        mEditText.setText(null);
        mIsSearchOpen = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            expandInAnimation();
        } else {
            SearchAnimator.fadeInAnimation(mCardView, ANIMATION_DURATION);
        }

        if (mSearchViewListener != null) {
            mSearchViewListener.onSearchViewShown();
        }
    }

    public void hide() {
        mEditText.clearFocus();
        mEditText.setText(null);
        mIsSearchOpen = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SearchAnimator.collaspeOutAnimation(mContext, mCardView, ANIMATION_DURATION);
        } else {
            SearchAnimator.fadeOutAnimation(mCardView, ANIMATION_DURATION);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
                if (mSearchViewListener != null) {
                    mSearchViewListener.onSearchViewClosed();
                }
            }
        }, ANIMATION_DURATION);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void expandInAnimation() {
        mCardView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                SearchAnimator.expandInAnimation(mContext, mCardView, ANIMATION_DURATION);
            }
        });
    }

    public interface SearchViewListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }
}
