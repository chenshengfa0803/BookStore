package com.bookstore.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bookstore.booklist.BookListLoader;
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
    private int mBook_id;
    private Activity mActivity;
    private BookListLoader mlistLoader = null;
    private BookListLoadListener mLoadListener = null;

    public static BookDetailFragment newInstance(int book_id) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_BOOK_ID, book_id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mBook_id = getArguments().getInt(ARGS_BOOK_ID, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detail_fragment = inflater.inflate(R.layout.book_detail_fragment, null);
        ImageView image = (ImageView) detail_fragment.findViewById(R.id.detail_book_cover);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.getFragmentManager().popBackStack();
            }
        });
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
        String[] projection = {DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.TITLE};
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
                }
            }
        }
    }
}
