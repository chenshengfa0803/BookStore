package com.bookstore.main.SearchView;

/**
 * Created by Administrator on 2016/5/22.
 */
public class SearchCloudItem {
    private String objectId;
    private String small_cover;
    private String book_title;
    private String author;
    private int category_code;

    public SearchCloudItem(String objectId, String small_cover, String book_title, String author, int category_code) {
        this.objectId = objectId;
        this.small_cover = small_cover;
        this.book_title = book_title;
        this.author = author;
        this.category_code = category_code;
    }

    public String getObjectId() {
        return objectId;
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
