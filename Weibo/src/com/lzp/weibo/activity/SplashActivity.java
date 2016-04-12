package com.lzp.weibo.activity;

import com.lzp.weibo.R;
import com.lzp.weibo.app.AccessTokenKeeper;
import com.lzp.weibo.app.AppConstants;
import com.lzp.weibo.app.AppInterface;
import com.lzp.weibo.app.BaseApplication;
import com.lzp.weibo.msg.Command;
import com.lzp.weibo.msg.handler.UserShowHandler;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class SplashActivity extends BaseActivity {

	private AuthInfo mAuthInfo;
	private SsoHandler mSsoHandler;
	private Button mBtnAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		doSplash();
		if (!AccessTokenKeeper.readAccessToken(this).isSessionValid()) {
			initView();
		} else {
			gotoMainView();
		}
	}

	private void initView() {
		mBtnAuth = (Button) findViewById(R.id.auth);
		mBtnAuth.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_btn_in);
		mBtnAuth.startAnimation(animation);
		mBtnAuth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doWeiboAuth();
			}
		});
	}

	private void gotoMainView() {
		getUserShow();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void doSplash() {
		View root = findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(2000);
		root.startAnimation(animation);
	}

	// 微博授权
	private void doWeiboAuth() {
		mAuthInfo = new AuthInfo(this, AppConstants.APP_KEY, AppConstants.REDIRECT_URL, AppConstants.SCOPE);
		mSsoHandler = new SsoHandler(this, mAuthInfo);
		mSsoHandler.authorizeWeb(mAuthListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	private WeiboAuthListener mAuthListener = new WeiboAuthListener() {

		@Override
		public void onCancel() {
			Toast.makeText(SplashActivity.this, "Auth cancle", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onComplete(Bundle values) {
			Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);// 从Bundle中解析Token
			if (accessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(SplashActivity.this, accessToken);
				gotoMainView();
			} else {
				String code = values.getString("code", "");
				Toast.makeText(SplashActivity.this, code, Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(SplashActivity.this, "Auth exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

		}
	};

	/**
	 * 获取账号信息，包括头像、昵称等 https://api.weibo.com/2/users/show.json
	 */
	private void getUserShow() {
		Log.e("Test", "SplashActivity getUserShow");
		AppInterface app = (AppInterface) BaseApplication.mApplication.getAppRuntime();
		UserShowHandler usHandler = (UserShowHandler) app.getBusinessHandler(AppInterface.USERSHOW_HANDLER);
		Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
		usHandler.sendRequest(Command.owner_users_show,
				"https://api.weibo.com/2/users/show.json?access_token=" + token.getToken() + "&uid=" + token.getUid());
	}
}
