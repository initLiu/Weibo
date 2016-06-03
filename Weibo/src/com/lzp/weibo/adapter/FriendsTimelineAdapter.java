package com.lzp.weibo.adapter;

import java.util.HashMap;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzp.weibo.R;
import com.lzp.weibo.text.WeiboText;
import com.lzp.weibo.text.WeiboText.WeiboTextClicklistener;
import com.lzp.weibo.widget.CommentLayout;
import com.lzp.weibo.widget.MultiImageView;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ����΢���б�
 * 
 * @author SKJP
 *
 */
public class FriendsTimelineAdapter extends BaseAdapter{

	private static final String TAG = FriendsTimelineAdapter.class.getSimpleName();
	private StatusList mStatusList;
	private Context mContext;
	private WeiboTextClicklistener mTextClicklistener;
	private Map<String, Integer> mMonths = new HashMap<String, Integer>();
	{
		mMonths.put("Jan", 1);
		mMonths.put("Feb", 2);
		mMonths.put("Mar", 3);
		mMonths.put("Apr", 4);
		mMonths.put("May", 5);
		mMonths.put("Jun", 6);
		mMonths.put("Jul", 7);
		mMonths.put("Aug", 8);
		mMonths.put("Sep", 9);
		mMonths.put("Oct", 10);
		mMonths.put("Nov", 11);
		mMonths.put("Dec", 12);
	}

	public FriendsTimelineAdapter(Context context) {
		mContext = context;
		mStatusList = new StatusList();
	}

	public void setWeiboTextClicklistener(WeiboTextClicklistener clicklistener){
		this.mTextClicklistener = clicklistener;
	}
	
	public void setData(StatusList statusList) {
		if (statusList != null) {
			mStatusList = statusList;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		int count = mStatusList.statusList == null ? 0 : mStatusList.statusList.size();
		return count;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_status, parent, false);
			holder = new ViewHolder();
			holder.imageFace = (ImageView) convertView.findViewById(R.id.friend_status_face);
			holder.textName = (TextView) convertView.findViewById(R.id.friend_status_name);
			holder.textTime = (TextView) convertView.findViewById(R.id.friend_status_time);
			holder.textSource = (TextView) convertView.findViewById(R.id.friend_status_source);
			holder.textContent = (TextView) convertView.findViewById(R.id.friend_status_content);
			holder.imageImage = (ImageView) convertView.findViewById(R.id.friend_status_image);
			holder.multiPicUrls = (MultiImageView)convertView.findViewById(R.id.friend_pic_urls);
			holder.layoutRetweeted = (LinearLayout)convertView.findViewById(R.id.retweeted_status_layout);
			holder.textRetstatus = (TextView)convertView.findViewById(R.id.retweeted_status_content);
			holder.multiRetPicUrls = (MultiImageView)convertView.findViewById(R.id.retweeted_status_pic_urls);
			holder.layoutBottomBarComment = (LinearLayout)convertView.findViewById(R.id.friend_status_bottom_bar_comment);
			holder.layoutBottomBarRepost = (LinearLayout)convertView.findViewById(R.id.friend_status_bottom_bar_repost);
			holder.textBottomBarCommnetNum = (TextView)convertView.findViewById(R.id.friend_status_bottom_bar_comment_num);
			holder.textBottomBarRepostNum = (TextView)convertView.findViewById(R.id.friend_status_bottom_bar_repost_num);
			holder.layoutComment = (CommentLayout)convertView.findViewById(R.id.friend_status_comments);
			
			holder.layoutBottomBarComment.setOnClickListener(new CommentClickListener(holder));
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		fillData(holder, position);

		return convertView;
	}

	private void fillData(ViewHolder holder, int position) {
		Status status = mStatusList.statusList.get(position);
		if (status == null) {
			return;
		}
		holder.weiId = status.id;
		
		Glide.with(mContext).load(status.user.profile_image_url).crossFade().placeholder(R.drawable.ic_weibo)
				.into(holder.imageFace);
		holder.textName.setText(status.user.screen_name);
		holder.textTime.setText(formatTime(status.created_at));
		holder.textSource.setText(Html.fromHtml(status.source).toString());

		holder.textContent.setSpannableFactory(WeiboText.SPANNABLE_FACTORY);
		holder.textContent.setLinkTextColor(Color.parseColor("#1C86EE"));
		WeiboText weiboText = new WeiboText(status.text, WeiboText.GRAB_LINKS);
		if (mTextClicklistener != null) {
			weiboText.setLinkOnClicklistener(mTextClicklistener);
		}
		holder.textContent.setText(weiboText);
		holder.textContent.setMovementMethod(LinkMovementMethod.getInstance());

		if (status.pic_urls != null && !status.pic_urls.isEmpty()) {	
			//���listviewͼƬ���ش�λ���⡣ͼƬ���ش�λ������convertView���õ��µ�
			//1��ԭ�����
			//ListView item������ƣ�Ϊ��ʹ�����ܸ��ţ�ListView�Ỻ����item(ĳ�ж�Ӧ��View)��ListViewͨ��adapter��getView�������ÿ�е�item�����������У�
			//a. ���ĳ��item�Ѿ�������Ļ������item���ڻ����ڣ���put�����棬������»��棻
			//b. ��ȡ������Ļ����item֮ǰ�����жϻ������Ƿ��п��õ�item������У���ΪconvertView�������ݸ�adapter��getView��
			//��ÿ��View���һ��tag������֮ǰ���ж����view��û��tag������б�ʾ���view�Ǹ��õ�֮ǰ����������view��ͼƬ�����¼����µ�ͼƬ
			if (holder.multiPicUrls.getTag() != null) {
				holder.multiPicUrls.removeAllPictures();
			}
			holder.multiPicUrls.setTag(position);
			holder.multiPicUrls.setPicUrls(status.pic_urls);
			holder.multiPicUrls.setVisibility(View.VISIBLE);
		} else {
			holder.multiPicUrls.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(status.bmiddle_pic)) {
				holder.imageImage.setVisibility(View.VISIBLE);
				if (status.bmiddle_pic.endsWith(".gif")) {
					Glide.with(mContext).load(status.bmiddle_pic).diskCacheStrategy(DiskCacheStrategy.SOURCE)
							.placeholder(R.drawable.image_default).into(holder.imageImage);
				} else {
					Glide.with(mContext).load(status.bmiddle_pic).placeholder(R.drawable.image_default)
							.into(holder.imageImage);
				}
			} else {
				holder.imageImage.setVisibility(View.GONE);
			}
		}

		if (status.retweeted_status == null) {
			holder.layoutRetweeted.setVisibility(View.GONE);
		} else {
			holder.layoutRetweeted.setVisibility(View.VISIBLE);
			StringBuilder sb = new StringBuilder();
			String sname ="";
			if (status.retweeted_status.user != null) {
				sname = status.retweeted_status.user.screen_name;
			}
			sb.append("@").append(sname).append(" :").append(status.retweeted_status.text);
			holder.textRetstatus.setSpannableFactory(WeiboText.SPANNABLE_FACTORY);
			weiboText = new WeiboText(sb.toString(), WeiboText.GRAB_LINKS);
			if (mTextClicklistener != null) {
				weiboText.setLinkOnClicklistener(mTextClicklistener);
			}
			holder.textRetstatus.setText(weiboText);
			holder.textRetstatus.setLinkTextColor(Color.parseColor("#1C86EE"));
			holder.textRetstatus.setMovementMethod(LinkMovementMethod.getInstance());
			
			if (status.retweeted_status.pic_urls != null && !status.retweeted_status.pic_urls.isEmpty()) {
				if (holder.multiRetPicUrls.getTag() != null) {
					holder.multiRetPicUrls.removeAllPictures();
				}
				holder.multiRetPicUrls.removeAllPictures();
				holder.multiRetPicUrls.setTag(position);
				holder.multiRetPicUrls.setPicUrls(status.retweeted_status.pic_urls);
				holder.multiRetPicUrls.setVisibility(View.VISIBLE);
			}else{
				holder.multiRetPicUrls.setVisibility(View.GONE);
			}
		}

		holder.textBottomBarCommnetNum.setText(mContext.getResources().getString(R.string.comment));
		if (status.comments_count != 0) {
			holder.textBottomBarCommnetNum.setText(status.comments_count + "");
		}
		holder.textBottomBarRepostNum.setText(mContext.getResources().getString(R.string.report));
		if (status.reposts_count != 0) {
			holder.textBottomBarRepostNum.setText(status.reposts_count + "");
		}

		//�����������Ƿ���ʾ
//		Log.e(TAG, "visible="+holder.layoutComment.getVisibility(position)+",position="+position);
		holder.layoutComment.setVisibility(status.id, holder.layoutComment.getVisibility(status.id));
	}

	private String formatTime(String createdtime) {
		if (TextUtils.isEmpty(createdtime)) {
			return "";
		}

		int cyear, cmonth, cday, chour, cminute, csecond;
		Time time = new Time("GMT+8");
		time.setToNow();

		String[] strs = createdtime.split(" ");

		cyear = Integer.parseInt(strs[5]);
		cmonth = mMonths.get(strs[1]);
		cday = Integer.parseInt(strs[2]);

		String[] tmp = strs[3].split(":");
		chour = Integer.parseInt(tmp[0]);
		cminute = Integer.parseInt(tmp[1]);
		csecond = Integer.parseInt(tmp[2]);
		if (time.year != cyear) {
			return cmonth + "-" + cday + " " + cyear;
		} else if ((time.month + 1) != cmonth) {
			return cmonth + "-" + cday;
		} else if (time.monthDay - cday == 1) {
			return "����";
		} else if (time.monthDay - cday > 1) {
			return time.monthDay - cday + "��ǰ";
		} else if (time.monthDay - cday == 0) {
			if (time.hour - chour >= 1) {
				return time.hour - chour + "Сʱǰ";
			} else if (time.minute - cminute > 1) {
				return time.minute - cminute + "����ǰ";
			} else {
				return "�ո�";
			}
		}
		return "";
	}

	class CommentClickListener implements OnClickListener {
		ViewHolder holder;

		public CommentClickListener(ViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onClick(View v) {
			if (holder.layoutComment.getVisibility(holder.weiId) == View.VISIBLE) {
				holder.layoutComment.setVisibility(holder.weiId, View.GONE);
			} else {
				holder.layoutComment.setVisibility(holder.weiId, View.VISIBLE);
			}
		}
	}
	
	class ViewHolder {
		ImageView imageFace;
		TextView textName;
		TextView textTime;
		TextView textSource;
		TextView textContent;
		ImageView imageImage;
		MultiImageView multiPicUrls;
		LinearLayout layoutRetweeted;
		TextView textRetstatus;
		MultiImageView multiRetPicUrls;
		LinearLayout layoutBottomBarComment;
		LinearLayout layoutBottomBarRepost;
		TextView textBottomBarCommnetNum;
		TextView textBottomBarRepostNum;
		CommentLayout layoutComment;
		String weiId;
	}
}

