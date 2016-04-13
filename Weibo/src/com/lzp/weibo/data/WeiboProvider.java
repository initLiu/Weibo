package com.lzp.weibo.data;

import java.util.HashMap;

import com.lzp.weibo.data.WeiboDatabase.Users;

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

public class WeiboProvider extends ContentProvider {

	private static final String TAG = WeiboProvider.class.getSimpleName();

	private static final String DATABASE_NAME = "weibo.sqlite";

	private static final int DATABASE_VERSION = 1;

	public static final String AUTHORITY = WeiboProvider.class.getName();

	private static final UriMatcher sUriMatcher;

	private DatabaseHelper mOpenHelper;

	private static HashMap<String, String> sUsersProjectionMap;

	private static final String USER_TABLE_NAME = "users";

	private static final int USERS = 1;
	private static final int USER_ID = 2;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, USER_TABLE_NAME, USERS);
		sUriMatcher.addURI(AUTHORITY, USER_TABLE_NAME + "/#", USER_ID);

		sUsersProjectionMap.put(Users._ID, Users._ID);
		sUsersProjectionMap.put(Users.AUTHOR_ID, Users.AUTHOR_ID);
		sUsersProjectionMap.put(Users.NAME, Users.NAME);
		sUsersProjectionMap.put(Users.PROFILE_IMAGE_URL, Users.PROFILE_IMAGE_URL);
		sUsersProjectionMap.put(Users.DESCRIPTION, Users.DESCRIPTION);
		sUsersProjectionMap.put(Users.FOLLOWERS_COUNT, Users.FOLLOWERS_COUNT);
		sUsersProjectionMap.put(Users.FRIENDS_COUNT, Users.FRIENDS_COUNT);
		sUsersProjectionMap.put(Users.STATUSES_COUNT, Users.STATUSES_COUNT);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return (mOpenHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case USERS:
			return Users.CONTENT_TYPE;
		case USER_ID:
			return Users.CONTENT_ITEM_TYPE;
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
		String nullColumnHack;
		Uri contentUri;

		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		switch (sUriMatcher.match(uri)) {
		case USERS:
			table = USER_TABLE_NAME;
			nullColumnHack = Users.AUTHOR_ID;
			contentUri = Users.CONTENT_URI;
			break;

		default:
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		rowId = db.insert(table, nullColumnHack, values);
		if (rowId > 0) {
			Uri newUri = ContentUris.withAppendedId(contentUri, rowId);
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String sql = "";

		int matchedCode = sUriMatcher.match(uri);
		switch (matchedCode) {
		case USERS:
			qb.setTables(USER_TABLE_NAME);
			qb.setProjectionMap(sUsersProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI \"" + uri + "\"; matchedCode=" + matchedCode);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			switch (matchedCode) {
			case USERS:
				orderBy = Users.DEFAULTSORT_ORDER;
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
		case USERS:
			count = db.delete(USER_TABLE_NAME, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case USERS:
			count = db.update(USER_TABLE_NAME, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI \"" + uri + "\"");
		}
		return count;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private SQLiteDatabase mDatabase;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (" + Users._ID + " INTEGER PRIMARY KEY," + Users.AUTHOR_ID
					+ " TEXT," + Users.NAME + " TEXT," + Users.PROFILE_IMAGE_URL + " TEXT," + Users.DESCRIPTION
					+ " TEXT," + Users.FOLLOWERS_COUNT + " INTEGER," + Users.FRIENDS_COUNT + " INTEGER,"
					+ Users.STATUSES_COUNT + " INTEGER);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.beginTransaction();
			try {
				db.execSQL("DROP TABLE " + USER_TABLE_NAME + ";");
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}
}
