package com.lzp.weibo.app;

import android.content.Context;
import android.os.RemoteException;

import com.lzp.weibo.msg.MessageFacade;

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

	public void sendRequest(String url) throws RemoteException {
		mNetService.sendRequest(url);
	}
}
