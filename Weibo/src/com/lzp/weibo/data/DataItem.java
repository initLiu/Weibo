package com.lzp.weibo.data;

import android.content.ContentValues;
import android.net.Uri;

public class DataItem {
	public static final int ACTION_INSERT = 0;
	public static final int ACTION_UPDATE = 1;
	public static final int ACTION_DELETE = 2;

	public Uri uri;
	public ContentValues contentValues;
	public String[] projection;
	public String where;
	public String[] selectionArgs;
	public String sortOrder;
	public int action;
}
