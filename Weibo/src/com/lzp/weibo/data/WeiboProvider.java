package com.lzp.weibo.data;

import java.util.HashMap;

import com.lzp.weibo.data.WeiboDatabase.Urls;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class WeiboProvider extends ContentProvider {

	private static final String TAG = WeiboProvider.class.getSimpleName();

	private static final String DATABASE_NAME = "weibo.sqlite";

	private static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = WeiboProvider.class.getName();

	private static final UriMatcher sUriMatcher;

	private DatabaseHelper mOpenHelper;

	private static HashMap<String, String> sUsersProjectionMap = new HashMap<String, String>();

	private static final String URL_TABLE_NAME = "urls";

	private static final int URLS = 1;
	private static final int URL_ID = 2;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, URL_TABLE_NAME, URLS);
		sUriMatcher.addURI(AUTHORITY, URL_TABLE_NAME + "/#", URL_ID);

		sUsersProjectionMap.put(Urls._ID, Urls._ID);
		sUsersProjectionMap.put(Urls.URL, Urls.URL);
		sUsersProjectionMap.put(Urls.CONTENT, Urls.CONTENT);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return (mOpenHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case URLS:
			return Urls.CONTENT_TYPE;
		case URL_ID:
			return Urls.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		long rowId;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		String table;
		Uri contentUri;

		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		switch (sUriMatcher.match(uri)) {
		case URLS:
			table = URL_TABLE_NAME;
			contentUri = Urls.CONTENT_URI;
			break;

		default:
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		rowId = db.insert(table, null, values);
		Log.e("Test", "WeiboProvider insert rowid=" + rowId);
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
			return newUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.e("Test", "WeiboProvider query");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String sql = "";

		int matchedCode = sUriMatcher.match(uri);
		switch (matchedCode) {
		case URLS:
			qb.setTables(URL_TABLE_NAME);
			qb.setProjectionMap(sUsersProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI \"" + uri + "\"; matchedCode=" + matchedCode);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			switch (matchedCode) {
			case URLS:
				orderBy = Urls.DEFAULTSORT_ORDER;
				break;

			default:
				throw new IllegalArgumentException("Unknown URI \"" + uri + "\"; matchedCode=" + matchedCode);
			}
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (c != null) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return c;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case URLS:
			count = db.delete(URL_TABLE_NAME, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		Log.e("Test", "WeiboProvider delete count=" + count);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case URLS:
			count = db.update(URL_TABLE_NAME, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI \"" + uri + "\"");
		}
		return count;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + URL_TABLE_NAME + " (" + Urls._ID + " INTEGER PRIMARY KEY," + Urls.URL
					+ " TEXT NOT NULL," + Urls.CONTENT + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.beginTransaction();
			try {
				db.execSQL("DROP TABLE " + URL_TABLE_NAME + ";");
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}
}
