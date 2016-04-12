package com.lzp.weibo.app;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

public class BaseApplication extends Application {

	public static BaseApplication mApplication;
	private static AppRuntime mAppRuntime;
	public static String processName;

	public static final String WEIBO_PROCESS = "com.lzp.weibo";
	public static final String WEIBO_NET_SERVICE_PROCESS = "com.lzp.weibo:netservice";

	@Override
	public void onCreate() {
		Log.e("Test", "BaseApplication onCreate");
		super.onCreate();
		mApplication = this;
		initRuntime();
	}

	private void initRuntime() {
		Log.e("Test", "BaseApplication initRuntime mAppRuntime=" + mAppRuntime);
		String process = getProcessName();
		if (WEIBO_PROCESS.equals(process)) {
			if (mAppRuntime == null) {
				mAppRuntime = new AppInterface(getApplicationContext());
				mAppRuntime.init();
			}
		}
	}

	public AppRuntime getAppRuntime() {
		return mAppRuntime;
	}

	public String getProcessName() {
		if (processName == null) {
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();
			if (null != appList) {
				for (RunningAppProcessInfo info : appList) {
					if (info.pid == Process.myPid()) {
						processName = info.processName;
						break;
					}
				}
			}
		}
		if (processName == null) {
			processName = getApplicationInfo().processName;
		}
		if (processName == null) {
			processName = WEIBO_PROCESS;
		}
		return processName;
	}
}
