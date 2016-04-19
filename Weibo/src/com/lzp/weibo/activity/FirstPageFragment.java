package com.lzp.weibo.activity;

import com.lzp.weibo.R;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.msg.Command;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Ê×Ò³
 * 
 * @author SKJP
 *
 */
public class FirstPageFragment extends Fragment implements OnRefreshListener {

	public static final String TAG = FirstPageFragment.class.getSimpleName();

	private AppInterface mApp;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	private ListView mList;
	private Handler mHandler = new Handler();
	private final Runnable mRefreshDone = new Runnable() {

		@Override
		public void run() {
			mSwipeRefreshWidget.setRefreshing(false);
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

		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getActivity());
		mApp.getMessageFacade().sendRequest(Command.friends_timeline,
				"https://api.weibo.com/2/statuses/friends_timeline.json?access_token=" + token.getToken());
	}

	@Override
	public void onRefresh() {
		refresh();
	}

	private void refresh() {
		mHandler.removeCallbacks(mRefreshDone);
		mHandler.postDelayed(mRefreshDone, 1000);
	}

}
