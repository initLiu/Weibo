package com.lzp.weibo.activity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.bumptech.glide.Glide;
import com.lzp.weibo.R;
import com.lzp.weibo.adapter.FriendsTimelineAdapter;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.CommentsManager;
import com.lzp.weibo.msg.MessageFacade;
import com.lzp.weibo.msg.MessageFacade.ObserverData;
import com.lzp.weibo.widget.CommentLayout;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 首页
 * 
 * @author SKJP
 *
 */
public class FirstPageFragment extends Fragment implements OnRefreshListener, OnScrollListener, Callback{

	public static final String TAG = FirstPageFragment.class.getSimpleName();

	private AppInterface mApp;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private ListView mList;
	private FriendsTimelineAdapter mAdapter;
	private boolean mIsloading = false;
	private boolean mShowLoading = false;
	private View mLoadView;
	private CommentsManager commentsManager;
	
	private static final int UPDATE_STATUSLIST = 1;
	private static final int REFRESH_DONE = 2;
	private static final int ADD_HISTORY = 3;
	private static final int ERROR = 4;
	private static final int COMMENTS = 5;
	
	private static final int DELAY_TIME = 2000;
	private Handler mHandler = new Handler(this);

	private Observer mStatusListObserver = new Observer() {

		@Override
		public void update(Observable observable, Object data) {
			if (data != null && data instanceof ObserverData) {
				ObserverData obData = (ObserverData) data;
				if (obData.cmd == Command.friends_timeline) {
					Message msg = mHandler.obtainMessage(UPDATE_STATUSLIST);
					msg.obj = obData.data;
					mHandler.sendMessage(msg);
				} else if (obData.cmd == Command.friends_timeline_old) {
					Message msg = mHandler.obtainMessage(ADD_HISTORY);
					msg.obj = obData.data;
					mHandler.sendMessageDelayed(msg, DELAY_TIME);
				} else if (obData.cmd == Command.error) {
					Message msg = mHandler.obtainMessage(ERROR);
					msg.obj = obData.data;
					mHandler.sendMessageDelayed(msg, DELAY_TIME);
				} else if (obData.cmd == Command.comments) {
					Message msg = mHandler.obtainMessage(COMMENTS);
					msg.obj = obData.data;
					mHandler.sendMessage(msg);
				}
			}
		}
	};

	public static FirstPageFragment getInstance() {
		FirstPageFragment fragment = new FirstPageFragment();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (AppInterface) BaseApplication.mApplication.getAppRuntime();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_firstpage, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.firstpage_swipe_refresh);
		mList = (ListView) view.findViewById(R.id.firstpage_list);
		mSwipeRefreshWidget.setColorScheme(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		
		// 以下下6行是为了解bug：E/AndroidRuntime(9319):
		// java.lang.ClassCastException:
		// com.lzp.weibo.adapter.FriendsTimelineAdapter cannot be cast to
		// android.widget.HeaderViewListAdapter
		// 06-02 14:10:08.186: E/AndroidRuntime(9319): at
		// android.widget.ListView.removeFooterView(ListView.java:387)
		// 06-02 14:10:08.186: E/AndroidRuntime(9319): at
		// com.lzp.weibo.activity.FirstPageFragment.handleMessage(FirstPageFragment.java:216)
		//原因：Android4.4之前addHeaderView和addFooterView的调用用在setAdapter之前
		
		mLoadView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_loading, null);
		mList.removeFooterView(mLoadView);
		mList.addFooterView(mLoadView);
		mAdapter = new FriendsTimelineAdapter(getActivity());
		mList.setAdapter(mAdapter);
		mList.removeFooterView(mLoadView);
		mList.setOnScrollListener(this);

		mApp.getMessageFacade().addObserver(mStatusListObserver);

		refreshListView();
	}

	private void refreshListView() {
		Log.e(TAG, "FirstPageFragment refreshListView");
		StatusList statusList = mApp.getMessageFacade().getStatusListFromCache();
		mAdapter.setData(statusList);
	}

	@Override
	public void onRefresh() {
		refresh();
	}

	private void refresh() {
		MessageFacade messageFacade = mApp.getMessageFacade();
		String since_id = messageFacade.getSinceId();
		if (TextUtils.isEmpty(since_id)) {
			since_id = "0";
		}
		// 请求好友微博
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getActivity());
		messageFacade.sendRequest(Command.friends_timeline,
				"https://api.weibo.com/2/statuses/friends_timeline.json?access_token=" + token.getToken() + "&since_id="
						+ since_id);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == 0 && mShowLoading) {
			getHistory();
		}
		
		if (scrollState == SCROLL_STATE_IDLE) {
			Glide.with(this).resumeRequests();
		} else {
			Glide.with(this).pauseRequests();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && !mIsloading) {
			mShowLoading = true;
		} else {
			mShowLoading = false;
		}
	}

	private void getHistory() {
		Log.e(TAG, "FirstPageFragment getHistory");
		mList.removeFooterView(mLoadView);
		mList.addFooterView(mLoadView);
		mList.smoothScrollToPosition(mAdapter.getCount()+1);
		mIsloading = true;
		addHistory();
	}
	
	private void addHistory(){
		Log.e(TAG, "FirstPageFragment addHistory");
		MessageFacade messageFacade = mApp.getMessageFacade();
		String max_id = messageFacade.getMaxId();
		if (TextUtils.isEmpty(max_id)) {
			max_id = "0";
		}
		// 请求好友微博
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getActivity());
		messageFacade.sendRequest(Command.friends_timeline_old,
				"https://api.weibo.com/2/statuses/friends_timeline.json?access_token=" + token.getToken() + "&max_id="
						+ max_id);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mApp.getMessageFacade().deleteObserver(mStatusListObserver);
	}

	@Override
	public boolean handleMessage(Message msg) {
		Object data = msg.obj;
		boolean update;
		
		switch (msg.what) {
		case UPDATE_STATUSLIST:
			Log.e(TAG, "FirstPageFragment UPDATE_STATUSLIST");
			mHandler.removeMessages(REFRESH_DONE);
			mHandler.sendEmptyMessage(REFRESH_DONE);
			update = (boolean) data;
			if (update) {
				refreshListView();
			} else {
				Toast.makeText(getActivity(), "本次没有更新", Toast.LENGTH_SHORT).show();
			}
			break;
		case ADD_HISTORY:
			mList.removeFooterView(mLoadView);
			mIsloading = false;
			update = (boolean) data;
			if (update) {
				refreshListView();
			} else {
				Toast.makeText(getActivity(), "本次没有更新", Toast.LENGTH_SHORT).show();
			}
			break;
		case REFRESH_DONE:
			Log.e(TAG, "FirstPageFragment REFRESH_DONE");
			mSwipeRefreshWidget.setRefreshing(false);
			break;
		case ERROR:
			mHandler.removeMessages(REFRESH_DONE);
			mHandler.sendEmptyMessage(REFRESH_DONE);
			mList.removeFooterView(mLoadView);
			mIsloading = false;
			Toast.makeText(getActivity(), "请求失败:"+data.toString(), Toast.LENGTH_SHORT).show();
			break;
		case COMMENTS:
			CommentList comments = (CommentList) data;
			if (commentsManager != null) {
				ArrayList<Comment> commentList = comments.commentList;
				if (commentList != null && !commentList.isEmpty()) {
					String wid = commentList.get(0).status.id;// 获取微博id
					CommentLayout layout = (CommentLayout) commentsManager.getComment(wid);
					Log.e(TAG, "comments="+commentList.size());
					layout.addComments(comments);
				}
			}
			break;
		default:
			break;
		}
		return true;
	}
}
