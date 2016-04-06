package com.lzp.weibo.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NetService extends Service {

	private Sender mSender;

	@Override
	public void onCreate() {
		super.onCreate();
		mSender = Sender.getInstance(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
