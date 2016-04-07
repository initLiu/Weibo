package com.lzp.weibo.net;

import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.aidl.IMsgResponse;

public class NetResponseHandler extends Thread {

	private NetCore mNetCore;
	public volatile boolean running = true;
	private IMsgResponse mCallback;

	public NetResponseHandler(NetCore netCore, IMsgResponse callback) {
		mNetCore = netCore;
		mCallback = callback;
	}

	@Override
	public void run() {
		while (running) {
			ToAppMsg msg;
			try {
				msg = mNetCore.getMsgQueue().take();
				if (msg != null) {
					mCallback.onResponse(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
