package com.bookstore.connection;

import android.os.AsyncTask;


/**
 * Created by 许杰 on 2016/3/12.
 */
public abstract class BookInfoRequestBase  {

    private BookInfoUrlBase mRequest = null;

    public BookInfoRequestBase(BookInfoUrlBase RequestBase) {
        super();
        mRequest = RequestBase;
    }

    public void requestExcute(int type, Object... param) {
        if (mRequest == null)
            return;
        String url = mRequest.getRequestUrl(type, param);
        AsyncTask task = new AsyncTask<String, Integer, String>() {



            @Override
            protected String doInBackground(String... params) {
                BookInfoConnection connection = new BookInfoConnection();
                try {
                    return connection.doRequestFromUrl(params[0]);
                } catch (Exception e) {
                    //TODO
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                PreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                PostExecute(s);
            }
        };
        task.execute(url);



    }


    protected abstract void PreExecute();

    protected abstract void PostExecute(String s) ;
}
