package com.lzp.weibo.activity;

import java.util.List;

import com.lzp.weibo.R;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {

	private DrawerLayout mDrawerLayout;
	private View mTitlelayout;
	private TextView mTxtTitle;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawable(null);
		init();
	}

	private void init() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTxtTitle = (TextView) findViewById(R.id.title);
		mTitlelayout = findViewById(R.id.title_layout);
		mTitlelayout.setOnClickListener(this);
		// mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
		// R.drawable.ic_drawer, R.string.openDrawer, R.string.closeDrawer) {
		//
		// @Override
		// public void onDrawerClosed(View drawerView) {
		// // TODO Auto-generated method stub
		// super.onDrawerClosed(drawerView);
		// }
		//
		// @Override
		// public void onDrawerOpened(View drawerView) {
		// // TODO Auto-generated method stub
		// super.onDrawerOpened(drawerView);
		// }
		//
		// };
		// mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_layout:
			Log.e("Test", "title click");
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawer(Gravity.START);
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
			}
			break;

		default:
			break;
		}
	}

	public void closeDrawer() {
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		}
	}

	public void openDrawer() {
		if (!mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.openDrawer(Gravity.START);
		}
	}

	public void changeDrawerPagePosition(Fragment fragment, String tag) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content, fragment, tag);
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
		BaseFragment fragment = (BaseFragment) manager.findFragmentByTag(BrowserFragment.TAG);
		if (fragment != null && fragment.isVisible()) {
			fragment.onBackPress();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTxtTitle.setText(title);
	}
}
