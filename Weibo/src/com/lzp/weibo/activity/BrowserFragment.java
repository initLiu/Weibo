package com.lzp.weibo.activity;

import com.lzp.weibo.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BrowserFragment extends BaseFragment {

	public static final String TAG = BrowserFragment.class.getSimpleName();

	private WebView mWebView;
	private ProgressBar mLoading;
	private WeiboWebViewClient mWebViewClient;
	private WeiboWebViewChromeClient mWebViewChromeClient;

	public static Fragment getInstance(String url, String fromTag) {
		Fragment fragment = new BrowserFragment();
		Bundle args = new Bundle();
		args.putString("url", url);
		args.putString("from", fromTag);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_browser, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initUI(view);
		initData();
	}

	private void initUI(View view) {
		mWebView = (WebView) view.findViewById(R.id.fragment_browser_webview);
		mLoading = (ProgressBar) view.findViewById(R.id.fragment_browser_loading);
		mLoading.setVisibility(View.GONE);
	}

	private void initData() {
		mWebViewClient = new WeiboWebViewClient();
		mWebViewChromeClient = new WeiboWebViewChromeClient();
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setWebChromeClient(mWebViewChromeClient);
		mWebView.getSettings().setJavaScriptEnabled(true);

		String url = getArguments().getString("url");
		if (url != null) {
			mWebView.loadUrl(url);
			String title = mWebView.getTitle().isEmpty() ? "อ๘าณ" : mWebView.getTitle();
			getActivity().setTitle(title);
		}
	}

	@Override
	public void onBackPress() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}

		String from = (String) getArguments().get("from");
		if (from != null) {
			FragmentManager manager = getActivity().getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			Fragment fragment = manager.findFragmentByTag(from);
			transaction.remove(this);
			if (fragment != null) {
				transaction.show(fragment);
			}
			transaction.commitAllowingStateLoss();
		}
	}

	class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mLoading.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mLoading.setVisibility(View.GONE);
		}
	}

	class WeiboWebViewChromeClient extends WebChromeClient {

	}
}
