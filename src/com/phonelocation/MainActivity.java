package com.phonelocation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.phonelocation.LocationService.LocationServiceBinder;

public class MainActivity extends Activity {

	public static String ACTION = "com.phonelocation";
	public static String MESSAGE = "message";
	public static String MESSAGE_NOAUTH = "needauth";
	public static String MESSAGE_NEWLOCATION = "location";

	private Button btn_start;
	private Button btn_stop;
	private Button btn_auth;

	private TextView tv_fuwu;
	private TextView tv_dingwei;

	private Intent serviceIntent;
	private LocationService locationService = null;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getExtras().getString(MESSAGE);
			if (message.equals(MESSAGE_NEWLOCATION)) {
				if (locationService != null) {
					BDLocation location = locationService.getLocation();
					tv_dingwei.setText("(" + location.getLongitude() + " ,"
							+ location.getLatitude() + " )"
							+ location.getRadius());
				}
			} else if (message.equals(MESSAGE_NOAUTH)) {
				Toast.makeText(MainActivity.this, "请认证", Toast.LENGTH_LONG)
						.show();
				if (locationService != null) {
					tv_fuwu.setText("定位暂停");
					locationService.stop();
				}
				startActivity(new Intent(MainActivity.this, AuthActivity.class));
			}
		}

	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocationServiceBinder binder = (LocationServiceBinder) service;
			locationService = binder.getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_auth = (Button) findViewById(R.id.btn_auth);
		tv_fuwu = (TextView) findViewById(R.id.tv_fuwu);
		tv_dingwei = (TextView) findViewById(R.id.tv_dingwei);

		serviceIntent = new Intent(MainActivity.this, LocationService.class);
		bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);

		tv_fuwu.setText("定位未启动");

		btn_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationService.start();
				tv_fuwu.setText("正在定位中...");
			}
		});
		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationService.stop();
				tv_fuwu.setText("定位暂停");
			}
		});
		btn_auth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AuthActivity.class);
				startActivity(intent);
			}
		});

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		this.registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		locationService = null;
		unbindService(conn);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
