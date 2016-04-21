package com.lzp.weibo.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.data.WeiboDatabase.Urls;
import com.lzp.weibo.msg.RequestUrlContasts;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.database.Cursor;
import android.util.Log;

public class WeiboCache {
	private ConcurrentHashMap<String, String> mRequestUrls;
	private static WeiboCache mUrlCache;
	private StatusList mStatusList;// Î¢²©µÄ»º´æ

	private WeiboCache() {
		mRequestUrls = new ConcurrentHashMap<String, String>();
		initWeiboCache();
	}

	public static WeiboCache getUrlCacheInstance() {
		if (mUrlCache == null) {
			mUrlCache = new WeiboCache();
		}
		return mUrlCache;
	}

	public void initWeiboCache() {
		if (mRequestUrls == null || mRequestUrls.isEmpty()) {
			Cursor cursor = BaseApplication.mApplication.getApplicationContext().getContentResolver()
					.query(Urls.CONTENT_URI, null, null, null, null);
			HashMap<String, String> tmpMap = new HashMap<String, String>();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					String requrl = cursor.getString(cursor.getColumnIndex(Urls.URL));
					String response = cursor.getString(cursor.getColumnIndex(Urls.CONTENT));
					tmpMap.put(requrl, response);
				} while (cursor.moveToNext());
			}
			mRequestUrls.putAll(tmpMap);
			initStatusList();
		}
	}

	private void initStatusList() {
		if (!mRequestUrls.isEmpty() && mRequestUrls.containsKey(RequestUrlContasts.FRIENDS_TIMELINE)) {
			String response = mRequestUrls.get(RequestUrlContasts.FRIENDS_TIMELINE);
			mStatusList = StatusList.parse(response);
		}
	}

	public synchronized ConcurrentHashMap<String, String> getReuestUrls() {
		return mRequestUrls;
	}

	public synchronized StatusList getStatusList() {
		return mStatusList;
	}

	public synchronized void setStatusList(StatusList status) {
		mergeStatusList(status);
	}

	private void mergeStatusList(StatusList status) {
		Log.e("Test", "WeiboCache mergeStatusList");
		if (mStatusList == null) {
			mStatusList = status;
		} else {
			mStatusList.hasvisible = status.hasvisible;
			mStatusList.previous_cursor = status.previous_cursor;
			mStatusList.next_cursor = status.next_cursor;
			mStatusList.total_number += status.total_number;
//			Log.e("Test", "WeiboCache mergeStatusList dumpstatuslist first");
//			dumpStatusList(mStatusList.statusList);
			mStatusList.statusList.addAll(0, status.statusList);
//			Log.e("Test", "WeiboCache mergeStatusList dumpstatuslist second");
//			dumpStatusList(mStatusList.statusList);
		}
	}
	
	private void dumpStatusList(ArrayList<Status> status){
		for (Status status2 : status) {
			Log.e("Test", "username="+status2.user.screen_name);
		}
	}
}
