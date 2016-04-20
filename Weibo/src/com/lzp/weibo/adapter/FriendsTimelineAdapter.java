package com.lzp.weibo.adapter;

import com.lzp.weibo.R;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ∫√”—Œ¢≤©¡–±Ì
 * 
 * @author SKJP
 *
 */
public class FriendsTimelineAdapter extends BaseAdapter {

	private StatusList mStatusList;
	private Context mContext;

	public FriendsTimelineAdapter(Context context) {
		mContext = context;
		mStatusList = new StatusList();
	}

	public void setData(StatusList statusList) {
		if (statusList != null) {
			mStatusList = statusList;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mStatusList.total_number;
	}

	@Override
	public Object getItem(int position) {
		if (mStatusList.statusList != null) {
			return mStatusList.statusList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_status, parent, false);
		}
		return convertView;
	}

}
