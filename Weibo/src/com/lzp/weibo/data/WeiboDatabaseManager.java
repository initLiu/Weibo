package com.lzp.weibo.data;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;

public class WeiboDatabaseManager {
	private LinkedBlockingQueue<DataItem> msgQueue = new LinkedBlockingQueue<DataItem>();
	private WorkThread mWorkThread;
	private Context mContext;
	private volatile boolean running = true;

	public WeiboDatabaseManager(Context context) {
		mContext = context;
		mWorkThread = new WorkThread();
		mWorkThread.start();
	}

	public void addMsgQueue(DataItem item) {
		if (item != null) {
			try {
				msgQueue.put(item);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class WorkThread extends Thread {

		@Override
		public void run() {
			while (running) {
				try {
					DataItem item = msgQueue.take();
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
						case DataItem.ACTION_UPDATE_INSERT:
							if (mContext.getContentResolver().update(item.uri, item.contentValuesTmp, item.where,
									item.selectionArgs) == 0) {
								mContext.getContentResolver().insert(item.uri, item.contentValues);
							}
							break;
						default:
							break;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void destroy() {
		running = false;
		if (!msgQueue.isEmpty()) {
			msgQueue.clear();
		}
	}
}
