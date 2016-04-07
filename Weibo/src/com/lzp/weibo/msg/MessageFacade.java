package com.lzp.weibo.msg;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.AppRuntime;
import com.lzp.weibo.app.BaseApplication;

import android.os.RemoteException;

public class MessageFacade {

	public boolean sendRequest(ToServiceMsg msg) {
		if (msg == null) {
			return false;
		}
		AppRuntime runtime = BaseApplication.mApplication.getAppRuntime();
		if (!runtime.isServiceInit()) {
			runtime.init();
		}
		if (runtime.isServiceInit()) {
			try {
				((AppInterface) runtime).sendRequest(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
