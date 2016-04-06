package com.lzp.weibo.app;

import com.lzp.weibo.msg.MessageFacade;

public class AppInterface{
	
	private MessageFacade mMessageFacade;

	public MessageFacade getMessageFacade() {
		if (mMessageFacade == null) {
			mMessageFacade = new MessageFacade();
		}
		return mMessageFacade;
	}
}
