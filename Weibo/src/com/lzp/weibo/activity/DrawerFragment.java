package com.lzp.weibo.activity;

import java.util.Observable;
import java.util.Observer;

import com.bumptech.glide.Glide;
import com.lzp.weibo.R;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
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
		mUiHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_drawer, container, false);
		mObserver = new FaceUpdateObserver();
		mApp.getMessageFacade().addObserver(mObserver);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		initCurPage();
	}

	private void initView(View view) {
		mBtnFrist = (Button) view.findViewById(R.id.drawer_firstPage);
		mBtnFrist.setOnClickListener(this);

		view.findViewById(R.id.drawer_header_layout).setOnClickListener(this);

		mImageFace = (ImageView) view.findViewById(R.id.drawer_header);
	}

	private void initCurPage() {
		switch (mCurPage.ordinal()) {
		case 0:// Ê×Ò³
			mBtnFrist.setSelected(true);
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
			} else {
				((MainActivity) getActivity()).closeDrawer();
			}
			break;
		case R.id.drawer_header_layout:
			Log.e("Test", "drawer click");
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mApp.getMessageFacade().deleteObserver(mObserver);
	}

	class FaceUpdateObserver implements Observer {

		@Override
		public void update(Observable observable, Object data) {
			Log.e("Test", "DrawerFragment update");
			if (data != null) {
				Log.e("Test", "DrawerFragment update1");
				Message msg = mUiHandler.obtainMessage(UPDATE_FACE);
				msg.obj = data;
				mUiHandler.sendMessage(msg);
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

	private void updateFace(String url) {
		Log.e("Test", "DrawerFragment updateFace");
		Glide.with(this).load(url).placeholder(R.drawable.ic_weibo).into(mImageFace);
	}
}
