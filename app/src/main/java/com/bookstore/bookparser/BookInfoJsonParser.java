package com.bookstore.bookparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 许杰 on 2016/3/2.
 */
public class BookInfoJsonParser {

    private static BookInfoJsonParser singleton = null;
    private BookInfoJsonParser () {}

    public static BookInfoJsonParser getInstance() {

        if (singleton == null)
            singleton = new BookInfoJsonParser();

        return singleton;
    }

    public JSONObject getJsonObjFromString(String str) throws Exception {
        if (str != null && str.length() > 0)
            try {
                return new JSONObject(str);
            } catch (JSONException e) {
                return null;
            }
        else
            throw new Exception("Can create Json Object from empty string");
    }

    // full book data will read from network, but not stored in local database
    public BookData getFullBookDataFromJson(String str) throws Exception {
        BookData data = getSimpleBookDataFromString(str);

        //TODO:Detail parser
        return data;
    }

    // simple book data will be stored in local database
    public BookData getSimpleBookDataFromString(String str) throws Exception {
        JSONObject JsObj = getJsonObjFromString(str);
        BookData bookData = new BookData();
        try {
            getBookId(bookData, JsObj);
            getBookTitle(bookData, JsObj);
            getBookAuthors(bookData, JsObj);
            getBookTranslator(bookData, JsObj);
            getBookPubDate(bookData, JsObj);
            getBookPublisher(bookData, JsObj);
            getBookPrice(bookData, JsObj);
            getBookPages(bookData, JsObj);
            getBookBinding(bookData, JsObj);
            getBookImagesUrl(bookData, JsObj);
            getBookIsbn(bookData, JsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bookData;
    }

    public void getBookId(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("id")) {
            bookData.book_id = json.getInt("id");
        }
    }

    public void getBookTitle(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("title")) {
            bookData.title = json.getString("title");
        }
    }

    public void getBookAuthors(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("author")) {
            JSONArray jsonArray = json.getJSONArray("author");
            for (int i = 0; i < jsonArray.length(); i++) {
                bookData.authors.add(jsonArray.getString(i));
            }
        }
    }

    public void getBookTranslator(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("translator")) {
            JSONArray jsonArray = json.getJSONArray("translator");
            for (int i = 0; i < jsonArray.length(); i++) {
                bookData.translator.add(jsonArray.getString(i));
            }
        }
    }

    public void getBookPubDate(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("pubdate")) {
            bookData.pub_date = json.getString("pubdate");
        }
    }

    public void getBookPrice(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("price")) {
            bookData.price = json.getString("price");
        }
    }

    public void getBookPublisher(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("publisher")) {
            bookData.publisher = json.getString("publisher");
        }
    }

    public void getBookPages(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("pages")) {
            bookData.pages = json.getInt("pages");
        }
    }

    public void getBookBinding(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("binding")) {
            bookData.binding = json.getString("binding");
        }
    }

    public void getBookImagesUrl(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("images")) {
            JSONObject imgUrlJson = json.getJSONObject("images");
            bookData.images_small = imgUrlJson.getString("small");
            bookData.images_large = imgUrlJson.getString("large");
            bookData.images_medium = imgUrlJson.getString("medium");
        }
    }

    public void getBookIsbn(BookData bookData, JSONObject json) throws JSONException {
        if (json.has("isbn10")) {
            bookData.isbn10 = json.getString("isbn10");
        }
        if (json.has("isbn13")) {
            bookData.isbn13 = json.getString("isbn13");
        }
    }
}
