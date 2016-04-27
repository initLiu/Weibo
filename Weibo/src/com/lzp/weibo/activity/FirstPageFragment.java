package com.lzp.weibo.activity;

import java.util.Observable;
import java.util.Observer;

import com.lzp.weibo.R;
import com.lzp.weibo.adapter.FriendsTimelineAdapter;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.MessageFacade.ObserverData;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
public class FirstPageFragment extends Fragment implements OnRefreshListener, OnScrollListener, Callback {

	public static final String TAG = FirstPageFragment.class.getSimpleName();

	private AppInterface mApp;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private ListView mList;
	private FriendsTimelineAdapter mAdapter;
	private boolean mIsloading = false;
	private View mLoadView;
	private StatusList mStatusList;

	private static final int UPDATE_STATUSLIST = 1;
	private static final int REFRESH_DONE = 2;
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
		mAdapter = new FriendsTimelineAdapter(getActivity());
		mList.setAdapter(mAdapter);
		mList.setOnScrollListener(this);

		mApp.getMessageFacade().addObserver(mStatusListObserver);

		refreshListView();
	}

	private void refreshListView() {
		Log.e("Test", "FirstPageFragment refreshListView");
		mStatusList = mApp.getMessageFacade().getStatusListFromCache();
		mAdapter.setData(mStatusList);
	}

	@Override
	public void onRefresh() {
		refresh();
	}

	private void refresh() {
		long since_id = 0;
		if (mStatusList == null) {
			mStatusList = mApp.getMessageFacade().getStatusListFromCache();
		}
		if (mStatusList != null && mStatusList.statusList != null && !mStatusList.statusList.isEmpty()) {
			since_id = Long.parseLong(mStatusList.statusList.get(0).id);
		}
		// 请求好友微博
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getActivity());
		mApp.getMessageFacade().sendRequest(Command.friends_timeline,
				"https://api.weibo.com/2/statuses/friends_timeline.json?access_token=" + token.getToken() + "&since_id="
						+ since_id);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// if (firstVisibleItem + visibleItemCount == totalItemCount &&
		// !mIsloading) {
		// mLoadView =
		// LayoutInflater.from(getActivity()).inflate(R.layout.list_item_loading,
		// null);
		// mList.removeFooterView(mLoadView);
		// mList.addFooterView(mLoadView);
		// mIsloading = true;
		// }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mApp.getMessageFacade().deleteObserver(mStatusListObserver);
	}

	@Override
	public boolean handleMessage(Message msg) {
		Object data = msg.obj;

		switch (msg.what) {
		case UPDATE_STATUSLIST:
			Log.e(TAG, "FirstPageFragment UPDATE_STATUSLIST");
			mHandler.removeMessages(REFRESH_DONE);
			mHandler.sendEmptyMessage(REFRESH_DONE);
			boolean update = (boolean) data;
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
		default:
			break;
		}
		return true;
	}
}
