package com.lzp.weibo.net.listener;

import java.util.HashMap;
import java.util.Map;

import com.lzp.weibo.msg.Command;
import com.lzp.weibo.net.NetCore;

public class ResponListenerCreator {
	private Map<Integer, BaseResponseListener> listeners = new HashMap<Integer, BaseResponseListener>();
	private NetCore netCore;

	public ResponListenerCreator(NetCore netCore) {
		this.netCore = netCore;
	}

	public BaseResponseListener createResponListener(Command cmd) {
		if (listeners.containsKey(cmd.ordinal())) {
			return listeners.get(cmd.ordinal());
		}

		if (cmd == Command.owner_users_show) {
			listeners.put(cmd.ordinal(), new OwnerUserShowResponseListener(netCore));
		} else if (cmd == Command.friends_timeline) {
			listeners.put(cmd.ordinal(), new FriendsTimelineListener(netCore));
		} else if (cmd == Command.friends_timeline_old) {
			listeners.put(cmd.ordinal(), new FriendsTimelineListenerOld(netCore));
		} else if (cmd == Command.comments) {
			listeners.put(cmd.ordinal(), new CommentsListener(netCore));
		}
		return listeners.get(cmd.ordinal());
	}
}
