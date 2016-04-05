package com.lzp.weibo.activity;

import com.lzp.weibo.R;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends BaseActivity implements OnClickListener {

	private DrawerLayout mDrawer;
	private View mTitlelayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitlelayout = findViewById(R.id.title_layout);
		mTitlelayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_layout:
			if (mDrawer.isDrawerOpen(Gravity.START)) {
				mDrawer.closeDrawer(Gravity.START);
			} else {
				mDrawer.openDrawer(Gravity.START);
			}
			break;

		default:
			break;
		}
	}
}
