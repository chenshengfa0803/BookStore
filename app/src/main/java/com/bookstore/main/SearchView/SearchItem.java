package com.bookstore.main.SearchView;

/**
 * Created by Administrator on 2016/5/22.
 */
public class SearchItem {
    private int book_id;
    private String small_cover;
    private String book_title;
    private String author;
    private int category_code;

    public SearchItem(int book_id, String small_cover, String book_title, String author, int category_code) {
        this.book_id = book_id;
        this.small_cover = small_cover;
        this.book_title = book_title;
        this.author = author;
        this.category_code = category_code;
    }

    public int getBook_id() {
        return book_id;
    }

    public String getBook_cover() {
        return small_cover;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCategory_code() {
        return category_code;
    }
}
