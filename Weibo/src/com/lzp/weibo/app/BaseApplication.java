package com.lzp.weibo.app;

import android.app.Application;

public class BaseApplication extends Application {

	public static BaseApplication mApplication;
	private AppRuntime mAppRuntime;
	private AppInterface mAppinterface;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		mAppinterface = new AppInterface();
		initRuntime();
	}

	public AppInterface getAppinterface() {
		if (mAppinterface == null) {
			mAppinterface = new AppInterface();
		}
		return mAppinterface;
	}

	private void initRuntime() {
		mAppRuntime = new AppRuntime(getApplicationContext());
		mAppRuntime.init();
	}
}
