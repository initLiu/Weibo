package com.lzp.weibo.net;

import java.util.concurrent.LinkedBlockingQueue;

import com.lzp.weibo.msg.ToAppMsg;

import android.util.Log;

public class NetCore {

	private LinkedBlockingQueue<ToAppMsg> msgQueue = new LinkedBlockingQueue<ToAppMsg>();

	public void addResponseToQueue(ToAppMsg msg) {
		try {
			Log.e("Test", "NetCore before addResponseToQueue");
			msgQueue.put(msg);
			Log.e("Test", "NetCore after addResponseToQueue");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public LinkedBlockingQueue<ToAppMsg> getMsgQueue() {
		return msgQueue;
	}
}
