package com.lzp.weibo.msg.handler;

import java.util.HashMap;
import java.util.Map;

import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.msg.Command;

public class Cmd2HandlerMap {
	private static Map<Integer, int[]> cmdHandlerMap;
	private static Object cmdmapLock = new Object();

	public static Map<Integer, int[]> getCmdHandlersMap() {
		if (cmdHandlerMap == null) {
			synchronized (cmdmapLock) {
				if (cmdHandlerMap == null) {
					initCmdHandlerMap();
				}
			}
		}
		return cmdHandlerMap;
	}

	private static void initCmdHandlerMap() {
		cmdHandlerMap = new HashMap<Integer, int[]>();

		cmdHandlerMap.put(Command.owner_users_show.ordinal(), new int[] { AppInterface.USERSHOW_HANDLER });
		cmdHandlerMap.put(Command.friends_timeline.ordinal(), new int[] { AppInterface.FRIENDSTIMELINE_HANDLER });
	}
}
