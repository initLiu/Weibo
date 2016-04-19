package com.lzp.weibo.cache;

import java.util.concurrent.ConcurrentHashMap;

public class WeiboCache {
	private ConcurrentHashMap<String, String> mRequestUrls;
	private static WeiboCache mUrlCache;

	public static WeiboCache getUrlCacheInstance() {
		if (mUrlCache == null) {
			mUrlCache = new WeiboCache();
		}
		return mUrlCache;
	}

	private WeiboCache() {
		mRequestUrls = new ConcurrentHashMap<String, String>();
	}

	public ConcurrentHashMap<String, String> getReuestUrls() {
		return mRequestUrls;
	}
}
