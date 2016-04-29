package com.lzp.weibo.msg.handler;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToServiceMsg;

import android.text.TextUtils;
import android.util.Log;

public class FriendsTimelineHandlerOld extends FriendsTimelineHandler{

	public FriendsTimelineHandlerOld(AppInterface app) {
		super(app);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean sendRequest(Command cmd, String url) {
		Log.e("Test", "FriendsTimelineHandlerOld sendRequest cmd=" + cmd.name() + ",url=" + url);
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		ToServiceMsg msg = new ToServiceMsg();
		msg.setCmd(Command.friends_timeline_old);
		msg.setUrl(url);
		return sendRequest(msg);
	}
}
