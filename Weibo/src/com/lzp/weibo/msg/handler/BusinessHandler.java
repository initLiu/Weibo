package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;

import android.os.RemoteException;
import android.util.Log;

public abstract class BusinessHandler {
	protected AppInterface mApp;

	public BusinessHandler(AppInterface app) {
		mApp = app;
	}

	public abstract void onReceive(ToAppMsg msg);

	public boolean sendRequest(ToServiceMsg msg){
		Log.e("Test", "BusinessHandler sendRequest");
		if (msg == null) {
			return false;
		}
		try {
			mApp.sendRequest(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
}
