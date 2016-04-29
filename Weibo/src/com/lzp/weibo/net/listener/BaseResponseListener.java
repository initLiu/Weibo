package com.lzp.weibo.net.listener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.net.NetCore;

public abstract class BaseResponseListener implements Listener<String>, ErrorListener {

	protected NetCore netCore;
	protected String url;

	public BaseResponseListener(NetCore netCore) {
		this.netCore = netCore;
		setUrl();
	}

	@Override
	public void onResponse(String response) {

	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		ToAppMsg msg = new ToAppMsg();
		msg.setCmd(Command.error);
		msg.setResponse(arg0.networkResponse.statusCode + "");
		netCore.addResponseToQueue(msg);
	}

	/**
	 * 子类实现该方法为url赋值
	 */
	protected abstract void setUrl();

	public String getUrl() {
		return url;
	}
}
