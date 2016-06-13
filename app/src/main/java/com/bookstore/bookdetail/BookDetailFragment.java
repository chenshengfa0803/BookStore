package com.bookstore.bookdetail;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bookstore.booklist.BookListLoader;
import com.bookstore.bookparser.BookCategory;
import com.bookstore.bookparser.BookData;
import com.bookstore.bookparser.BookInfoJsonParser;
import com.bookstore.connection.BookInfoRequestBase;
import com.bookstore.connection.BookInfoUrlBase;
import com.bookstore.connection.douban.DoubanBookInfoUrl;
import com.bookstore.main.FloatButton;
import com.bookstore.main.MainActivity;
import com.bookstore.main.R;
import com.bookstore.main.SubFloatButton;
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
    ScrollView detail_scroll = null;
    ObjectAnimator loadAnimator = null;
    private String mBook_id;
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
    private TextView book_translator = null;
    private TextView book_category = null;
    private TextView book_pages = null;
    private TextView book_price = null;
    private View item1;
    private TextView summary_header = null;
    private ExpandableTextView book_summary = null;
    private View item2;
    private TextView catalog_header = null;
    private ExpandableTextView book_catalog = null;
    private View item3;
    private TagGroup tagGroup = null;

    public static BookDetailFragment newInstance(String objectId, int category_code, int paletteColor) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_BOOK_ID, objectId);
        args.putInt(ARGS_CATEGORY_CODE, category_code);
        args.putInt(ARGS_PALETTE_COLOR, paletteColor);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mBook_id = getArguments().getString(ARGS_BOOK_ID);
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
            detail_middle_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail_scroll != null) {
                        detail_scroll.smoothScrollTo(0, 0);
                    }
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SystemBarTintManager tintManager = new SystemBarTintManager(mActivity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintColor(mPalette_color);
            }
            setHasOptionsMenu(true);
        }
        detail_scroll = (ScrollView) detail_fragment.findViewById(R.id.scroll_detail);

        item0 = detail_fragment.findViewById(R.id.detail_item0);
        book_title = (TextView) item0.findViewById(R.id.detail_book_title);
        ratingBar = (RatingBar) item0.findViewById(R.id.detail_book_rating);
        book_author = (TextView) item0.findViewById(R.id.detail_book_author);
        book_translator = (TextView) item0.findViewById(R.id.detail_book_translator);
        book_category = (TextView) item0.findViewById(R.id.detail_book_category);
        book_pages = (TextView) item0.findViewById(R.id.detail_book_pages);
        book_price = (TextView) item0.findViewById(R.id.detail_book_price);

        item1 = detail_fragment.findViewById(R.id.detail_item1);
        summary_header = (TextView) item1.findViewById(R.id.detail_book_summary_header);
        book_summary = (ExpandableTextView) item1.findViewById(R.id.detail_expanded_book_summary);

        item2 = detail_fragment.findViewById(R.id.detail_item2);
        catalog_header = (TextView) item2.findViewById(R.id.catalog_header);
        book_catalog = (ExpandableTextView) item2.findViewById(R.id.detail_expanded_book_catalog);

        item3 = detail_fragment.findViewById(R.id.detail_item3);
        tagGroup = (TagGroup) item3.findViewById(R.id.tag_group);

        floatButtonLoadAnimation();

        return detail_fragment;
    }

    private void floatButtonLoadAnimation() {
        final FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
        ImageView imageView = new ImageView(mActivity);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_black));
        mainFloatButton.setFloatButtonIcon(imageView);
        mainFloatButton.registerClickListener(new FloatButton.FloatButtonClickListener() {
            @Override
            public void onFloatButtonClick(View floatButton) {
                floatButtonLoadAnimation();
                loadBookDetail();
            }
        });
        mainFloatButton.setEnabled(false);

        mainFloatButton.getContentView().setRotation(0);
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 360);
        loadAnimator = ObjectAnimator.ofPropertyValuesHolder(mainFloatButton.getContentView(), rotation);
        loadAnimator.setRepeatMode(ValueAnimator.RESTART);
        loadAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadAnimator.setDuration(1000);
        loadAnimator.start();
    }

    public void updateFloatButton() {
        final FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();

        ImageView imageView = new ImageView(mActivity);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_share_black));
        mainFloatButton.setFloatButtonIcon(imageView);

        int size = getResources().getDimensionPixelSize(R.dimen.sub_float_button_size);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        params.leftMargin = 0;
        params.topMargin = 0;
        params.rightMargin = 0;
        params.bottomMargin = 0;
        SubFloatButton subFab_wechat = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.share_wechat_selector), params);
        SubFloatButton subFab_friend = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.share_friend_selector), params);
        SubFloatButton subFab_sina = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.share_sina_selector), params);
        SubFloatButton subFab_qzone = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.share_qzone_selector), params);
        SubFloatButton subFab_qq = new SubFloatButton(mActivity, getResources().getDrawable(R.drawable.share_qq_selector), params);
        subFab_wechat.setClickable(true);
        subFab_friend.setClickable(true);
        subFab_sina.setClickable(true);
        subFab_qzone.setClickable(true);
        subFab_qq.setClickable(true);

        int startAngle = 260;//270 degree
        int endAngle = 370;//360 degree
        int menu_radio = getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
        int menu_duration = 500;//500 ms

        mainFloatButton.createFloatButtonMenu(startAngle, endAngle, menu_radio, menu_duration)
                .addSubFloatButton(subFab_wechat)
                .addSubFloatButton(subFab_friend)
                .addSubFloatButton(subFab_sina)
                .addSubFloatButton(subFab_qzone)
                .addSubFloatButton(subFab_qq);

        mainFloatButton.addMenuStateListener(new FloatButton.MenuStateListener() {
            @Override
            public void onMenuOpened(FloatButton fb) {
                ((MainActivity) mActivity).makeBlurWindow();
                fb.getContentView().setScaleX(0.8f);
                fb.getContentView().setScaleY(0.8f);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), scaleX, scaleY);
                animator.start();
            }

            @Override
            public void onMenuClosed(FloatButton fb) {
                fb.getContentView().setScaleX(0.8f);
                fb.getContentView().setScaleY(0.8f);
                PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
                PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(fb.getContentView(), scaleX, scaleY);
                animator.start();
                ((MainActivity) mActivity).disappearBlurWindow();
            }
        });

        mainFloatButton.registerClickListener(new FloatButton.FloatButtonClickListener() {
            @Override
            public void onFloatButtonClick(View floatButton) {
                if (mainFloatButton.isMenuOpened()) {
                    mainFloatButton.closeMenu();
                } else {
                    mainFloatButton.openMenu();
                }
            }
        });
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
        //loadBookDetail();
        loadCloudBookDetail();
    }

    private void loadBookDetail() {
        Uri uri = Uri.parse("content://" + BookProvider.AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME + "/" + mBook_id);
        String[] projection = {DB_Column.BookInfo.IMG_LARGE, DB_Column.BookInfo.ISBN13};
        mlistLoader = new BookListLoader(mActivity, uri, projection, null, null, null);
        mLoadListener = new BookListLoadListener();
        mlistLoader.registerListener(0, mLoadListener);
        mlistLoader.startLoading();
    }

    private void loadCloudBookDetail() {
        AVQuery<AVObject> query = new AVQuery<>(BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME);
        query.getInBackground(mBook_id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null) {
                    String cover = avObject.getString(DB_Column.BookInfo.IMG_LARGE);
                    ImageView image = (ImageView) mActivity.findViewById(R.id.detail_book_cover);

                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .build();

                    ImageLoader.getInstance().displayImage(cover, image, options);
                    String isbn = avObject.getString(DB_Column.BookInfo.ISBN13);
                    getBookDetailInfo(isbn);
                } else {
                    e.printStackTrace();
                }
            }
        });
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
                    updateFloatButton();
                } catch (Exception e) {
                    TextView load_fail_text = (TextView) item0.findViewById(R.id.loadfail_text);
                    if (load_fail_text == null) {
                        ViewStub stub = (ViewStub) item0.findViewById(R.id.load_bookdetail_fail);
                        stub.inflate();
                    }
                    if (loadAnimator != null) {
                        loadAnimator.setRepeatCount(0);
                    }
                    FloatButton mainFloatButton = ((MainActivity) mActivity).getFloatButton();
                    mainFloatButton.setEnabled(true);
                    e.printStackTrace();
                }
            }
        };
        bookRequest.requestExcute(BookInfoUrlBase.REQ_ISBN);
    }

    public void bindView0(BookData bookData) {
        TextView load_fail_text = (TextView) item0.findViewById(R.id.loadfail_text);
        if (load_fail_text != null) {
            load_fail_text.setVisibility(View.GONE);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(bookData.title);
        if (!TextUtils.isEmpty(bookData.subtitle)) {
            stringBuilder.append("(");
            stringBuilder.append(bookData.subtitle);
            stringBuilder.append(")");
        }
        book_title.setText(stringBuilder.toString());

        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(bookData.rating.average);

        if (bookData.authors.size() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(mActivity.getResources().getString(R.string.authors));
            sb.append(": ");
            String suffix = "";
            for (String author : bookData.authors) {
                sb.append(suffix);
                suffix = "、";
                sb.append(author);
            }
            book_author.setText(sb.toString());
        } else {
            book_author.setVisibility(View.GONE);
        }

        if (bookData.translator.size() != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(mActivity.getResources().getString(R.string.translators));
            sb.append(": ");
            String suffix = "";
            for (String translator : bookData.translator) {
                sb.append(suffix);
                suffix = "、";
                sb.append(translator);
            }
            book_translator.setText(sb.toString());
        } else {
            book_translator.setVisibility(View.GONE);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(mActivity.getResources().getString(R.string.category));
        sb.append(": ");
        sb.append(BookCategory.getCategoryName(bookData.category_code));
        book_category.setText(sb.toString());

        if (bookData.pages != null) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(mActivity.getResources().getString(R.string.pages));
            sb1.append(": ");
            sb1.append(bookData.pages);
            sb1.append(mActivity.getResources().getString(R.string.pages_unit));
            book_pages.setText(sb1.toString());
        } else {
            book_pages.setVisibility(View.GONE);
        }

        if (bookData.price != null) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(mActivity.getResources().getString(R.string.price));
            sb1.append(": ");
            sb1.append(bookData.price);
            book_price.setText(sb1.toString());
        } else {
            book_price.setVisibility(View.GONE);
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                String alertTitle = getResources().getString(R.string.delete_alert_title);
                builder.setTitle(alertTitle);
                builder.setPositiveButton(R.string.positive_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Uri uri = Uri.parse("content://" + BookProvider.AUTHORITY + "/" + BookSQLiteOpenHelper.BOOKINFO_TABLE_NAME + "/" + mBook_id);
                        mActivity.getContentResolver().delete(uri, null, null);
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
                builder.setNegativeButton(R.string.negative_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
        }
        return super.onOptionsItemSelected(item);
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
                data.close();
            }
        }
    }
}
