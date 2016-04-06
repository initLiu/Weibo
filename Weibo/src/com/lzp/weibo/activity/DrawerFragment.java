package com.lzp.weibo.activity;

import com.lzp.weibo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DrawerFragment extends Fragment implements OnClickListener {

	public enum DrawerPage {
		fistePage
	};

	private DrawerPage mCurPage = DrawerPage.fistePage;
	private Button mBtnFrist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_drawer, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBtnFrist = (Button) view.findViewById(R.id.drawer_firstPage);
		mBtnFrist.setOnClickListener(this);
		view.findViewById(R.id.drawer_header_layout).setOnClickListener(this);
		init();
	}

	private void init() {
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
}
