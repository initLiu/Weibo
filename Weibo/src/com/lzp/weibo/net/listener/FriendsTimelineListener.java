package com.lzp.weibo.net.listener;

import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.RequestUrlContasts;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetCore;

import android.util.Log;

public class FriendsTimelineListener extends BaseResponseListener {

	public FriendsTimelineListener(NetCore netCore) {
		super(netCore);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResponse(String response) {
		Log.e("Test", "FriendsTimelineListener onResponse response=" + response);
		ToAppMsg msg = new ToAppMsg();
		msg.setCmd(Command.friends_timeline);
		msg.setUrl(url);
		msg.setResponse(response);
		netCore.addResponseToQueue(msg);
	}

	@Override
	protected void setUrl() {
		url = RequestUrlContasts.FRIENDS_TIMELINE;
	}

}
