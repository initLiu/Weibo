package com.lzp.weibo.app;

import android.app.Application;

public class BaseApplication extends Application {

	public static BaseApplication mApplication;
	private AppRuntime mAppRuntime;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		initRuntime();
	}

	private void initRuntime() {
		if (mAppRuntime == null) {
			mAppRuntime = new AppInterface(getApplicationContext());
			mAppRuntime.init();
		}
	}

	public AppRuntime getAppRuntime() {
		return mAppRuntime;
	}
}
