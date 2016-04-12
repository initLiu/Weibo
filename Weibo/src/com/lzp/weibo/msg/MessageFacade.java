package com.lzp.weibo.msg;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.handler.UserShowHandler;

import android.text.TextUtils;

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
			UserShowHandler handler = (UserShowHandler) mApp.getBusinessHandler(AppInterface.USERSHOW_HANDLER);
			return handler.sendRequest(cmd, url);
		}
		return false;
	}
}
