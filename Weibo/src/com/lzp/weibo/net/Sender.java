package com.lzp.weibo.net;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Context;

public class Sender {
	private static Sender mInstance;
	private RequestQueue mRequestQueue;
	private static Context mContext;

	private Sender(Context context) {
		mContext = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized Sender getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Sender(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}
}
