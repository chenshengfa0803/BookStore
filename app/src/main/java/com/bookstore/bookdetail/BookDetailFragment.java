package com.bookstore.bookdetail;

import android.app.Activity;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookstore.booklist.BookListLoader;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.connection.BookInfoRequestBase;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.douban.DoubanBookInfoUrl;
import com.bookstore.main.R;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.BookSQLiteOpenHelper;
import com.bookstore.provider.DB_Column;
import com.bookstore.util.SystemBarTintManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

/**
 * Created by Administrator on 2016/4/11.
 */
public class BookDetailFragment extends Fragment {
    public static final String ARGS_BOOK_ID = "book_id";
    public static final String ARGS_CATEGORY_CODE = "category_code";
    public static final String ARGS_PALETTE_COLOR = "palette_color";
    private int mBook_id;
    private int mCategory_code;
    private int mPalette_color;
    private Activity mActivity;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;
    private SparseBooleanArray mCollapsedStatus;

    private View item0;
    private TextView book_title = null;
    private RatingBar ratingBar = null;
    private TextView book_author = null;
    private TextView book_category = null;

    private View item1;
    private TextView summary_header = null;
    private ExpandableTextView book_summary = null;

    private View item2;
    private TextView catalog_header = null;
    private ExpandableTextView book_catalog = null;

    private View item3;
    private TagGroup tagGroup = null;

    public static BookDetailFragment newInstance(int book_id, int category_code, int paletteColor) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_BOOK_ID, book_id);
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        args.putInt(ARGS_PALETTE_COLOR, paletteColor);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mBook_id = getArguments().getInt(ARGS_BOOK_ID, 0);
        mCategory_code = getArguments().getInt(ARGS_CATEGORY_CODE, 0);
        mPalette_color = getArguments().getInt(ARGS_PALETTE_COLOR, getResources().getColor(android.R.color.darker_gray));
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detail_fragment = inflater.inflate(R.layout.book_detail_fragment, null);

        AppCompatActivity mAppCompatActivity = (AppCompatActivity) mActivity;
        Toolbar detail_toolbar = (Toolbar) detail_fragment.findViewById(R.id.detail_toolbar);
        if (detail_toolbar != null) {
            mAppCompatActivity.setSupportActionBar(detail_toolbar);
            detail_toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
            detail_toolbar.setTitleTextColor(Color.WHITE);
            detail_toolbar.setBackgroundColor(mPalette_color);
            detail_toolbar.setTitle("");
            detail_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.onBackPressed();
                }
            });
            TextView detail_middle_title = (TextView) detail_toolbar.findViewById(R.id.toolbar_middle_title);
            detail_middle_title.setVisibility(View.VISIBLE);
            detail_middle_title.setText(getString(R.string.detail_bookinfo));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(mPalette_color);
            }
        }

        item0 = detail_fragment.findViewById(R.id.detail_item0);
        book_title = (TextView) item0.findViewById(R.id.detail_book_title);
        ratingBar = (RatingBar) item0.findViewById(R.id.detail_book_rating);
        book_author = (TextView) item0.findViewById(R.id.detail_book_author);
        book_category = (TextView) item0.findViewById(R.id.detail_book_category);

        item1 = detail_fragment.findViewById(R.id.detail_item1);
        summary_header = (TextView) item1.findViewById(R.id.detail_book_summary_header);
        book_summary = (ExpandableTextView) item1.findViewById(R.id.detail_expanded_book_summary);

        item2 = detail_fragment.findViewById(R.id.detail_item2);
        catalog_header = (TextView) item2.findViewById(R.id.catalog_header);
        book_catalog = (ExpandableTextView) item2.findViewById(R.id.detail_expanded_book_catalog);

        item3 = detail_fragment.findViewById(R.id.detail_item3);
        tagGroup = (TagGroup) item3.findViewById(R.id.tag_group);

        return detail_fragment;
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
        super.onResume();
        Uri uri = Uri.parse("content://" + BookProvider.AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME + "/" + mBook_id);
        String selection = DB_Column.BookInfo.ID
                + "="
                + mBook_id;
        String[] projection = {DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.ISBN13};
        //mlistLoader = new BookListLoader(mActivity, BookProvider.BOOKINFO_URI, projection, selection, null, null);
        mlistLoader = new BookListLoader(mActivity, uri, projection, null, null, null);
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();
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

    public void getBookDetailInfo(String isbn) {
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
                    BookData bookData = BookInfoJsonParser.getInstance().getFullBookDataFromJson(bookInfo);
                    bookData.category_code = mCategory_code;
                    bindView0(bookData);
                    bindView1(bookData);
                    bindView2(bookData);
                    bindView3(bookData);
                } catch (Exception e) {
                    ViewStub stub = (ViewStub) item0.findViewById(R.id.load_bookdetail_fail);
                    stub.inflate();
                    e.printStackTrace();
                }
            }
        };
        bookRequest.requestExcute(BookInfoUrlBase.REQ_ISBN);
    }

    public void bindView0(BookData bookData) {
        book_title.setText(bookData.title);
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(bookData.rating.average);
        if (bookData.authors.size() != 0) {
            book_author.setText(bookData.authors.get(0));
        }
        book_category.setText(BookCategory.getCategoryName(bookData.category_code));
    }

    public void bindView1(BookData bookData) {
        if (!TextUtils.isEmpty(bookData.detail.summary)) {
            summary_header.setText("简介");
            book_summary.setText(bookData.detail.summary, mCollapsedStatus, 0);
        } else {
            item1.setVisibility(View.GONE);
        }
    }

    public void bindView2(BookData bookData) {
        if (!TextUtils.isEmpty(bookData.detail.catalog)) {
            catalog_header.setText("目录");
            book_catalog.setText(bookData.detail.catalog, mCollapsedStatus, 1);
        } else {
            item2.setVisibility(View.GONE);
        }
    }

    public void bindView3(BookData bookData) {
        List<String> tagList = new ArrayList<>();
        for (BookData.Tag tag : bookData.tags) {
            tagList.add(tag.name);
        }
        if (tagList.size() > 0) {
            tagGroup.setTags(tagList);
        } else {
            item3.setVisibility(View.GONE);
        }
    }

    private class BookListLoadListener implements Loader.OnLoadCompleteListener<Cursor> {
        public BookListLoadListener() {
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.moveToFirst()) {
                    String cover = data.getString(data.getColumnIndex(DB_Column.BookInfo.IMG_LARGE));
                    ImageView image = (ImageView) mActivity.findViewById(R.id.detail_book_cover);

                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();

                    ImageLoader.getInstance().displayImage(cover, image, options);
                    String isbn = data.getString(data.getColumnIndex(DB_Column.BookInfo.ISBN13));
                    getBookDetailInfo(isbn);
                }
            }
        }
    }
}
