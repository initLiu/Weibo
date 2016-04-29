package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;

public class MessageHandler extends BusinessHandler {

	public MessageHandler(AppInterface app) {
		super(app);
	}

	@Override
	public void onReceive(ToAppMsg msg) {
		// TODO Auto-generated method stub

	}

	public boolean SendMessageRequest(Command cmd, String url) {
		return false;
	}

	@Override
	public boolean sendRequest(ToServiceMsg msg) {
		return false;
	}

	@Override
	public boolean sendRequest(Command cmd, String url) {
		// TODO Auto-generated method stub
		return false;
	}

}
