package com.phonelocation.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class SendPostRequestUtil {

	public static int sendJSONRequest(String jsonString, String url,String tokenid) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			// 添加http头信息			
			httppost.addHeader("Content-Type", "application/json");
			httppost.addHeader("tokenid",tokenid);
			httppost.setEntity(new StringEntity(jsonString));
			HttpResponse response;
			response = httpclient.execute(httppost);

			Log.i("mytag", "" + response.getStatusLine().getStatusCode());
			return response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
