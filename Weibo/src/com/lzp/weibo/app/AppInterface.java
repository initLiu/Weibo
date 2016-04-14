package com.lzp.weibo.app;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.lzp.weibo.data.WeiboDatabaseManager;
import com.lzp.weibo.msg.MessageFacade;
import com.lzp.weibo.msg.ToAppMsg;
import com.lzp.weibo.msg.ToServiceMsg;
import com.lzp.weibo.msg.handler.BusinessHandler;
import com.lzp.weibo.msg.handler.Cmd2HandlerMap;
import com.lzp.weibo.msg.handler.MessageHandler;
import com.lzp.weibo.msg.handler.OwnerUserShowHandler;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

public class AppInterface extends AppRuntime {

	public static final int HANDLER_SIZE = 500;
	public static final int MESSAGE_HANDLER = 0;
	public static final int USERSHOW_HANDLER = MESSAGE_HANDLER + 1;

	private BusinessHandler[] mHandlers = new BusinessHandler[HANDLER_SIZE];
	private MessageFacade mMessageFacade;
	private MessageHandler mMessageHandler;
	private WeiboDatabaseManager mdbManager;

	private ConcurrentLinkedQueue<ToServiceMsg> msgQueue = new ConcurrentLinkedQueue<ToServiceMsg>();

	public AppInterface(Context context) {
		super(context);
	}

	public WeiboDatabaseManager getWeiboDatabaseManager() {
		if (mdbManager == null) {
			mdbManager = new WeiboDatabaseManager(getContext());
		}
		return mdbManager;
	}

	public MessageHandler getMessageHandler() {
		if (mMessageHandler == null) {
			mMessageHandler = new MessageHandler(this);
		}
		return mMessageHandler;
	}

	public BusinessHandler getBusinessHandler(int name) {
		if (name < 0 || name >= HANDLER_SIZE) {
			throw new IllegalArgumentException("out of the range of handler");
		}
		BusinessHandler handler = mHandlers[name];
		if (handler != null) {
			return handler;
		} else {
			synchronized (mHandlers) {
				handler = mHandlers[name];
				if (handler == null) {
					handler = createHandler(name);
				}
				if (handler != null) {
					mHandlers[name] = handler;
				}
			}
		}
		return handler;
	}

	private BusinessHandler createHandler(int name) {
		BusinessHandler handler = null;
		switch (name) {
		case MESSAGE_HANDLER:
			mMessageHandler = new MessageHandler(this);
			handler = mMessageHandler;
			break;
		case USERSHOW_HANDLER:
			handler = new OwnerUserShowHandler(this);
			break;
		default:
			break;
		}
		return handler;
	}

	public MessageFacade getMessageFacade() {
		if (mMessageFacade == null) {
			mMessageFacade = new MessageFacade(this);
		}
		return mMessageFacade;
	}

	public void sendRequest(ToServiceMsg msg) throws RemoteException {
		Log.e("Test", "AppInterface sendRequest mNetService=" + mNetService);
		if (isServiceInit()) {
			mNetService.sendRequest(msg);
		} else {
			msgQueue.add(msg);
		}
	}

	@Override
	protected void sendAll() {
		if (!msgQueue.isEmpty()) {
			if (isServiceInit()) {
				ToServiceMsg msg = null;
				while ((msg = msgQueue.poll()) != null) {
					try {
						sendRequest(msg);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onReceive(ToAppMsg msg) {
		Log.e("Test", "AppInterface onReceive msg=" + msg);
		int[] handlerIds = Cmd2HandlerMap.getCmdHandlersMap().get(msg.getCmd().ordinal());
		for (int id : handlerIds) {
			BusinessHandler handler = getBusinessHandler(id);
			handler.onReceive(msg);
		}
	}
}
