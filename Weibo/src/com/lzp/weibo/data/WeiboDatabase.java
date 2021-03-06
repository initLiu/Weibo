package com.lzp.weibo.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeiboDatabase {

	/**
	 * Authors table
	 * 
	 * @author SKJP
	 *
	 */
	public static final class Urls implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + WeiboProvider.AUTHORITY + "/urls");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.weibo.urls";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.weibo.urls";
		public static final String DEFAULTSORT_ORDER = "_id ASC";

		// public static final String AUTHOR_ID = "author_id";
		// public static final String NAME = "name";
		// public static final String PROFILE_IMAGE_URL = "profile_image_url";
		// public static final String DESCRIPTION = "description";
		// public static final String FOLLOWERS_COUNT = "followers_count";// ��˿
		// public static final String FRIENDS_COUNT = "friends_count";// ��ע
		// public static final String STATUSES_COUNT = "statuses_count";

		public static final String URL = "url";
		public static final String CONTENT = "content";
	}
}
