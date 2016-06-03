/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 在SpannableStringInternal的基础上增加了对链接的支持
 * parseLinkSpan
 */
package com.lzp.weibo.text;

import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.GetChars;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;

public class WeiboText implements Spannable, GetChars, Cloneable {

	public static Spannable.Factory SPANNABLE_FACTORY = new Spannable.Factory() {
		@Override
		public Spannable newSpannable(CharSequence source) {
			if (source instanceof WeiboText) {
				try {
					return (WeiboText) ((WeiboText) source).clone();
				} catch (CloneNotSupportedException e) {
				}
			}
			return super.newSpannable(source);
		};
	};

	public WeiboText(CharSequence source, int grabFlag) {
		this(source, 0, source.length(), grabFlag);
	}

	public WeiboText(CharSequence source, int start, int end) {
		this(source, start, end, GRAB_LINKS);
	}

	WeiboText(CharSequence source, int start, int end, int flags) {
		int initial = idealIntArraySize(3);
		try {
			mSpans = new Object[initial];
			mSpanData = new int[initial * 3];
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		mSource = source.toString();
		mText = mSource;

		// 要抓链接
		if ((GRAB_LINKS & flags) == GRAB_LINKS) {
			// Modify by shawn for IVR
			if (mText != null && mText.length() < 1000) {
				// 避免正则表达式被攻击，超过1000个字符，则不再解析了。
				parseLinkSpan();
			}
		}

		if (source instanceof Spanned) {
			Spanned sp = (Spanned) source;
			Object[] spans = sp.getSpans(start, end, Object.class);

			for (int i = 0; i < spans.length; i++) {
				int st = sp.getSpanStart(spans[i]);
				int en = sp.getSpanEnd(spans[i]);
				int fl = sp.getSpanFlags(spans[i]);

				if (st < start)
					st = start;
				if (en > end)
					en = end;

				setSpan(spans[i], st - start, en - start, fl);
			}
		}
	}

	protected void parseLinkSpan() {
		Matcher m = LINK_PATTERN.matcher(mText);
		int s;
		int e;
		char c_pre = 0;
		char c_last = 0;
		String sub_str;
		while (m.find()) {
			s = m.start();
			e = m.end();
			sub_str = mText.substring(s, e);
			addSpan(new LinkSpan(sub_str), s, e, SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * 构造函数使用, 不要public出去
	 * 
	 * @param span
	 * @param start
	 * @param end
	 * @param flags
	 */
	protected void addSpan(Object span, int start, int end, int flags) {
		// 添加span
		if (mSpanCount + 1 >= mSpans.length) {
			int newsize = idealIntArraySize(mSpanCount + 1);
			Object[] newtags = new Object[newsize];
			int[] newdata = new int[newsize * 3];

			System.arraycopy(mSpans, 0, newtags, 0, mSpanCount);
			System.arraycopy(mSpanData, 0, newdata, 0, mSpanCount * 3);

			mSpans = newtags;
			mSpanData = newdata;
		}
		mSpans[mSpanCount] = span;
		mSpanData[mSpanCount * COLUMNS + START] = start;
		mSpanData[mSpanCount * COLUMNS + END] = end;
		mSpanData[mSpanCount * COLUMNS + FLAGS] = flags;
		mSpanCount++;
	}

	public final int length() {
		return mText.length();
	}

	public final char charAt(int i) {
		return mText.charAt(i);
	}

	public final String toString() {
		return mText;
	}

	/* subclasses must do subSequence() to preserve type */

	public final void getChars(int start, int end, char[] dest, int off) {
		mText.getChars(start, end, dest, off);
	}

	public void setSpan(Object what, int start, int end, int flags) {
		int nstart = start;
		int nend = end;

		checkRange("setSpan", start, end);

		if ((flags & Spannable.SPAN_PARAGRAPH) == Spannable.SPAN_PARAGRAPH) {
			if (start != 0 && start != length()) {
				char c = charAt(start - 1);

				if (c != '\n')
					throw new RuntimeException(
							"PARAGRAPH span must start at paragraph boundary" + " (" + start + " follows " + c + ")");
			}

			if (end != 0 && end != length()) {
				char c = charAt(end - 1);

				if (c != '\n')
					throw new RuntimeException(
							"PARAGRAPH span must end at paragraph boundary" + " (" + end + " follows " + c + ")");
			}
		}

		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		for (int i = 0; i < count; i++) {
			if (spans[i] == what) {
				int ostart = data[i * COLUMNS + START];
				int oend = data[i * COLUMNS + END];

				data[i * COLUMNS + START] = start;
				data[i * COLUMNS + END] = end;
				data[i * COLUMNS + FLAGS] = flags;

				sendSpanChanged(what, ostart, oend, nstart, nend);
				return;
			}
		}

		if (mSpanCount + 1 >= mSpans.length) {
			int newsize = idealIntArraySize(mSpanCount + 1);
			Object[] newtags = new Object[newsize];
			int[] newdata = new int[newtags.length * 3];

			System.arraycopy(mSpans, 0, newtags, 0, mSpanCount);
			System.arraycopy(mSpanData, 0, newdata, 0, mSpanCount * 3);

			mSpans = newtags;
			mSpanData = newdata;
		}

		mSpans[mSpanCount] = what;
		mSpanData[mSpanCount * COLUMNS + START] = start;
		mSpanData[mSpanCount * COLUMNS + END] = end;
		mSpanData[mSpanCount * COLUMNS + FLAGS] = flags;
		mSpanCount++;

		if (this instanceof Spannable)
			sendSpanAdded(what, nstart, nend);
	}

	private static int idealIntArraySize(int need) {
		need *= 4;
		for (int i = 4; i < 32; i++) {
			if (need <= (1 << i) - 12) {
				need = (1 << i) - 12;
				break;
			}
		}
		return need / 4;
	}

	public void removeSpan(Object what) {
		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		for (int i = count - 1; i >= 0; i--) {
			if (spans[i] == what) {
				int ostart = data[i * COLUMNS + START];
				int oend = data[i * COLUMNS + END];

				int c = count - (i + 1);

				System.arraycopy(spans, i + 1, spans, i, c);
				System.arraycopy(data, (i + 1) * COLUMNS, data, i * COLUMNS, c * COLUMNS);

				mSpanCount--;

				sendSpanRemoved(what, ostart, oend);
				return;
			}
		}
	}

	public int getSpanStart(Object what) {
		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		for (int i = count - 1; i >= 0; i--) {
			if (spans[i] == what) {
				return data[i * COLUMNS + START];
			}
		}

		return -1;
	}

	public int getSpanEnd(Object what) {
		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		for (int i = count - 1; i >= 0; i--) {
			if (spans[i] == what) {
				return data[i * COLUMNS + END];
			}
		}

		return -1;
	}

	public int getSpanFlags(Object what) {
		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		for (int i = count - 1; i >= 0; i--) {
			if (spans[i] == what) {
				return data[i * COLUMNS + FLAGS];
			}
		}

		return 0;
	}

	public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
		int count = 0;

		int spanCount = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;
		Object[] ret = null;
		Object ret1 = null;

		for (int i = 0; i < spanCount; i++) {
			int spanStart = data[i * COLUMNS + START];
			int spanEnd = data[i * COLUMNS + END];

			if (spanStart > queryEnd) {
				continue;
			}
			if (spanEnd < queryStart) {
				continue;
			}

			if (spanStart != spanEnd && queryStart != queryEnd) {
				if (spanStart == queryEnd) {
					continue;
				}
				if (spanEnd == queryStart) {
					continue;
				}
			}

			// verify span class as late as possible, since it is expensive
			if (kind != null && !kind.isInstance(spans[i])) {
				continue;
			}

			if (count == 0) {
				ret1 = spans[i];
				count++;
			} else {
				if (count == 1) {
					ret = (Object[]) Array.newInstance(kind, spanCount - i + 1);
					ret[0] = ret1;
				}

				int prio = data[i * COLUMNS + FLAGS] & Spanned.SPAN_PRIORITY;
				if (prio != 0) {
					int j;

					for (j = 0; j < count; j++) {
						int p = getSpanFlags(ret[j]) & Spanned.SPAN_PRIORITY;

						if (prio > p) {
							break;
						}
					}

					System.arraycopy(ret, j, ret, j + 1, count - j);
					ret[j] = spans[i];
					count++;
				} else {
					ret[count++] = spans[i];
				}
			}
		}

		if (count == 0) {
			return (T[]) Array.newInstance(kind, 0);
		}
		if (count == 1) {
			ret = (Object[]) Array.newInstance(kind, 1);
			ret[0] = ret1;
			return (T[]) ret;
		}
		if (count == ret.length) {
			return (T[]) ret;
		}

		Object[] nret = (Object[]) Array.newInstance(kind, count);
		System.arraycopy(ret, 0, nret, 0, count);
		return (T[]) nret;
	}

	public int nextSpanTransition(int start, int limit, Class kind) {
		int count = mSpanCount;
		Object[] spans = mSpans;
		int[] data = mSpanData;

		if (kind == null) {
			kind = Object.class;
		}

		for (int i = 0; i < count; i++) {
			int st = data[i * COLUMNS + START];
			int en = data[i * COLUMNS + END];

			if (st > start && st < limit && kind.isInstance(spans[i]))
				limit = st;
			if (en > start && en < limit && kind.isInstance(spans[i]))
				limit = en;
		}

		return limit;
	}

	private void sendSpanAdded(Object what, int start, int end) {
		SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
		int n = recip.length;

		for (int i = 0; i < n; i++) {
			recip[i].onSpanAdded((Spannable) this, what, start, end);
		}
	}

	private void sendSpanRemoved(Object what, int start, int end) {
		SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
		int n = recip.length;

		for (int i = 0; i < n; i++) {
			recip[i].onSpanRemoved((Spannable) this, what, start, end);
		}
	}

	private void sendSpanChanged(Object what, int s, int e, int st, int en) {
		SpanWatcher[] recip = getSpans(Math.min(s, st), Math.max(e, en), SpanWatcher.class);
		int n = recip.length;

		for (int i = 0; i < n; i++) {
			recip[i].onSpanChanged((Spannable) this, what, s, e, st, en);
		}
	}

	private static String region(int start, int end) {
		return "(" + start + " ... " + end + ")";
	}

	private void checkRange(final String operation, int start, int end) {
		if (end < start) {
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " has end before start");
		}

		int len = length();

		if (start > len || end > len) {
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " ends beyond length " + len);
		}

		if (start < 0 || end < 0) {
			throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " starts before 0");
		}
	}

	// Same as SpannableStringBuilder
	@Override
	public boolean equals(Object o) {
		if (o instanceof Spanned && toString().equals(o.toString())) {
			Spanned other = (Spanned) o;
			// Check span data
			Object[] otherSpans = other.getSpans(0, other.length(), Object.class);
			if (mSpanCount == otherSpans.length) {
				for (int i = 0; i < mSpanCount; ++i) {
					Object thisSpan = mSpans[i];
					Object otherSpan = otherSpans[i];
					if (thisSpan == this) {
						if (other != otherSpan || getSpanStart(thisSpan) != other.getSpanStart(otherSpan)
								|| getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan)
								|| getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
							return false;
						}
					} else if (!thisSpan.equals(otherSpan) || getSpanStart(thisSpan) != other.getSpanStart(otherSpan)
							|| getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan)
							|| getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	// Same as SpannableStringBuilder
	@Override
	public int hashCode() {
		int hash = toString().hashCode();
		hash = hash * 31 + mSpanCount;
		for (int i = 0; i < mSpanCount; ++i) {
			Object span = mSpans[i];
			if (span != this) {
				hash = hash * 31 + span.hashCode();
			}
			hash = hash * 31 + getSpanStart(span);
			hash = hash * 31 + getSpanEnd(span);
			hash = hash * 31 + getSpanFlags(span);
		}
		return hash;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// 容错处理
		start = start < 0 ? 0 : start;
		int l = length();
		end = (end > l ? l : end);
		return new WeiboText(this, start, end);
	}

	/**
	 * 标识链接的span
	 * 
	 */
	public class LinkSpan extends ClickableSpan {
		// Modify by shawn for IVR
		protected String mUrl;

		public LinkSpan(String url) {
			this.mUrl = url;
		}

		@Override
		public void onClick(View widget) {
			if (mClickListener == null) {
				return;
			}
			
			String url = mUrl;
			Matcher m = Patterns.WEB_URL.matcher(url);
			// 是个链接
			if (m.find()) {
				mClickListener.onClick(WeiboTextClicklistener.TYPE_URL, mUrl);
				return;
			}

			m = Pattern.compile(TOPIC).matcher(url);
			// 是个话题
			if (m.find()) {
				mClickListener.onClick(WeiboTextClicklistener.TYPE_TOPIC, mUrl);
				return;
			}

			m = Pattern.compile(AT).matcher(url);
			// 是@
			if (m.find()) {
				mClickListener.onClick(WeiboTextClicklistener.TYPE_AT, mUrl);
				return;
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		WeiboText clone = (WeiboText) super.clone();
		// clone.mSpanData = Arrays.copyOfRange(mSpanData, 0, mSpanData.length);
		clone.mSpanData = new int[mSpanData.length];
		System.arraycopy(mSpanData, 0, clone.mSpanData, 0, mSpanData.length);
		// clone.mSpans = Arrays.copyOfRange(mSpans, 0, mSpans.length);
		clone.mSpans = new Object[mSpans.length];
		System.arraycopy(mSpans, 0, clone.mSpans, 0, mSpans.length);
		return clone;
	}

	public void setLinkOnClicklistener(WeiboTextClicklistener listener){
		mClickListener = listener;
	}
	
	public interface WeiboTextClicklistener {
		public static final int TYPE_URL = 0;
		public static final int TYPE_TOPIC = 1;
		public static final int TYPE_AT = 2;

		public void onClick(int type, String content);
	}

	private WeiboTextClicklistener mClickListener;
	
	// 原数据
	public String mSource;
	private String mText;
	private Object[] mSpans;
	private int[] mSpanData;
	private int mSpanCount;

	/* package */ static final Object[] EMPTY = new Object[0];

	private static final int START = 0;
	private static final int END = 1;
	private static final int FLAGS = 2;
	private static final int COLUMNS = 3;

	/**
	 * 不抓取任何东西
	 */
	public static final int NO_ACTION = 0;
	/**
	 * 抓emoj
	 */
	public static final int GRAB_EMOJI = 0x00000001;
	/**
	 * 抓链接/#asdfasdf#/@dddd
	 */
	public static final int GRAB_LINKS = 0x00000002;

	/**
	 * Regular expression to match all IANA top-level domains for WEB_URL. List
	 * accurate as of 2010/02/05. List taken from:
	 * http://data.iana.org/TLD/tlds-alpha-by-domain.txt This pattern is
	 * auto-generated by frameworks/base/common/tools/make-iana-tld-pattern.py
	 */
	public static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL = "(?:" + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
			+ "|(?:biz|b[abdefghijmnorstvwyz])" + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])" + "|d[ejkmoz]"
			+ "|(?:edu|e[cegrstu])" + "|f[ijkmor]" + "|(?:gov|g[abdefghilmnpqrstuwy])" + "|h[kmnrtu]"
			+ "|(?:info|int|i[delmnoqrst])" + "|(?:jobs|j[emop])" + "|k[eghimnprwyz]" + "|l[abcikrstuvy]"
			+ "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])" + "|(?:name|net|n[acefgilopruz])" + "|(?:org|om)"
			+ "|(?:pro|p[aefghklmnrstwy])" + "|qa" + "|r[eosuw]" + "|s[abcdeghijklmnortuvyz]"
			+ "|(?:tel|travel|t[cdfghjklmnoprtvwz])" + "|u[agksyz]" + "|v[aceginu]" + "|w[fs]"
			+ "|(?:xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-80akhbyknj4f|xn\\-\\-9t4b11yi5a|xn\\-\\-deba0ad|xn\\-\\-g6w251d|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-zckzah)"
			+ "|y[etu]" + "|z[amw]))";

	/**
	 * Good characters for Internationalized Resource Identifiers (IRI). This
	 * comprises most common used Unicode characters allowed in IRI as detailed
	 * in RFC 3987. Specifically, those two byte Unicode characters are not
	 * included.
	 */
	public static final String GOOD_IRI_CHAR = "a-zA-Z0-9";// \u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

	/**
	 * Regular expression pattern to match most part of RFC 3987
	 * Internationalized URLs, aka IRIs. Commonly used Unicode characters are
	 * added.
	 */
	public static final Pattern WEB_URL = Pattern
			.compile("((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
					+ "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
					+ "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?" + "((?:(?<!["
					+ GOOD_IRI_CHAR + "])(?:[" + GOOD_IRI_CHAR + "][" + GOOD_IRI_CHAR + "\\-]{0,64}\\.){1,16}" // named
																												// host
					+ TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL + "|(?:(?:25[0-5]|2[0-4]" // or
																					// ip
																					// address
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
					+ "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}" + "|[1-9][0-9]|[0-9])))"
					+ "(?:\\:\\d{1,5})?)" // plus option port number
					+ "(\\/(?:(?:([" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus
																						// option
																						// query
																						// params
					+ "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))|(\\[(?=(?:([" + GOOD_IRI_CHAR
					+ "\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus option query params
					+ "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2})))))*)?" + "(?:\\b|$)");// and
																										// finally,
																										// a
																										// word
																										// boundary
																										// or
																										// end
																										// of
																										// input.
																										// This
																										// is
																										// to
																										// stop
																										// foo.sure
																										// from
																										// matching
																										// as
																										// foo.su

	/**
	 * 话题正则表达式
	 */
	public static final String TOPIC = "#[a-zA-Z0-9\u4e00-\u9fa5]+#";

	/**
	 * @ 正则表达式
	 */
	public static final String AT = "@[a-zA-Z0-9\u4e00-\u9fa5_-]+";

	// 所有链接的的正则表达式
	protected static final String LINK_REGEX = WEB_URL.pattern() + "|" + AT + "|" + TOPIC;

	// 抓链接用的正则表达式
	static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);

	public static final String TAG = WeiboText.class.getSimpleName();
}
