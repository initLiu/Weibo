package com.lzp.weibo.msg;

import java.util.HashMap;

import android.text.TextUtils;
import android.view.View;

public class CommentsManager {
	private HashMap<String, View> comments = new HashMap<String, View>();

	public void addComment(String id, View v) {
		if (TextUtils.isEmpty(id) || v == null) {
			return;
		}
		comments.put(id, v);
	}

	public View getComment(String id) {
		return comments.get(id);
	}
	
	public void removeComment(String id){
		comments.remove(id);
	}
}
