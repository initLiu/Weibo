package com.lzp.weibo.adapter;

import java.util.HashMap;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzp.weibo.R;
import com.lzp.weibo.text.WeiboText;
import com.lzp.weibo.widget.CommentLayout;
import com.lzp.weibo.widget.MultiImageView;
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
 * 好友微博列表
 * 
 * @author SKJP
 *
 */
public class FriendsTimelineAdapter extends BaseAdapter{

	private static final String TAG = FriendsTimelineAdapter.class.getSimpleName();
	private StatusList mStatusList;
	private Context mContext;
	private GetCommentsListener mCommentsListener;
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

	public FriendsTimelineAdapter(Context context,GetCommentsListener listener) {
		mContext = context;
		mStatusList = new StatusList();
		mCommentsListener = listener;
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
//			holder.layoutBottomBarRepost.setOnClickListener(this);
			holder.layoutComment = (CommentLayout)convertView.findViewById(R.id.friend_status_comments);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		fillData(holder, position);

		return convertView;
	}

	private void fillData(ViewHolder holder, int position) {
		Status status = mStatusList.statusList.get(position);
		Glide.with(mContext).load(status.user.profile_image_url).crossFade().placeholder(R.drawable.ic_weibo)
				.into(holder.imageFace);
		holder.textName.setText(status.user.screen_name);
		holder.textTime.setText(formatTime(status.created_at));
		holder.textSource.setText(Html.fromHtml(status.source).toString());

		holder.textContent.setSpannableFactory(WeiboText.SPANNABLE_FACTORY);
		holder.textContent.setMovementMethod(LinkMovementMethod.getInstance());
		holder.textContent.setLinkTextColor(Color.parseColor("#1C86EE"));
		holder.textContent.setText(new WeiboText(status.text, WeiboText.GRAB_LINKS));

		if (status.pic_urls != null && !status.pic_urls.isEmpty()) {	
			//解决listview图片加载错位问题。图片加载错位是由于convertView复用导致的
			//1、原因分析
			//ListView item缓存机制：为了使得性能更优，ListView会缓存行item(某行对应的View)。ListView通过adapter的getView函数获得每行的item。滑动过程中，
			//a. 如果某行item已经滑出屏幕，若该item不在缓存内，则put进缓存，否则更新缓存；
			//b. 获取滑入屏幕的行item之前会先判断缓存中是否有可用的item，如果有，做为convertView参数传递给adapter的getView。
			//给每个View添加一个tag，加载之前先判断这个view有没有tag，如果有表示这个view是复用的之前就清除掉这个view的图片，重新加载新的图片
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
			sb.append("@").append(status.retweeted_status.user.screen_name).append(" :")
					.append(status.retweeted_status.text);
			holder.textRetstatus.setText(new WeiboText(sb.toString(), WeiboText.GRAB_LINKS));
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
		
		holder.layoutBottomBarComment.setOnClickListener(new CommentClickListener(holder.layoutComment, status.id));
		holder.textBottomBarCommnetNum.setText(mContext.getResources().getString(R.string.comment));
		holder.layoutBottomBarComment.setTag(status.id);//设置微博id，在获取评论的时候需要用到
		if (status.comments_count != 0) {
			holder.textBottomBarCommnetNum.setText(status.comments_count + "");
		}
		holder.textBottomBarRepostNum.setText(mContext.getResources().getString(R.string.report));
		if (status.reposts_count != 0) {
			holder.textBottomBarRepostNum.setText(status.reposts_count + "");
		}
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
			return "昨天";
		} else if (time.monthDay - cday > 1) {
			return time.monthDay - cday + "天前";
		} else if (time.monthDay - cday == 0) {
			if (time.hour - chour >= 1) {
				return time.hour - chour + "小时前";
			} else if (time.minute - cminute > 1) {
				return time.minute - cminute + "分钟前";
			} else {
				return "刚刚";
			}
		}
		return "";
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
	}

	class CommentClickListener implements OnClickListener {

		/** 评论list*/
		private View commentLayout;
		/** 微博id*/
		private String id;

		public CommentClickListener(View commentlayout,String id) {
			this.commentLayout = commentlayout;
			this.id = id;
		}

		@Override
		public void onClick(View v) {
			mCommentsListener.getComments(id, commentLayout);
		}
	}
	
	public interface GetCommentsListener {
		/**
		 * 获取微博评论列表
		 * @param id 微博id
		 * @param commentLayout 显示评论列表的view
		 */
		public void getComments(String id, View commentLayout);
	}
}
