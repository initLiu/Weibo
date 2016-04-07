package com.lzp.weibo.msg;

import android.os.Parcel;
import android.os.Parcelable;

public class ToAppMsg implements Parcelable {
	private String response;
	private Command cmd;

	public ToAppMsg() {
	}

	public ToAppMsg(Parcel in) {
		readFromParcel(in);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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
		dest.writeString(response);
		dest.writeSerializable(cmd);
	}

	private void readFromParcel(Parcel in) {
		response = in.readString();
		cmd = (Command) in.readSerializable();
	}

	public static final Parcelable.Creator<ToAppMsg> CREATOR = new Creator<ToAppMsg>() {

		@Override
		public ToAppMsg[] newArray(int size) {
			return new ToAppMsg[size];
		}

		@Override
		public ToAppMsg createFromParcel(Parcel source) {
			return new ToAppMsg(source);
		}
	};
}
