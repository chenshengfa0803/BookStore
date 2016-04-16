package com.bookstore.bookdetail;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bookstore.bookparser.BookCategory;
import com.bookstore.bookparser.BookData;
import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/4/13.
 */
public class BookDetailListViewAdapter extends BaseAdapter {
    private Context mContext;
    private BookData mBookData = null;

    public BookDetailListViewAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View detail_item_view = null;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        switch (position) {
            case 0:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item0, null);

                    ImageView book_cover = (ImageView) detail_item_view.findViewById(R.id.detail_book_cover);
                    book_cover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity) mContext).getFragmentManager().popBackStack();
                        }
                    });
                } else {
                    detail_item_view = convertView;
                }
                break;
            case 1:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item1, null);
                } else {
                    detail_item_view = convertView;
                }
                break;
            case 2:
                if (convertView == null) {
                    detail_item_view = mInflater.inflate(R.layout.bookdetail_list_item2, null);
                } else {
                    detail_item_view = convertView;
                }
                break;
        }
        bindView(detail_item_view, position);
        return detail_item_view;
    }

    public void bindView(View detail_item_view, int position) {
        switch (position) {
            case 0:
                if (mBookData != null) {
                    TextView book_title = (TextView) detail_item_view.findViewById(R.id.detail_book_title);
                    RatingBar ratingBar = (RatingBar) detail_item_view.findViewById(R.id.detail_book_rating);
                    TextView book_author = (TextView) detail_item_view.findViewById(R.id.detail_book_author);
                    TextView book_category = (TextView) detail_item_view.findViewById(R.id.detail_book_category);

                    book_title.setText(mBookData.title);
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(mBookData.rating.average);
                    book_author.setText(mBookData.authors.get(0));
                    book_category.setText(BookCategory.getCategoryName(mBookData.category_code));
                }
                break;
            case 1:
                if (mBookData != null) {
                    TextView summary_header = (TextView) detail_item_view.findViewById(R.id.detail_book_summary_header);
                    final TextView summary_content = (TextView) detail_item_view.findViewById(R.id.detail_book_summary_content);
                    ImageView expand_collapse = (ImageView) detail_item_view.findViewById(R.id.book_intro_expand_collapse);
                    summary_header.setText("简介");
                    summary_content.setText(mBookData.detail.summary);
                    if (summary_content.getLineCount() == summary_content.getMaxLines()) {
                        expand_collapse.setVisibility(View.VISIBLE);
                        expand_collapse.setImageDrawable(mContext.getDrawable(R.drawable.ic_expand));
                        expand_collapse.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                summary_content.setMinLines(10);
                                summary_content.requestLayout();
                                summary_content.setEllipsize(null);
                            }
                        });
                    }
                }
                break;
            case 2:
                if (mBookData != null) {
                    TextView catalog_header = (TextView) detail_item_view.findViewById(R.id.catalog_header);
                    TextView book_catalog_content = (TextView) detail_item_view.findViewById(R.id.catalog_content);
                    catalog_header.setText("目录");
                    book_catalog_content.setText(mBookData.detail.catalog);
                }
                break;
        }
    }

    public void registerData(BookData bookData) {
        mBookData = bookData;
    }
}
