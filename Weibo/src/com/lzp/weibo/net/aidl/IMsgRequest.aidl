package com.lzp.weibo.net.aidl;
import com.lzp.weibo.msg.ToServiceMsg;
import com.lzp.weibo.net.aidl.IMsgResponse;

interface IMsgRequest{
	void register(IMsgResponse callback);
	void unRegister();
	void sendRequest(in ToServiceMsg msg);
}