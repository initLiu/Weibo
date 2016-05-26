package com.lzp.weibo.msg;

import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.cache.WeiboCache;
import com.lzp.weibo.data.DataItem;
import com.lzp.weibo.data.WeiboDatabase.Urls;
import com.lzp.weibo.msg.handler.BusinessHandler;
import com.lzp.weibo.msg.handler.FriendsTimelineHandler;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

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
		BusinessHandler handler = null;
		if (cmd == Command.owner_users_show) {
			handler = mApp.getBusinessHandler(AppInterface.USERSHOW_HANDLER);
		} else if (cmd == Command.friends_timeline) {
			handler = mApp.getBusinessHandler(AppInterface.FRIENDSTIMELINE_HANDLER);
		} else if (cmd == Command.friends_timeline_old) {
			handler = mApp.getBusinessHandler(AppInterface.FRIENDSTIMELINE_HANDLER_OLD);
		} else if (cmd == Command.comments) {
			handler = mApp.getBusinessHandler(AppInterface.COMMENTS);
		}
		return handler.sendRequest(cmd, url);
	}

	/**
	 * 接收到服务器的回包
	 * 
	 * @param cmd
	 * @param reqUrl
	 * @param response
	 */
	public void receiveResponse(Command cmd, String cacheUrl, String response) {
		Log.e(TAG, "MessageFacade receiveResponse");
		if (TextUtils.isEmpty(response)) {
			notifyUI(cmd, null);
			return;
		}

		Object data = null;
		if (cmd == Command.owner_users_show) {
			data = User.parse(response);
		} else if ((cmd == Command.friends_timeline) || (cmd == Command.friends_timeline_old)) {
			StatusList statusList = StatusList.parse(response);
			if (statusList == null || statusList.statusList == null || statusList.statusList.isEmpty()) {
				notifyUI(cmd, null);
				return;
			}
			data = statusList;
		} else if (cmd == Command.comments) {
			data = CommentList.parse(response);
		}

		insert2DB(cmd, cacheUrl, response);
		insert2Cache(cmd, cacheUrl, response, data);
		notifyUI(cmd, data);
	}

	public void receiveErrorResponse(String msg){
		notifyUI(Command.error, msg);
	}
	
	/**
	 * 将url请求保存到数据库中
	 * 
	 * @param cmd
	 * @param response
	 */
	private void insert2DB(Command cmd, String cacheUrl, String response) {
		Log.e(TAG, "MessageFacade insert2DB");
		if (TextUtils.isEmpty(cacheUrl) || cmd == Command.friends_timeline_old || cmd == Command.comments) {
			return;
		}
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
	 * 通知ui更新
	 * 
	 * @param cmd
	 * @param response
	 */
	private void notifyUI(Command cmd, Object response) {
		Log.e(TAG, "MessageFacade notifyUI cmd=" + cmd.toString());
		ObserverData data = new ObserverData();
		data.cmd = cmd;

		if (cmd == Command.owner_users_show) {
			if (response != null) {
				User user = (User) response;
				data.data = user.avatar_large;
				setChanged();
				notifyObservers(data);
			}
		} else if ((cmd == Command.friends_timeline)||(cmd == Command.friends_timeline_old)) {
			boolean update = response != null;
			data.data = update;
			setChanged();
			notifyObservers(data);
		} else if (cmd == Command.error) {
			setChanged();
			data.data = response;
			notifyObservers(data);
		} else if (cmd == Command.comments) {
			setChanged();
			data.data = response;
			notifyObservers(data);
		}
	}

	public static class ObserverData {
		public Command cmd;
		public Object data;
	}

	/************************************ 缓存相关 ***********************************/
	/*****************************************************************************/
	/**
	 * 将请求插入到缓存中
	 * 
	 * @param cmd
	 * @param cacheUrl
	 * @param response
	 */
	private void insert2Cache(Command cmd, String cacheUrl, String response, Object data) {
		Log.e(TAG, "MessageFacade insert2Cache");
		if (TextUtils.isEmpty(cacheUrl)) {
			return;
		}
		// 缓存url请求返回的数据
		if (cmd == Command.owner_users_show) {// 账号信息
			// 缓存url请求
			mWeiboCache.getReuestUrls().put(cacheUrl, response);
		} else if (cmd == Command.friends_timeline) {// 微博列表
			// 缓存url请求
			mWeiboCache.getReuestUrls().put(cacheUrl, response);
			
			mWeiboCache.setStatusList((StatusList) data, true);
		} else if (cmd == Command.friends_timeline_old) {
			mWeiboCache.setStatusList((StatusList) data, false);
		}
	}

	/**
	 * 从url缓存中取出之前的请求
	 * 
	 * @param cmd
	 * @return response
	 */
	public String getResponseFromCache(Command cmd) {
		Log.e(TAG, "MessageFacade getResponseFromCache");
		ConcurrentHashMap<String, String> requestUrls = mWeiboCache.getReuestUrls();

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
		Log.e("Test", "MessageFacade getStatusListFromCache");
		return mWeiboCache.getStatusList();
	}

	public String getSinceId() {
		return mWeiboCache.getSinceId();
	}

	public String getMaxId() {
		return mWeiboCache.getMaxId();
	}
}
