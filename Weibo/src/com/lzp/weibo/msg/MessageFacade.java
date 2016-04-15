package com.lzp.weibo.msg;

import java.util.Observable;

import org.json.JSONObject;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.data.DataItem;
import com.lzp.weibo.data.WeiboDatabase.Urls;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MessageFacade extends Observable {

	private AppInterface mApp;

	public MessageFacade(AppInterface app) {
		mApp = app;
	}

	public boolean sendRequest(Command cmd, String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		if (cmd == Command.owner_users_show) {
			OwnerUserShowHandler handler = (OwnerUserShowHandler) mApp
					.getBusinessHandler(AppInterface.USERSHOW_HANDLER);
			return handler.sendRequest(cmd, url);
		}
		return false;
	}

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

	private void notifyUI(Command cmd, String response) {
		if (cmd == Command.owner_users_show) {
			try {
				JSONObject owneruser = new JSONObject(response);
				String url = owneruser.getString("profile_image_url");
				setChanged();
				notifyObservers(url);
				Log.e("Test", "MessageFacade notifyUI");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
