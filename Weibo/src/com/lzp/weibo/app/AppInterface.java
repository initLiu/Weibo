package com.lzp.weibo.app;

import com.lzp.weibo.msg.MessageFacade;
import com.lzp.weibo.msg.ToServiceMsg;

import android.content.Context;
import android.os.RemoteException;

public class AppInterface extends AppRuntime {

	public AppInterface(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private MessageFacade mMessageFacade;

	public MessageFacade getMessageFacade() {
		if (mMessageFacade == null) {
			mMessageFacade = new MessageFacade();
		}
		return mMessageFacade;
	}

	public void sendRequest(ToServiceMsg msg) throws RemoteException {
		mNetService.sendRequest(msg);
	}
}
