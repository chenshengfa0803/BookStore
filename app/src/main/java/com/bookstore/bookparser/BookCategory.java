package com.bookstore.bookparser;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/24.
 */
public class BookCategory {
    public static ArrayList<CategoryItem> default_category_list = new ArrayList<CategoryItem>();
    public static ArrayList<CategoryItem> user_category_list = new ArrayList<CategoryItem>();//user may define new category

    static {
        default_category_list.add(new CategoryItem('a', "所有图书"));
        default_category_list.add(new CategoryItem('A', "马列主义、毛泽东思想、邓小平理论"));// A
        default_category_list.add(new CategoryItem('B', "哲学、宗教"));                       // B
        default_category_list.add(new CategoryItem('C', "社会科学总论"));                    // C
        default_category_list.add(new CategoryItem('D', "政治、法律"));                       // D
        default_category_list.add(new CategoryItem('E', "军事"));                            // E
        default_category_list.add(new CategoryItem('F', "经济"));                            // F
        default_category_list.add(new CategoryItem('G', "文化、科学、教育、体育"));         // G
        default_category_list.add(new CategoryItem('H', "语言、文字"));                      // H
        default_category_list.add(new CategoryItem('I', "文学"));                           // I
        default_category_list.add(new CategoryItem('J', "艺术"));                           // J
        default_category_list.add(new CategoryItem('K', "历史、地理"));                    // K
        default_category_list.add(new CategoryItem('N', "自然科学总论"));                  // N
        default_category_list.add(new CategoryItem('O', "数理科学和化学"));                // O
        default_category_list.add(new CategoryItem('P', "天文学、地球科学"));              // P
        default_category_list.add(new CategoryItem('Q', "生物科学"));                      // Q
        default_category_list.add(new CategoryItem('R', "医药、卫生"));                    // R
        default_category_list.add(new CategoryItem('S', "农业科学"));                      // S
        default_category_list.add(new CategoryItem('T', "工业技术"));                      // T
        default_category_list.add(new CategoryItem('U', "交通运输"));                      // U
        default_category_list.add(new CategoryItem('V', "航空、航天"));                    // V
        default_category_list.add(new CategoryItem('X', "环境科学、安全科学"));           // X
        default_category_list.add(new CategoryItem('Z', "综合性图书"));                    // Z
        default_category_list.add(new CategoryItem(0, "其他"));
    }

    public BookCategory() {
    }

    public static int getCategoryByClcNum(String clcNum) {
        int result_category = 0;//if result_category is 0, that means category is 其他(other)
        if (clcNum != null) {
            char firstChar = clcNum.charAt(0);
            result_category = firstChar;//use the first character as category, because clcNum is always like this TN911.73, T means 工业技术(Technology) category
        }
        return result_category;
    }

    /* this function will return CLC number of the book, like TN911.73
    * Param clcStr will include information like this
    <!-- publish section
     ACTIVE-LIBRARY: NLC01
     SFX URL:  <a href="javascript:open_window('http://opac.nlc.cn:80/F/QS52U9CLG71ETDSBQBVXFED4N2JCPMFNDSCVUBX9H2LN5HDI2G-02697?func=service-sfx&doc_number=003640267&line_number=0000&service_type=RECORD');"><img src=http://opac.nlc.cn:80/exlibris/aleph/u20_1/alephe/www_f_chi/icon/f-sfx.gif border=0 alt='Use SFX services'></a>

     FMT:
     ISBN: 978-7-121-04397-0 CNY59.00
     TITLE: 数字图像处理 专著 Digital image processing (美)Rafael C. Gonzalez，(美)Richard E. Woods
     AUTHOR: (美) 冈萨雷斯 (Gonzalez, Rafael C.) 著 gang sa lei si
     IMPRINT: 电子工业出版社
     CALL-NO: TN911.73

     DOC-NUMBER: 003640267
     BASE:       NLC01
     SET-NUMBER: 389979
     SET-FORMAT: 999
     FIND-REQUEST: ISB = "9787121043970"

     SEARCH-POINT: ISB
     BASE: NLC01
    -->
    * Actually, clcStr is a HTML document
    * this function is intent to find string "CALL-NO: TN911.73"
    * */
    public static String getClcNumber(String clcStr) {
        int pos = clcStr.indexOf("CALL-NO");
        if (pos == -1) {
            return null;
        }
        String newStr = clcStr.substring(pos);
        int blankPos = newStr.indexOf(" ");
        String clc = newStr.substring(blankPos);
        String clc_no_blank = clc.substring(1);
        int endPos = clc_no_blank.indexOf(" ");
        String clcNum = clc_no_blank.substring(0, endPos);
        return clcNum;

    }



    public ArrayList<CategoryItem> getDefault_category_list() {
        return default_category_list;
    }

    @Deprecated
    public ArrayList<CategoryItem> getUser_category_list() {
        return user_category_list;
    }

    @Deprecated
    public void setUser_category_list(ArrayList<CategoryItem> list) {
        user_category_list = list;
    }

    public String getCategoryName(int category_code) {
        for (CategoryItem item : default_category_list) {
            if (item.category_code == category_code) {
                return item.category_name;
            }
        }
        return null;
    }

    public int getIndexByCategoryCode(int category_code) {
        for (int i = 0; i < default_category_list.size(); i++) {
            if (default_category_list.get(i).category_code == category_code) {
                return i;
            }
        }
        return default_category_list.size() - 1;
    }

    public int getCategoryCount() {
        return default_category_list.size();
    }

    public static class CategoryItem {
        public int category_code;
        public String category_name;

        public CategoryItem(int category_code, String category_name) {
            this.category_code = category_code;
            this.category_name = category_name;
        }
    }
}
