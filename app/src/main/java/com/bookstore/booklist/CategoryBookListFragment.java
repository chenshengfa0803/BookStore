package com.bookstore.booklist;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.bookdetail.BookDetailFragment;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.main.BookOnClickListener;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.main.animation.BookDetailTransition;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.DB_Column;
import com.bookstore.util.BitmapUtil;
import com.bookstore.util.SystemBarTintManager;

/**
 * Created by Administrator on 2016/4/6.
 */
public class CategoryBookListFragment extends Fragment {
    public static final String ARGS_CATEGORY_CODE = "category_code";
    GridView mGridView;
    private Activity mActivity;
    private int mCategoryCode = 0;
    private CategoryBookGridViewAdapter gridViewAdapter = null;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;
    private BookOnClickListener mListener = new BookOnClickListener() {
        @Override
        public void onBookClick(View clickedImageView, int book_id, int category_code) {
            Bitmap bitmap = null;
            int paletteColor = getResources().getColor(android.R.color.darker_gray);
            BitmapDrawable bd = (BitmapDrawable) ((ImageView) clickedImageView).getDrawable();
            if (bd != null) {
                bitmap = bd.getBitmap();
                paletteColor = BitmapUtil.getPaletteColor(bitmap);
            }
            BookDetailFragment detailFragment = BookDetailFragment.newInstance(book_id, category_code, paletteColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                detailFragment.setSharedElementEnterTransition(new BookDetailTransition());
                setExitTransition(new Fade());
                detailFragment.setEnterTransition(new Fade());
                detailFragment.setSharedElementReturnTransition(new BookDetailTransition());
            }

            FragmentManager fragmentManager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addSharedElement(clickedImageView, getString(R.string.image_transition))
                    .hide(fragmentManager.findFragmentByTag(CategoryBookListFragment.class.getSimpleName()))
                    .addToBackStack(null)
                    .add(R.id.container_view, detailFragment, BookDetailFragment.class.getSimpleName())
                    .commit();

        }
    };

    public static CategoryBookListFragment newInstance(int category_code) {
        CategoryBookListFragment fragment = new CategoryBookListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mCategoryCode = getArguments().getInt(ARGS_CATEGORY_CODE, 0);
        Log.i("BookStore", "read books by category code " + mCategoryCode);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View category_fragment = null;
        category_fragment = inflater.inflate(R.layout.category_list_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        Toolbar category_toolbar = (Toolbar) category_fragment.findViewById(R.id.category_toolbar);
        if (category_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(category_toolbar);
            category_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            category_toolbar.setTitleTextColor(Color.WHITE);
            category_toolbar.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            category_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            category_toolbar.setTitle("");
            TextView title_middle = (TextView) category_toolbar.findViewById(R.id.toolbar_middle_title);
            title_middle.setVisibility(View.VISIBLE);
            title_middle.setText(BookCategory.getCategoryName(mCategoryCode));
            title_middle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGridView != null) {
                        mGridView.smoothScrollToPosition(0);
                    }
                }
            });
        }
        if (mCategoryCode == 0) {
            TextView textView = new TextView(mActivity);
            textView.setTextColor(Color.RED);
            textView.setText(R.string.category_error);
            FrameLayout frameLayout = (FrameLayout) category_fragment.findViewById(R.id.category_summary);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.addView(textView, lp);
            frameLayout.setVisibility(View.VISIBLE);
        }

        mGridView = (GridView) category_fragment.findViewById(R.id.category_book_gridview);
        gridViewAdapter = new CategoryBookGridViewAdapter(mActivity, mListener);
        mGridView.setAdapter(gridViewAdapter);
        return category_fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        String selection = null;
        super.onResume();
        if (mCategoryCode != 'a') {
            selection = DB_Column.BookInfo.CATEGORY_CODE
                    + "="
                    + mCategoryCode;
        }
        String[] projection = {DB_Column.BookInfo.ID, DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.TITLE, DB_Column.BookInfo.CATEGORY_CODE};
        //mlistLoader = new BookListLoader(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC LIMIT 15");
        mlistLoader = new BookListLoader(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, DB_Column.BookInfo.ID + " DESC");
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(getResources().getColor(android.R.color.darker_gray));
            }
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.main_floatbutton_add));
            View current = ((MainActivity) mActivity).mainFloatButton.getContentView();
            ((MainActivity) mActivity).mainFloatButton.removeView(current);
            ((MainActivity) mActivity).mainFloatButton.setContentView(imageView, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            gridViewAdapter.registerDataCursor(data);
            gridViewAdapter.notifyDataSetChanged();
        }
    }
}
