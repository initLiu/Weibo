package com.lzp.weibo.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class ToServiceMsg implements Parcelable {
	private String url;
	private Command cmd;

	public ToServiceMsg() {
	}

	public ToServiceMsg(Parcel in) {
		readFromParcel(in);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command cmd) {
		this.cmd = cmd;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeSerializable(cmd);
	}

	private void readFromParcel(Parcel in) {
		try {
			url = in.readString();
			cmd = (Command) in.readSerializable();
		} catch (Throwable e) {
			throw e;
		}
	}

	public static final Parcelable.Creator<ToServiceMsg> CREATOR = new Creator<ToServiceMsg>() {

		@Override
		public ToServiceMsg[] newArray(int size) {
			return new ToServiceMsg[size];
		}

		@Override
		public ToServiceMsg createFromParcel(Parcel source) {
			return new ToServiceMsg(source);
		}
	};
}
