package com.phonelocation.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/*
 * 需要权限 
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 */
public class PhoneStateUtil {

	public static String getPhoneID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneID = tm.getLine1Number();
		String deviceID = tm.getDeviceId();
		Log.i("mytag", "phoneID:" + phoneID);
		Log.i("mytag", "deviceID:" + deviceID);
		return deviceID;
	}
}
