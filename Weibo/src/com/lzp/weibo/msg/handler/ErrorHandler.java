package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.ToAppMsg;

public class ErrorHandler extends BusinessHandler{

	public ErrorHandler(AppInterface app) {
		super(app);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(ToAppMsg msg) {
		mApp.getMessageFacade().receiveErrorResponse(msg.getResponse());
	}
	
}
