package com.lzp.weibo.activity;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.lzp.weibo.R;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.MessageFacade.ObserverData;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class DrawerFragment extends Fragment implements OnClickListener, Callback {

	public enum DrawerPage {
		fistePage
	};

	public static final String TAG = DrawerFragment.class.getSimpleName();
	private static final int UPDATE_FACE = 1;

	private DrawerPage mCurPage = DrawerPage.fistePage;
	private Button mBtnFrist;
	private AppInterface mApp;
	private FaceUpdateObserver mObserver;
	private ImageView mImageFace;
	private Handler mUiHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (AppInterface) BaseApplication.mApplication.getAppRuntime();
		mUiHandler = new Handler(Looper.getMainLooper(), this);
		mObserver = new FaceUpdateObserver();
		mApp.getMessageFacade().addObserver(mObserver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_drawer, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		initData();
		initCurPage();
	}

	private void initView(View view) {
		mBtnFrist = (Button) view.findViewById(R.id.drawer_firstPage);
		mBtnFrist.setOnClickListener(this);

		view.findViewById(R.id.drawer_header_layout).setOnClickListener(this);

		mImageFace = (ImageView) view.findViewById(R.id.drawer_header);
	}

	private void initData() {
		String response = mApp.getMessageFacade().getResponseFromCache(Command.owner_users_show);
		String url = null;
		if (TextUtils.isEmpty(response)) {
			mImageFace.setImageResource(R.drawable.ic_weibo);
			getUserShow();
			return;
		}

		try {
			JSONObject owneruser = new JSONObject(response);
			url = owneruser.getString("avatar_large");
			Log.e("Test", "DrawerFragment url=" + url);
			Glide.with(getActivity()).load(url).asBitmap().placeholder(R.drawable.ic_weibo).into(mImageFace);
		} catch (Exception e) {
			e.printStackTrace();
			mImageFace.setImageResource(R.drawable.ic_weibo);
		}
	}

	private void initCurPage() {
		switch (mCurPage.ordinal()) {
		case 0:// 首页
			mBtnFrist.setSelected(true);
			chagePage(FirstPageFragment.getInstance(), FirstPageFragment.TAG);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.drawer_firstPage:
			if (mCurPage != DrawerPage.fistePage) {
				mCurPage = DrawerPage.fistePage;
				mBtnFrist.setSelected(true);
				chagePage(FirstPageFragment.getInstance(), FirstPageFragment.TAG);
			}
			((MainActivity) getActivity()).closeDrawer();
			break;
		case R.id.drawer_header_layout:
			Log.e("Test", "drawer click");
			break;
		default:
			break;
		}
	}

	private void chagePage(Fragment fragment, String tag) {
		((MainActivity) getActivity()).changeDrawerPagePosition(fragment, tag);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mApp.getMessageFacade().deleteObserver(mObserver);
	}

	class FaceUpdateObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			Log.e(TAG, "FaceUpdateObserver update");
			if (data != null && data instanceof ObserverData) {
				ObserverData obData = (ObserverData) data;
				if (obData.cmd == Command.owner_users_show) {
					Message msg = mUiHandler.obtainMessage(UPDATE_FACE);
					msg.obj = obData.data;
					mUiHandler.sendMessage(msg);
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		Object data = msg.obj;
		switch (msg.what) {
		case UPDATE_FACE:
			updateFace((String) data);
			break;

		default:
			break;
		}
		return false;
	}

	/**
	 * 获取账号信息，包括头像、昵称等 https://api.weibo.com/2/users/show.json
	 */
	private void getUserShow() {
		Log.e("Test", "DrawerFragment getUserShow");
		AppInterface app = (AppInterface) BaseApplication.mApplication.getAppRuntime();
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getActivity());
		app.getMessageFacade().sendRequest(Command.owner_users_show,
				"https://api.weibo.com/2/users/show.json?access_token=" + token.getToken() + "&uid=" + token.getUid());
	}

	private void updateFace(String url) {
		Log.e("Test", "DrawerFragment updateFace url=" + url);
		Glide.with(getActivity()).load(url).asBitmap().placeholder(R.drawable.ic_weibo).into(mImageFace);
	}
}
