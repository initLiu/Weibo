package com.lzp.weibo.msg;

import com.lzp.weibo.app.AppInterface;

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

		return mApp.getMessageHandler().SendMessageRequest(cmd, url);
	}
}
