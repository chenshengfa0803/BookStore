package com.bookstore.util;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/24.
 */
public class BookCategory {
    private static ArrayList<String> category_list = null;

    static {
        category_list = new ArrayList<String>();
        category_list.add("全部分类");
        category_list.add("计算机/互联网");
        category_list.add("历史/地理");
        category_list.add("文学");
        category_list.add("哲学/宗教");
    }

    public static String getCategoryName(int pos) {
        if (pos >= category_list.size()) {
            return null;
        } else {
            return category_list.get(pos);
        }
    }

    public static int getCategoryCount() {
        return category_list.size();
    }
}
