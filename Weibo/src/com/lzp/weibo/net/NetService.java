package com.lzp.weibo.net;

import com.lzp.weibo.msg.ToServiceMsg;
import com.lzp.weibo.net.aidl.IMsgRequest;
import com.lzp.weibo.net.aidl.IMsgResponse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 网络层的服务，接收业务层发来的消息，并且将服务器传来的数据传给业务层。
 * 
 * @author SKJP
 *
 */
public class NetService extends Service {

	private Sender mSender;
	private IMsgResponse mCallback;
	private NetCore mNetCore;
	private NetResponseHandler mResponseHandler;

	@Override
	public void onCreate() {
		super.onCreate();
		mNetCore = new NetCore();
		mSender = Sender.getInstance(getApplicationContext(), mNetCore);
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
		public void register(IMsgResponse callback) throws RemoteException {
			mCallback = callback;
			mResponseHandler = new NetResponseHandler(mNetCore, mCallback);
			mResponseHandler.start();
		}

		@Override
		public void sendRequest(ToServiceMsg msg) throws RemoteException {
			Log.e("Test", "NetService sendRequest msg=" + msg);
			if (msg != null) {
				mSender.addToRequestQueue(msg);
			}
		}

		@Override
		public void unRegister() throws RemoteException {
			mCallback = null;
			mResponseHandler.interrupt();
		}
	};

	@Override
	public boolean onUnbind(Intent intent) {
		mCallback = null;
		mResponseHandler.interrupt();
		return super.onUnbind(intent);
	}

}
