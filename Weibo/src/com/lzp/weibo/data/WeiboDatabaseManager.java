package com.lzp.weibo.data;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;

public class WeiboDatabaseManager {
	private LinkedBlockingQueue<DataItem> msgQueue = new LinkedBlockingQueue<DataItem>();
	private WorkThread mWorkThread;
	private Context mContext;

	public WeiboDatabaseManager(Context context) {
		mContext = context;
		mWorkThread = new WorkThread();
		mWorkThread.start();
	}

	public void addMsgQueue(DataItem item) {
		if (item != null) {
			msgQueue.add(item);
		}
	}

	class WorkThread extends Thread {

		@Override
		public void run() {
			while (true) {
				DataItem item = msgQueue.poll();
				if (item != null) {
					switch (item.action) {
					case DataItem.ACTION_INSERT:
						mContext.getContentResolver().insert(item.uri, item.contentValues);
						break;
					case DataItem.ACTION_DELETE:
						mContext.getContentResolver().delete(item.uri, item.where, item.selectionArgs);
						break;
					case DataItem.ACTION_UPDATE:
						mContext.getContentResolver().update(item.uri, item.contentValues, item.where,
								item.selectionArgs);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public void destroy() {
		if (!msgQueue.isEmpty()) {
			msgQueue.clear();
		}
	}
}
