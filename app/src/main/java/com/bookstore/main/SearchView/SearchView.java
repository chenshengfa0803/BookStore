package com.bookstore.main.SearchView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.booklist.BookListLoader;
import com.bookstore.main.R;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;

/**
 * Created by Administrator on 2016/5/16.
 */
public class SearchView extends FrameLayout implements View.OnClickListener, Filter.FilterListener {
    private static int ANIMATION_DURATION = 300;
    private Context mContext;
    private ImageView mBackImageView;
    private ImageView mClearImageView;
    private View mBackGroundView;
    private CardView mCardView;
    private EditText mEditText;
    private View mDivider;
    private RecyclerView mRecyclerView;
    private SearchViewListener mSearchViewListener;
    private boolean mIsSearchOpen = false;
    private SearchAdapter mSearchAdapter;

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

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mDivider = findViewById(R.id.view_divider);
        mDivider.setVisibility(View.GONE);

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
                startFilter(s);
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
                if (hasFocus) {
                    showKeyboard();
                } else {
                    hideKeyboard();
                }
            }
        });
        mEditText.requestFocus();
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

    public boolean isSearchOpen() {
        return mIsSearchOpen;
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

        getBookList();
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

    public void registerSearchViewStateListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSearchResult();
        } else {
            hideSearchResult();
        }
    }

    private void showSearchResult() {
/*        if (mSearchAdapter != null && mSearchAdapter.getItemCount() > 0 && mRecyclerView.getVisibility() == GONE) {
            mDivider.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(VISIBLE);
            mRecyclerView.setAlpha(0.0f);
            mRecyclerView.animate().alpha(1.0f);
        }*/
    }

    private void hideSearchResult() {
/*        if (mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        }*/
    }

    private void startFilter(CharSequence s) {
        if (mSearchAdapter != null) {
            mSearchAdapter.getFilter().filter(s, this);
        }
    }

    public void setAdapter(SearchAdapter adapter) {
        mSearchAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        startFilter(mEditText.getText());
    }

    public void getBookList() {
        String[] projection = {DB_Column.BookInfo.ID, DB_Column.BookInfo.IMG_SMALL, DB_Column.BookInfo.TITLE, DB_Column.BookInfo.AUTHOR, DB_Column.BookInfo.CATEGORY_CODE};
        BookListLoader mlistLoader = new BookListLoader(mContext, BookProvider.BOOKINFO_URI, projection, null, null, DB_Column.BookInfo.ID + " DESC");
        BookListLoadListener mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();
    }

    public interface SearchViewListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }

    private class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            mSearchAdapter.registerDataCursor(data);
        }
    }
}
