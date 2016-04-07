package com.lzp.weibo.net.aidl;

import com.lzp.weibo.msg.ToAppMsg;

interface IMsgResponse{
	void onResponse(in ToAppMsg msg);
}