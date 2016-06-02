package com.lzp.weibo.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lzp.weibo.R;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.text.WeiboText;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CommentLayout extends ScrollView {

	private static final String TAG = CommentLayout.class.getSimpleName();
	private LinearLayout mLinearLayout;
	private static Map<String, Integer> status = new HashMap<String, Integer>();
	private static Map<String, CommentList> comments = new HashMap<String, CommentList>();
	private RequestQueue mRequestQueue;

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
		// Comment comment = new Comment();
		// comment.text=",mnzx,cvnz,xcvn,zcvn,zcvn,alsdjflasjkdflajkdflajdf";
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		// addComment(comment);
		mRequestQueue = Volley.newRequestQueue(getContext());
	}

	public void addComment(Comment comment) {
		addCommentView(comment);
	}

	public void addComments(CommentList comments) {
		if (comments == null) {
			return;
		}
		ArrayList<Comment> list = comments.commentList;
		if (list != null && !list.isEmpty()) {
			for (Comment comment : list) {
				addComment(comment);
			}
		}
	}

	private void addCommentView(Comment comment) {
		TextView content = new TextView(getContext());
		content.setLayoutParams(
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		content.setTextColor(getContext().getResources().getColor(R.color.white));
		SpannableString author = new SpannableString(comment.user.screen_name);
		author.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				// TODO Auto-generated method stub

			}
		}, 0, author.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		content.setText(author);
		content.append(" : ");
		content.append(new WeiboText(comment.text, WeiboText.GRAB_LINKS));

		LinearLayout layout = new LinearLayout(getContext());
		layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(content);

		mLinearLayout.addView(layout);
	}

	private void clearCommentView() {
		mLinearLayout.removeAllViews();
	}

	public void setVisibility(String weiId, int visibility) {
		// Log.e(TAG, "setVisibility position=" + position + ",visibility=" +
		// visibility);
		if (!TextUtils.isEmpty(weiId)) {
			if (visibility == View.VISIBLE) {
				clearCommentView();
				if (comments.containsKey(weiId)) {
					// Log.e(TAG, "use old");
					addComments(comments.get(weiId));
					innerSetVisibility(weiId, visibility);
				} else {
					sendRequest(weiId);
				}
			} else {
				innerSetVisibility(weiId, visibility);
			}
		}
	}

	private void innerSetVisibility(final String id, final int visibility) {
		post(new Runnable() {
			public void run() {
				status.put(id, visibility);
				setVisibility(visibility);
			}
		});
	}

	private void sendRequest(String weiId) {
		// Log.e(TAG, "sendRequest position="+position);
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getContext());
		ResponseListener listener = new ResponseListener(weiId);
		StringRequest request = new StringRequest(
				"https://api.weibo.com/2/comments/show.json?access_token=" + token.getToken() + "&id=" + weiId,
				listener, listener);
		mRequestQueue.add(request);
	}

	public Integer getVisibility(String id) {
		return status.get(id) == null ? View.GONE : status.get(id);
	}

	class ResponseListener implements Listener<String>, ErrorListener {
		private String weiId;

		public ResponseListener(String weiId) {
			this.weiId = weiId;
		}

		@Override
		public void onResponse(String response) {
			final CommentList commentList = CommentList.parse(response);
			if (commentList != null && commentList.commentList != null && !commentList.commentList.isEmpty()) {
				CommentLayout.this.post(new Runnable() {

					@Override
					public void run() {
						// Log.e(TAG, "onResponse position="+position);
						addComments(commentList);
						comments.put(weiId, commentList);
						innerSetVisibility(weiId, View.VISIBLE);
					}
				});
			}
		}

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub

		}
	}
}
