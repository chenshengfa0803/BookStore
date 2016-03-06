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
    public List<Tags> tag;
    public String binding = null;
    public String price = null;
    public int series_id = -1;
    public String series_title = null;
    public int pages = -1;
    public Detail intro = null;
    public EBook ebook = null;
    public int id = -1;


    public BookData() {
        authors = new ArrayList<String>();
        translator = new ArrayList<String>();
        tag = new ArrayList<Tags>();
        rating = new Rating();
        intro = new Detail();
        ebook = new EBook();
    }

    final public class Rating {
        public int max = -1;
        public int num_raters = -1;
        public int average = -1;
    }

    final public class Tags {
        public int count = -1;
        public String name;
        public String title;
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
