package com.lzp.weibo.utils;

import android.content.Context;
import android.view.WindowManager;

public class Utils {
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay().getWidth();
	}

	public static int dp2px(Context context, int dp) {
		float scrall = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scrall + 0.5f);
	}

	public static int px2dp(Context context, int px) {
		float scrall = context.getResources().getDisplayMetrics().density;
		return (int) (px / scrall + 0.5f);
	}
}
