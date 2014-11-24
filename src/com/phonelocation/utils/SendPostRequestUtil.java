package com.phonelocation.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class SendPostRequestUtil {

    /**
     * 发送JSON信息
     */
    public static int sendJSONRequest(String jsonString, String url,
            String tokenid) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            // 添加http头信息
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("tokenid", tokenid);
            httppost.setEntity(new StringEntity(jsonString));
            HttpResponse response;
            response = httpclient.execute(httppost);

            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
