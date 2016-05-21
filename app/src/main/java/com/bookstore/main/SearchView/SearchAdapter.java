package com.bookstore.main.SearchView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/5/22.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> {
    private Context mContext;

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

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView book_cover;
        private final TextView book_title;

        public ResultViewHolder(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.search_item_bookcover);
            book_title = (TextView) itemView.findViewById(R.id.search_item_booktitle);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
