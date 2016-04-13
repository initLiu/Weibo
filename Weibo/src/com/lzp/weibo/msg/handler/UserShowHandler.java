package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class UserShowHandler extends BusinessHandler {

	public UserShowHandler(AppInterface app) {
		super(app);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(ToAppMsg msg) {
		mApp.getMessageFacade().receiveResponse(msg.getCmd(), msg.getResponse());
	}

	@Override
	public boolean sendRequest(ToServiceMsg msg) {
		Log.e("Test", "UserShowHandler sendRequest");
		if (msg == null) {
			return false;
		}
		try {
			mApp.sendRequest(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean sendRequest(Command cmd, String url) {
		Log.e("Test", "UserShowHandler sendRequest cmd=" + cmd.name() + ",url=" + url);
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		ToServiceMsg msg = new ToServiceMsg();
		msg.setCmd(Command.owner_users_show);
		msg.setUrl(url);
		return sendRequest(msg);
	}
}
