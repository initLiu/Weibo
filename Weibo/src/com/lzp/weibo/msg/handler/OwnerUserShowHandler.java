package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;

import android.text.TextUtils;
import android.util.Log;

public class OwnerUserShowHandler extends BusinessHandler {

	public OwnerUserShowHandler(AppInterface app) {
		super(app);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(ToAppMsg msg) {
		Log.e("Test", "OwnerUserShowHandler onReceive");
		mApp.getMessageFacade().receiveResponse(msg.getCmd(), msg.getUrl(), msg.getResponse());
	}

	public boolean sendRequest(Command cmd, String url) {
		Log.e("Test", "OwnerUserShowHandler sendRequest cmd=" + cmd.name() + ",url=" + url);
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		ToServiceMsg msg = new ToServiceMsg();
		msg.setCmd(Command.owner_users_show);
		msg.setUrl(url);
		return sendRequest(msg);
	}
}
