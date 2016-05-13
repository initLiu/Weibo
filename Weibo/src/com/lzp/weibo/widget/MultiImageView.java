package com.lzp.weibo.widget;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzp.weibo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MultiImageView extends TableLayout {
	public static final String TAG = MultiImageView.class.getSimpleName();
	
	private ArrayList<String> pic_urls;

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MultiImageView(Context context) {
		this(context, null);
	}

	public void setPicUrls(ArrayList<String> pics) {
		if (pics != null && !pics.isEmpty()) {
			pic_urls = pics;
			layoutPictures();
		}
	}

	public void removeAllPictures() {
		removeAllViews();
	}

	private void layoutPictures() {
		if (pic_urls != null && !pic_urls.isEmpty()) {
			int count = pic_urls.size();
			int rows = count / 3;
			rows = count % 3 != 0 ? ++rows : rows;

			for (int i = 0; i < rows; i++) {
				TableRow tableRow = new TableRow(getContext());
				for (int j = 0; j < 3; j++) {
					int pos = i * 3 + j;
					if (pos >= count) {
						break;
					}
					ImageView imageView = new ImageView(getContext());
					imageView.setScaleType(ScaleType.FIT_XY);
					imageView.setPadding(0, 0, dp2px(5), 0);
					imageView.setAdjustViewBounds(true);
					imageView.setMaxWidth(dp2px(135));
					imageView.setMaxHeight(dp2px(135));
					imageView.setMinimumWidth(dp2px(45));
					imageView.setMinimumHeight(dp2px(45));

					tableRow.addView(imageView);
					String url = pic_urls.get(i * 3 + j);
					if (url.endsWith(".gif")) {
						Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE)
								.placeholder(R.drawable.image_default).centerCrop().into(imageView);
					} else {
						Log.e(TAG, "url=" + url);
						Glide.with(getContext()).load(url).placeholder(R.drawable.image_default).centerCrop()
								.into(imageView);
					}
				}
				tableRow.setPadding(0, dp2px(5), 0, 0);
				addView(tableRow, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		}
	}

	private int dp2px(int dp) {
		float scrall = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * scrall + 0.5f);
	}
}
