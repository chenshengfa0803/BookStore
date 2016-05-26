package com.bookstore.main.SearchView;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;
import com.bookstore.provider.DB_Column;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/5/22.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> implements Filterable {
    private final List<Integer> mStartList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<SearchItem> mDataList = new ArrayList<>();
    private List<SearchItem> mSearchList = new ArrayList<>();
    private int mKeyLength = 0;

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.search_view_item, parent, false);
        return new ResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        SearchItem item = mSearchList.get(position);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(item.getBook_cover(), holder.book_cover, options);

        int key_start = mStartList.get(position);
        int key_end = key_start + mKeyLength;
        String text = item.getAuthor() + " : " + item.getBook_title();
        holder.book_title.setText(text, TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable) holder.book_title.getText();
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.search_light_text_highlight)), key_start, key_end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.book_title.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public List<SearchItem> getSearchList() {
        return mSearchList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    List<SearchItem> searchData = new ArrayList<>();

                    mStartList.clear();
                    String key = constraint.toString().toLowerCase(Locale.getDefault());
                    for (SearchItem item : mDataList) {
                        String title = item.getBook_title().toLowerCase(Locale.getDefault());
                        String author = item.getAuthor().toLowerCase(Locale.getDefault());
                        String all = author + " : " + title;
                        if (all.contains(key)) {
                            searchData.add(item);
                            mStartList.add(all.indexOf(key));
                            mKeyLength = key.length();
                        }
                    }
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mSearchList.clear();
                if (results.values != null) {
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof SearchItem) {
                            mSearchList.add((SearchItem) object);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        };
    }

    public void registerDataCursor(Cursor dataCursor) {
        if (dataCursor == null) {
            return;
        }
        mDataList.clear();
        dataCursor.moveToFirst();
        do {
            int id = dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.ID));
            String img = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.IMG_SMALL));
            String title = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.TITLE));
            String author = dataCursor.getString(dataCursor.getColumnIndex(DB_Column.BookInfo.AUTHOR));
            int category_code = dataCursor.getInt(dataCursor.getColumnIndex(DB_Column.BookInfo.CATEGORY_CODE));
            SearchItem item = new SearchItem(id, img, title, author, category_code);
            mDataList.add(item);
        } while (dataCursor.moveToNext());

        dataCursor.close();
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView book_cover;
        private final TextView book_title;

        public ResultViewHolder(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.search_item_bookcover);
            book_title = (TextView) itemView.findViewById(R.id.search_item_booktitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }
}
