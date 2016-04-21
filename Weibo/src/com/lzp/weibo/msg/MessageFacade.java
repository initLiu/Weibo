package com.lzp.weibo.msg;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.cache.WeiboCache;
import com.lzp.weibo.data.DataItem;
import com.lzp.weibo.data.WeiboDatabase.Urls;
import com.lzp.weibo.msg.handler.FriendsTimelineHandler;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MessageFacade extends Observable {

	private static final String TAG = MessageFacade.class.getSimpleName();
	private AppInterface mApp;
	private WeiboCache mWeiboCache;

	public MessageFacade(AppInterface app) {
		mApp = app;
		mWeiboCache = WeiboCache.getUrlCacheInstance();
	}

	/**
	 * ����json����
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
	 * ���յ��������Ļذ�
	 * 
	 * @param cmd
	 * @param reqUrl
	 * @param response
	 */
	public void receiveResponse(Command cmd, String cacheUrl, String response) {
		Log.e(TAG, "MessageFacade receiveResponse");
		insert2DB(cmd, cacheUrl, response);
		insert2Cache(cmd, cacheUrl, response);
		notifyUI(cmd, response);
	}

	/**
	 * ��url���󱣴浽���ݿ���
	 * 
	 * @param cmd
	 * @param response
	 */
	private void insert2DB(Command cmd, String cacheUrl, String response) {
		Log.e(TAG, "MessageFacade insert2DB");
		Uri uri = Urls.CONTENT_URI;

		ContentValues values = new ContentValues();
		values.put(Urls.URL, cacheUrl);
		values.put(Urls.CONTENT, response);

		ContentValues valuestmp = new ContentValues();
		valuestmp.put(Urls.CONTENT, response);

		DataItem item = new DataItem();
		item.uri = uri;
		item.contentValues = values;
		item.contentValuesTmp = valuestmp;
		item.where = Urls.URL + " = ?";
		item.selectionArgs = new String[] { cacheUrl };
		item.action = DataItem.ACTION_UPDATE_INSERT;
		mApp.getWeiboDatabaseManager().addMsgQueue(item);
	}

	/**
	 * ֪ͨui����
	 * 
	 * @param cmd
	 * @param response
	 */
	private void notifyUI(Command cmd, String response) {
		Log.e(TAG, "MessageFacade notifyUI cmd="+cmd.toString());
		ObserverData data = new ObserverData();
		data.cmd = cmd;

		if (cmd == Command.owner_users_show) {
			try {
				JSONObject owneruser = new JSONObject(response);
				String url = owneruser.getString("avatar_large");
				data.data = url;
				setChanged();
				notifyObservers(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (cmd == Command.friends_timeline) {
			boolean update = !TextUtils.isEmpty(response);
			data.data = update;
			setChanged();
			notifyObservers(data);
		}
	}

	public static class ObserverData {
		public Command cmd;
		public Object data;
	}

	/************************************ ������� ***********************************/
	/*****************************************************************************/
	/**
	 * ��������뵽������
	 * 
	 * @param cmd
	 * @param cacheUrl
	 * @param response
	 */
	private void insert2Cache(Command cmd, String cacheUrl, String response) {
		Log.e(TAG, "MessageFacade insert2Cache");
		// ����url����
		mWeiboCache.getReuestUrls().put(cacheUrl, response);

		// ����url���󷵻ص�����
		if (cmd == Command.owner_users_show) {// �˺���Ϣ

		} else if (cmd == Command.friends_timeline) {// ΢���б�
			mWeiboCache.setStatusList(StatusList.parse(response));
		}
	}

	/**
	 * ��url������ȡ��֮ǰ������
	 * 
	 * @param cmd
	 * @return response
	 */
	public String getResponseFromCache(Command cmd) {
		Log.e(TAG, "MessageFacade getResponseFromCache");
		ConcurrentHashMap<String, String> requestUrls = mWeiboCache.getReuestUrls();
		if (requestUrls == null) {
			mWeiboCache.initWeiboCache();
		}

		String url = null;
		if (cmd == Command.owner_users_show) {
			url = RequestUrlContasts.OWNER_USER_SHOW;

		} else if (cmd == Command.friends_timeline) {
			url = RequestUrlContasts.FRIENDS_TIMELINE;
		}

		if (TextUtils.isEmpty(url) || !requestUrls.containsKey(url)) {
			return null;
		}
		return requestUrls.get(url);
	}

	public StatusList getStatusListFromCache() {
		if (mWeiboCache.getReuestUrls() == null) {
			mWeiboCache.initWeiboCache();
		}
		return mWeiboCache.getStatusList();
	}
}
