package com.lzp.weibo.net;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.lzp.weibo.net.aidl.IMsgRequest;

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
		return mBinder;
	}

	private final IMsgRequest.Stub mBinder = new IMsgRequest.Stub() {

		@Override
		public void sendRequest(String url) throws RemoteException {
			
		}
	};

}
