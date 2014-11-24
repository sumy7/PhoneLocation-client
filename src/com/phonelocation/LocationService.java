package com.phonelocation;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.phonelocation.utils.JSONUtil;
import com.phonelocation.utils.MD5;
import com.phonelocation.utils.PhoneStateUtil;
import com.phonelocation.utils.PropertiesUtil;
import com.phonelocation.utils.SendPostRequestUtil;

public class LocationService extends Service {

    private static final int UPDATE_TIME = 30000;

    private BDLocation tmpLocation;
    private boolean iswork = false;
    private int location_count = 0;

    private String serverURL = "";
    private String phoneID;
    private String phoneIDMD5;
    private String tokenid = "";

    private LocationClient locationClient;

    private LocationServiceBinder binder = new LocationService.LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    /**
     * 返回当前位置
     */
    public BDLocation getLocation() {
        return this.tmpLocation;
    }

    /**
     * 开始获取位置信息
     */
    public boolean start() {
        tokenid = PropertiesUtil.getTokenId(this);
        if (locationClient.isStarted())
            return false;
        locationClient.start();
        locationClient.requestLocation();
        iswork = true;
        return true;
    }

    /**
     * 停止获取位置信息
     */
    public boolean stop() {
        if (!locationClient.isStarted())
            return false;
        locationClient.stop();
        iswork = false;
        return true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    Runnable sendLocationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!iswork)
                return;
            if (serverURL == "") {
                // 从配置文件中获取服务器地址
                serverURL = PropertiesUtil.getProperties(LocationService.this)
                        .getProperty("serverUrl");
            }
            // 尝试将位置信息发送到服务器
            int status = SendPostRequestUtil.sendJSONRequest(
                    JSONUtil.makeJSON(tmpLocation, phoneIDMD5), serverURL,
                    tokenid);
            if (status != 200) {
                // 没有返回成功信息，可能是未认证，跳转到认证界面
                stop();
                Intent mIntent = new Intent(MainActivity.ACTION);
                mIntent.putExtra(MainActivity.MESSAGE,
                        MainActivity.MESSAGE_NOAUTH);
                sendBroadcast(mIntent);
            }
        }
    };

    @Override
    public void onCreate() {
        phoneID = PhoneStateUtil.getPhoneID(this);
        phoneIDMD5 = MD5.string2MD5(phoneID);

        iswork = false;
        locationClient = new LocationClient(this);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 设置定位模式
        option.setProdName("LocationDemo"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);

        // 注册位置变化监听器
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                tmpLocation = location;
                location_count++;
                new Thread(sendLocationRunnable).start(); // 将位置信息发送到服务器

                // 将位置信息发送到主界面
                Intent mIntent = new Intent(MainActivity.ACTION);
                mIntent.putExtra(MainActivity.MESSAGE,
                        MainActivity.MESSAGE_NEWLOCATION);
                sendBroadcast(mIntent);
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
