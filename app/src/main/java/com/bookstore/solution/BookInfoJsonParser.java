package com.bookstore.solution;

import android.os.Bundle;

import com.bookstore.util.BookData;

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

    public static JSONObject getJsonObjFromString(String str) throws Exception {
        if (str != null && str.length() > 0)
            try {
                return new JSONObject(str);
            } catch (JSONException e) {

            }
        else
            throw new Exception("Can create Json Object from empty string");
    }

    public static BookData getFullBookDataFromJson (JSONObject JsObj) {
        BookData data = getSimpleBookDataFromJson(JsObj);

        //TODO:Detail parser
        return data;
    }

    public static BookData getSimpleBookDataFromJson (JSONObject JsObj) {
        BookData data = null;
        int id = -1;
        try {
            id = JsObj.getInt("id");
        } catch (JSONException e) {
            //Log
        }

        if(id > 0) {
            data = new BookData(id);
            //TODO:Detail parser
        }

        return data;
    }
}
