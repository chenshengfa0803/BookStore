package com.bookstore.bookdetail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bookstore.booklist.BookListLoader;
import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.connection.BookInfoRequestBase;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.douban.DoubanBookInfoUrl;
import com.bookstore.main.R;
import com.bookstore.provider.BookProvider;
import com.bookstore.provider.BookSQLiteOpenHelper;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/4/11.
 */
public class BookDetailFragment extends Fragment {
    public static final String ARGS_BOOK_ID = "book_id";
    public static final String ARGS_CATEGORY_CODE = "category_code";
    private int mBook_id;
    private int mCategory_code;
    private Activity mActivity;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;
    private BookDetailListViewAdapter detailListViewAdapter = null;

    public static BookDetailFragment newInstance(int book_id, int category_code) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_BOOK_ID, book_id);
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mBook_id = getArguments().getInt(ARGS_BOOK_ID, 0);
        mCategory_code = getArguments().getInt(ARGS_CATEGORY_CODE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detail_fragment = inflater.inflate(R.layout.book_detail_fragment, null);
        BookDetailListView detailListView = (BookDetailListView) detail_fragment.findViewById(R.id.book_detail_list_container);
        detailListViewAdapter = new BookDetailListViewAdapter(mActivity);
        detailListView.setAdapter(detailListViewAdapter);

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
        String[] projection = {DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.ISBN10};
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
                    detailListViewAdapter.registerData(bookData);
                    detailListViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bookRequest.requestExcute(BookInfoUrlBase.REQ_ISBN);
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
                    String isbn = data.getString(data.getColumnIndex(DB_Column.BookInfo.ISBN10));
                    getBookDetailInfo(isbn);
                }
            }
        }
    }
}
