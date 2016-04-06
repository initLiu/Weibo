package com.lzp.weibo.msg;

import android.os.RemoteException;
import android.text.TextUtils;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.AppRuntime;
import com.lzp.weibo.app.BaseApplication;

public class MessageFacade {

	public boolean sendRequest(String url) {
		if (TextUtils.isEmpty(url)) {
			return true;
		}
		AppRuntime runtime = BaseApplication.mApplication.getAppRuntime();
		if (!runtime.isServiceInit()) {
			runtime.init();
		}
		if (runtime.isServiceInit()) {
			try {
				((AppInterface) runtime).sendRequest(url);
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
