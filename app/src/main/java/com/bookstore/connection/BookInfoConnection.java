package com.bookstore.connection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 许杰 on 2016/3/10.
 */
public class BookInfoConnection {

    private static final String prefix_http = "http://";
    private static final String prefix_https = "https://";
    private static final int timeout = 5000;

    public String doRequestFromUrl(String urlString) throws Exception {
        String url = simpleCheckUrl(urlString);
        if (url == null || url.length() <= 0)
            throw new Exception("Requesting Wrong Url, please check");
        URL requestUrl = new URL(url);

        InputStream responseInputStream = null;
        InputStreamReader responseInputReader = null;
        BufferedReader bufReader = null;
        StringBuffer result = new StringBuffer();
        String tempLine = null;

        if (url.startsWith(prefix_http) || url.startsWith(prefix_https)) {
            HttpURLConnection httpConnection = (HttpURLConnection) requestUrl.openConnection();
            httpConnection.setConnectTimeout(timeout);
            httpConnection.setReadTimeout(timeout);
            int code = httpConnection.getResponseCode();
            if (code != 200) {
                throw new Exception("HTTP request failed, code is " + httpConnection.getResponseCode());
            }

            try {
                responseInputStream = httpConnection.getInputStream();
                responseInputReader = new InputStreamReader(responseInputStream);
                bufReader = new BufferedReader(responseInputReader);
                while ((tempLine = bufReader.readLine()) != null) {
                    result.append(tempLine);
                }
            } catch (Exception e) {
                //TODO : ex handler
            } finally {
                if (bufReader != null) {
                    bufReader.close();
                }

                if (responseInputReader != null) {
                    responseInputReader.close();
                }

                if (responseInputStream != null) {
                    responseInputStream.close();
                }
            }

            if (result.length() > 0) {
                return result.toString();
            } else {
                throw new Exception("HTTP request success, but return nothing");
            }


        } else if (url.startsWith(prefix_https)){
            // TODO : https request
        }

        return null;
    }

    public String simpleCheckUrl(String urlString) {
        if(urlString == null)
            return null;
        urlString = urlString.trim();
        if(urlString.isEmpty() || urlString.contains(" "))
            return null;
        if(urlString.startsWith(prefix_http) || urlString.startsWith(prefix_https))
            return urlString;
        else
            return prefix_http + urlString;
    }
}
