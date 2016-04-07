package com.lzp.weibo.net;

import java.util.concurrent.LinkedBlockingQueue;

import com.lzp.weibo.msg.ToAppMsg;

public class NetCore {

	private LinkedBlockingQueue<ToAppMsg> msgQueue = new LinkedBlockingQueue<ToAppMsg>();

	public void addResponseToQueue(ToAppMsg msg) {
		msgQueue.add(msg);
	}

	public LinkedBlockingQueue<ToAppMsg> getMsgQueue() {
		return msgQueue;
	}
}
