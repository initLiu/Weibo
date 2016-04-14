package com.lzp.weibo.msg;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.data.DataItem;
import com.lzp.weibo.data.WeiboDatabase.Users;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;
import com.sina.weibo.sdk.openapi.models.User;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MessageFacade {

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

	public void receiveResponse(Command cmd, String response) {
		Log.e("Test", "MessageFacade receiveResponse");
		insert2DB(cmd, response);
	}

	/**
	 * 保存到数据库中
	 * 
	 * @param cmd
	 * @param response
	 */
	private void insert2DB(Command cmd, String response) {
		Log.e("Test", "MessageFacade insert2DB");
		if (cmd == Command.owner_users_show) {
			Uri uri = Users.CONTENT_URI;
			User user = User.parse(response);

			DataItem item = new DataItem();
			item.uri = uri;
			item.where = Users.AUTHOR_ID + " = ?";
			item.selectionArgs = new String[] { user.id };
			item.action = DataItem.ACTION_DELETE;
			mApp.getWeiboDatabaseManager().addMsgQueue(item);

			ContentValues values = new ContentValues();
			values.put(Users.AUTHOR_ID, user.id);
			values.put(Users.NAME, user.name);
			values.put(Users.PROFILE_IMAGE_URL, user.profile_image_url);
			values.put(Users.DESCRIPTION, user.description);
			values.put(Users.FOLLOWERS_COUNT, user.followers_count);
			values.put(Users.FRIENDS_COUNT, user.friends_count);
			values.put(Users.STATUSES_COUNT, user.statuses_count);

			item = new DataItem();
			item.uri = uri;
			item.contentValues = values;
			item.action = DataItem.ACTION_INSERT;
			mApp.getWeiboDatabaseManager().addMsgQueue(item);
		}
	}
}
