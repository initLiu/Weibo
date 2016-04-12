package com.lzp.weibo.net.listener;

import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetCore;

import android.util.Log;

public class OwnerUserShowResponseListener extends BaseResponseListener {

	public OwnerUserShowResponseListener(NetCore netCore) {
		super(netCore);
	}

	@Override
	public void onResponse(String response) {
		Log.e("Test", "OwnerUserShowResponseListener onResponse");
		ToAppMsg msg = new ToAppMsg();
		msg.setCmd(Command.owner_users_show);
		msg.setResponse(response);
		netCore.addResponseToQueue(msg);
	}
}
