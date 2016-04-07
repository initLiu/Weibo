package com.lzp.weibo.net.listener;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.lzp.weibo.net.NetCore;
import com.android.volley.VolleyError;

public class BaseResponseListener implements Listener<String>, ErrorListener {

	protected NetCore netCore;
	
	public BaseResponseListener(NetCore netCore) {
		this.netCore = netCore;
	}

	@Override
	public void onResponse(String response) {
	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		// TODO Auto-generated method stub

	}
}
