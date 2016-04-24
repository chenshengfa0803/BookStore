package com.bookstore.bookparser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 许杰 on 2016/3/2.
 */
final public class BookData {

    public String isbn10 = null;
    public String isbn13 = null;
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
    public List<String> authors = null;
    public List<String> translator = null;
    public String publisher = null;
    public String pub_date = null;
    public Rating rating = null;
    public List<Tag> tags;
    public String binding = null;
    public String price = null;
    public int series_id = -1;
    public String series_title = null;
    public String pages = null;
    public String clc_number = null;
    public int category_code = 0;
    public String category_name = null;
    public Detail detail = null;
    public EBook ebook = null;
    public int book_id = -1;


    public BookData() {
        authors = new ArrayList<String>();
        translator = new ArrayList<String>();
        tags = new ArrayList<Tag>();
        rating = new Rating();
        detail = new Detail();
        ebook = new EBook();
    }

    final static public class Tag {
        public int count = -1;
        public String name;
        public String title;
    }

    final public class Rating {
        public int num_raters = -1;
        public float average;
    }

    final public class Detail {
        public String author_intro = null;
        public String summary = null;
        public String catalog = null;
    }

    final public class EBook {
        public String ebook_url = null;
        public String ebook_price = null;
    }

}
