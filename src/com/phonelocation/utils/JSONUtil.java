package com.phonelocation.utils;

import org.json.JSONObject;

import android.util.Log;

import com.baidu.location.BDLocation;

public class JSONUtil {
	public static String makeJSON(BDLocation location, String name) {
		try {
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("x", location.getLongitude());
			json.put("y", location.getLatitude());
			json.put("radius", location.getRadius());
			json.put("date", System.currentTimeMillis());
			Log.i("mytag", json.toString());
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
