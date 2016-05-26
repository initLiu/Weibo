package com.lzp.weibo.net;

import java.net.URL;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lzp.weibo.msg.ToServiceMsg;
import com.lzp.weibo.net.listener.BaseResponseListener;
import com.lzp.weibo.net.listener.CommentsListener;
import com.lzp.weibo.net.listener.ResponListenerCreator;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class Sender {
	private static Sender mInstance;
	private RequestQueue mRequestQueue;
	private static Context mContext;
	private ResponListenerCreator mResponListenerCreator;
	private NetCore mNetCore;

	private Sender(Context context, NetCore netCore) {
		mContext = context;
		mRequestQueue = getRequestQueue();
		mNetCore = netCore;
		mResponListenerCreator = new ResponListenerCreator(mNetCore);
	}

	public static synchronized Sender getInstance(Context context, NetCore netCore) {
		if (mInstance == null) {
			mInstance = new Sender(context, netCore);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
		}
		return mRequestQueue;
	}

	public void addToRequestQueue(ToServiceMsg msg) {
		Log.e("Test", "Sender addToRequestQueue mRequestQueue=" + mRequestQueue);
		if (mRequestQueue != null) {
			BaseResponseListener listener = mResponListenerCreator.createResponListener(msg.getCmd());
			StringRequest request = new StringRequest(Method.GET, msg.getUrl(), listener, listener);
			addToRequestQueue(request);
		}
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}
}
