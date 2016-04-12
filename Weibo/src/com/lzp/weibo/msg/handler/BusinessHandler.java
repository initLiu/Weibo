package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;

public abstract class BusinessHandler {
	protected AppInterface mApp;

	public BusinessHandler(AppInterface app) {
		mApp = app;
	}

	public abstract void onReceive(ToAppMsg msg);

	public abstract boolean sendRequest(ToServiceMsg msg);
}
