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

public abstract class AppRuntime {
	private Context mContext;
	protected IMsgRequest mNetService;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mNetService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mNetService = IMsgRequest.Stub.asInterface(service);
			try {
				mNetService.register(mCallback);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private final IMsgResponse.Stub mCallback = new IMsgResponse.Stub() {

		@Override
		public void onResponse(ToAppMsg msg) throws RemoteException {

		}
	};

	public AppRuntime(Context context) {
		mContext = context;
	}

	public void init() {
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
