package com.lzp.weibo.app;

import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetService;
import com.lzp.weibo.net.aidl.IMsgRequest;
import com.lzp.weibo.net.aidl.IMsgResponse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public abstract class AppRuntime {
	private Context mContext;
	protected IMsgRequest mNetService;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("Test", "AppRuntime onServiceDisconnected");
			mNetService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("Test", "AppRuntime onServiceConnected");
			mNetService = IMsgRequest.Stub.asInterface(service);
			try {
				mNetService.register(mCallback);
				sendAll();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private final IMsgResponse.Stub mCallback = new IMsgResponse.Stub() {

		@Override
		public void onResponse(ToAppMsg msg) throws RemoteException {
			Log.e("Test", "AppRuntime onResponse msg=" + msg);
			onReceive(msg);
		}
	};

	protected abstract void onReceive(ToAppMsg msg);
	protected abstract void sendAll();

	public AppRuntime(Context context) {
		mContext = context;
	}

	public void init() {
		Log.e("Test", "AppRuntime init");
		startNetService();
		bindNetService();
	}

	private void startNetService() {
		Intent intent = new Intent(mContext, NetService.class);
		mContext.startService(intent);
	}

	private void bindNetService() {
		Intent intent = new Intent(mContext, NetService.class);
		mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	public boolean isServiceInit() {
		return mNetService != null;
	}
}
