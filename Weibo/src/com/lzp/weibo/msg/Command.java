package com.lzp.weibo.msg;

public enum Command {
	owner_users_show,//自己的信息
	friends_timeline,//获取当前登录用户及其所关注用户的最新微博
	friends_timeline_old,//获取当前登录用户及其所关注用户的某个时间点之前的微博
	error,//error
}
