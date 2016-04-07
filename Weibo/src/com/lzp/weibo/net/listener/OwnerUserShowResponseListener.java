package com.lzp.weibo.net.listener;

import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetCore;

public class OwnerUserShowResponseListener extends BaseResponseListener {

	public OwnerUserShowResponseListener(NetCore netCore) {
		super(netCore);
	}

	@Override
	public void onResponse(String response) {
		ToAppMsg msg = new ToAppMsg();
		msg.setCmd(Command.ower_users_show);
		msg.setResponse(response);
		netCore.addResponseToQueue(msg);
	}
}
