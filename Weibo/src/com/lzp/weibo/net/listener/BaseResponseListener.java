package com.lzp.weibo.net.listener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.lzp.weibo.net.NetCore;
import com.android.volley.VolleyError;

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
		// TODO Auto-generated method stub

	}

	/**
	 * 子类实现该方法为url赋值
	 */
	protected abstract void setUrl();

	public String getUrl() {
		return url;
	}
}
