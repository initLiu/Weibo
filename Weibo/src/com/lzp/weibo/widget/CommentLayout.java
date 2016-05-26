package com.lzp.weibo.widget;

import java.util.ArrayList;

import com.lzp.weibo.R;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CommentLayout extends ScrollView {

	private LinearLayout mLinearLayout;

	public CommentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public CommentLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CommentLayout(Context context) {
		this(context, null);
	}

	private void init() {
		mLinearLayout = new LinearLayout(getContext());
		mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		addView(mLinearLayout);

		setVerticalScrollBarEnabled(false);// 不显示滚动条
	}

	public void addComment(Comment comment) {
		addCommentView(comment);
	}

	public void addComments(CommentList comments) {
		ArrayList<Comment> list = comments.commentList;
		if (list != null && !list.isEmpty()) {
			for (Comment comment : list) {
				addComment(comment);
			}
		}
	}

	private void addCommentView(Comment comment) {
		TextView author = new TextView(getContext());
		author.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		author.setTextColor(getContext().getColor(R.color.white));
		author.setText(comment.user.screen_name+" :");

		TextView content = new TextView(getContext());
		content.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		content.setTextColor(getContext().getColor(R.color.white));
		content.setText(comment.text);

		LinearLayout layout = new LinearLayout(getContext());
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(author);
		layout.addView(content);

		mLinearLayout.addView(layout);
	}
}
