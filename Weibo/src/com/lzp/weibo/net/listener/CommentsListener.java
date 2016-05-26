package com.lzp.weibo.net.listener;

import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetCore;

public class CommentsListener extends BaseResponseListener {

	public CommentsListener(NetCore netCore) {
		super(netCore);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResponse(String response) {
		ToAppMsg msg = new ToAppMsg();
		msg.setCmd(Command.comments);
		msg.setUrl(null);
		msg.setResponse(response);
		netCore.addResponseToQueue(msg);
	}

	@Override
	protected void setUrl() {
	}
}
