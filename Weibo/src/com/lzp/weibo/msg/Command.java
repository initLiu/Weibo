package com.lzp.weibo.msg;

public enum Command {
	/**自己的信息 */
	owner_users_show,
	/** 获取当前登录用户及其所关注用户的最新微博 */
	friends_timeline,
	/**获取当前登录用户及其所关注用户的某个时间点之前的微博 */
	friends_timeline_old,
	/** error */
	error,
	/** 根据微博ID返回某条微博的评论列表 */
	comments
}
