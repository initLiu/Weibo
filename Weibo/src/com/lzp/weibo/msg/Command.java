package com.lzp.weibo.msg;

public enum Command {
	/**�Լ�����Ϣ */
	owner_users_show,
	/** ��ȡ��ǰ��¼�û���������ע�û�������΢�� */
	friends_timeline,
	/**��ȡ��ǰ��¼�û���������ע�û���ĳ��ʱ���֮ǰ��΢�� */
	friends_timeline_old,
	/** error */
	error,
	/** ����΢��ID����ĳ��΢���������б� */
	comments
}
