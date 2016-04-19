package com.lzp.weibo.msg;

import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.cache.WeiboCache;
import com.lzp.weibo.data.DataItem;
import com.lzp.weibo.data.WeiboDatabase.Urls;
import com.lzp.weibo.msg.handler.FriendsTimelineHandler;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MessageFacade extends Observable {

	private AppInterface mApp;
	private WeiboCache mUrlCache;

	public MessageFacade(AppInterface app) {
		mApp = app;
		mUrlCache = WeiboCache.getUrlCacheInstance();
	}

	/**
	 * 发送json请求
	 * 
	 * @param cmd
	 * @param url
	 * @return
	 */
	public boolean sendRequest(Command cmd, String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		if (cmd == Command.owner_users_show) {
			OwnerUserShowHandler handler = (OwnerUserShowHandler) mApp
					.getBusinessHandler(AppInterface.USERSHOW_HANDLER);
			return handler.sendRequest(cmd, url);
		} else if (cmd == Command.friends_timeline) {
			FriendsTimelineHandler handler = (FriendsTimelineHandler) mApp
					.getBusinessHandler(AppInterface.FRIENDSTIMELINE_HANDLER);
			return handler.sendRequest(cmd, url);
		}
		return false;
	}

	/**
	 * 接收到服务器的回包
	 * 
	 * @param cmd
	 * @param reqUrl
	 * @param response
	 */
	public void receiveResponse(Command cmd, String reqUrl, String response) {
		Log.e("Test", "MessageFacade receiveResponse");
		insert2DB(cmd, reqUrl, response);
	}

	/**
	 * 将url请求保存到数据库中
	 * 
	 * @param cmd
	 * @param response
	 */
	private void insert2DB(Command cmd, String reqUrl, String response) {
		Log.e("Test", "MessageFacade insert2DB");
		Uri uri = Urls.CONTENT_URI;

		ContentValues values = new ContentValues();
		values.put(Urls.URL, reqUrl);
		values.put(Urls.CONTENT, response);

		ContentValues valuestmp = new ContentValues();
		valuestmp.put(Urls.CONTENT, response);

		DataItem item = new DataItem();
		item.uri = uri;
		item.contentValues = values;
		item.contentValuesTmp = valuestmp;
		item.where = Urls.URL + " = ?";
		item.selectionArgs = new String[] { reqUrl };
		item.action = DataItem.ACTION_UPDATE_INSERT;
		mApp.getWeiboDatabaseManager().addMsgQueue(item);
		notifyUI(cmd, response);
	}

	/**
	 * 通知ui更新
	 * 
	 * @param cmd
	 * @param response
	 */
	private void notifyUI(Command cmd, String response) {
		if (cmd == Command.owner_users_show) {
			try {
				JSONObject owneruser = new JSONObject(response);
				String url = owneruser.getString("avatar_large");
				setChanged();
				notifyObservers(url);
				Log.e("Test", "MessageFacade notifyUI");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getResponseFromCache(Command cmd) {
		Log.e("Test", "MessageFacade getResponseFromCache");
		ConcurrentHashMap<String, String> requestUrls = mUrlCache.getReuestUrls();
		if (requestUrls == null || requestUrls.isEmpty()) {
			initUrlCache();
		}

		if (cmd == Command.owner_users_show) {
			String url = RequestUrlContasts.OWNER_USER_SHOW;
			if (!requestUrls.containsKey(url)) {
				return null;
			}
			return requestUrls.get(url);
		}
		return null;
	}

	private void initUrlCache() {
		Log.e("Test", "MessageFacade initUrlCache");
		ConcurrentHashMap<String, String> requestUrls = mUrlCache.getReuestUrls();
		if (requestUrls == null || requestUrls.isEmpty()) {
			Cursor cursor = mApp.getContext().getContentResolver().query(Urls.CONTENT_URI, null, null, null, null);
			HashMap<String, String> tmpMap = new HashMap<String, String>();
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					String requrl = cursor.getString(cursor.getColumnIndex(Urls.URL));
					String response = cursor.getString(cursor.getColumnIndex(Urls.CONTENT));
					tmpMap.put(requrl, response);
				} while (cursor.moveToNext());
			}
			requestUrls.putAll(tmpMap);
		}
	}
}
