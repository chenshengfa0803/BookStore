package com.bookstore.util;

import java.util.List;

/**
 * Created by 许杰 on 2016/3/2.
 */
final public class BookData {

    private int id = -1;
    public int isbn10 = -1;
    public int isbn13 = -1;
    public String title = null;
    public String origin_title = null;
    public String alt_title = null;
    public String subtitle = null;
    public String url = null;
    public String alt = null;
    public String image = null;
    public String images_small = null;
    public String images_large = null;
    public String images_medium = null;
    public String author = null;
    public String translator = null;
    public String publisher = null;
    public String pub_date = null;
    public Rating rating = null;
    public List<Tags> tag;
    public String binding = null;
    public float price = -1;
    public int series_id = -1;
    public String series_title = null;
    public int pages = -1;
    public Detail intro = null;
    public Ebook ebook = null;


    final public class Rating {
        public int max = -1;
        public int num_raters = -1;
        public int average = -1;
    }

    final public class Tags {
        public int count = -1;
        public String name = null;
    }

    final public class Detail {
        public String author_intro = null;
        public String summary = null;
        public String catalog = null;
    }

    final public class Ebook {
        public String ebook_url = null;
        public String ebook_price = null;
    }


    BookData(int id) {
        this.id = id;
        this.rating = new Rating();
    }

}
