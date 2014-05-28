package com.pengjun.android.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.pengjun.android.utils.AdResourceUtils;
import com.pengjun.utils.FileUtils;
import com.pengjun.utils.NetWorkUtils;

public class LyricTextView extends TextView {

	public class Song {

		private String songName;
		private String lyricUrl;
		private int songId;
		private String songUrl;

		public String getSongName() {
			return songName;
		}

		public void setSongName(String songName) {
			this.songName = songName;
		}

		public String getLyricUrl() {
			return lyricUrl;
		}

		public void setLyricUrl(String lyricUrl) {
			this.lyricUrl = lyricUrl;
		}

		public int getSongId() {
			return songId;
		}

		public void setSongId(int songId) {
			this.songId = songId;
		}

		public String getSongUrl() {
			return songUrl;
		}

		public void setSongUrl(String songUrl) {
			this.songUrl = songUrl;
		}

	}

	private int index = 0;
	private Context context;

	private List<Sentence> list;
	private float lineHeight;
	private Paint mPaint, mCurrPaint;
	private Song song;
	private float textSize = 0;
	private int scrollOffset = 0;

	private LoadLyricTask task;
	private File lyricDir;

	private Scroller scroller;

	public LyricTextView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public LyricTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LyricTextView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		this.context = context;
		init();
	}

	public void setScrollOffset(int offset) {
		this.scrollOffset = offset;
	}

	private void init() {

		scroller = new Scroller(getContext(), new AccelerateInterpolator());
		list = new ArrayList<Sentence>();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTypeface(Typeface.SERIF);
		mPaint.setColor(Color.GRAY);
		mPaint.setTextAlign(Paint.Align.CENTER);

		mCurrPaint = new Paint();
		mCurrPaint.setAntiAlias(true);
		mCurrPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mCurrPaint.setColor(Color.WHITE);
		mCurrPaint.setShadowLayer(10, 0, 0, Color.BLUE);
		mCurrPaint.setTextAlign(Paint.Align.CENTER);

		textSize = getTextSize();

		mPaint.setTextSize(textSize);
		lineHeight = (float) (mPaint.measureText("M") * 1.8);
		mCurrPaint.setTextSize(textSize);

		lyricDir = new File(getContext().getCacheDir().getAbsolutePath()
				.concat(File.separator).concat("lyric"));
		if (!lyricDir.exists()) {
			lyricDir.mkdir();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// Log.d(String.format("scrollY:%d", getScrollY()));
		int current = index;
		Paint p = mPaint;
		Paint cp = mCurrPaint;
		float x = getWidth() / 2;
		float y = getHeight() / 2;

		int top = getScrollY();
		int bottom = top + getHeight();

		int lines = list.size();

		// Log.v("draw", "current:" + current + "lines:" + lines);
		if (current >= lines || current < 0) {
			return;
		}

		for (int i = 0; i < lines; i++) {
			String[] sps = list.get(i).getSplits();
			// Log.v("draw", "x:" + x + "y:" + y);
			// Log.v("draw", "top:" + top + "bottom:" + bottom);
			if (y < top - lineHeight || y > bottom + lineHeight) {
				y += lineHeight;
				continue;
			}
			// Log.v("draw", "x:" + x + "y:" + y);
			// Log.v("draw", "sps:" + sps);
			if (i == current) {
				for (String sp : sps) {
					canvas.drawText(sp, x, y, cp);
					y += lineHeight;
				}
			} else {
				for (String sp : sps) {
					canvas.drawText(sp, x, y, p);
					y += lineHeight;
				}
			}
		}
	}

	private void parseLine(String line) {
		if (line.equals("")) {
			return;
		}
		Matcher m = Pattern.compile("(?<=\\[).*?(?=\\])").matcher(line);
		List<String> temp = new ArrayList<String>();
		int length = 0;
		while (m.find()) {
			String s = m.group();
			temp.add(s);
			length += (s.length() + 2);
		}
		String content = line.substring(length > line.length() ? line.length()
				: length);
		content = content.trim();
		if (content.equals("")) {
			return;
		}
		for (String s : temp) {
			long t = parseTime(s);
			if (t != -1) {
				list.add(new Sentence(content, t));
			}
		}

	}

	private long parseTime(String time) {
		String[] ss = time.split("\\:|\\.");
		// 如果 是两位以后，就非法了
		if (ss.length < 2) {
			return -1;
		} else if (ss.length == 2) {// 如果正好两位，就算分秒
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				if (min < 0 || sec < 0 || sec >= 60) {
					throw new RuntimeException("数字不合法!");
				}
				return (min * 60 + sec) * 1000L;
			} catch (Exception exe) {
				return -1;
			}
		} else if (ss.length == 3) {// 如果正好三位，就算分秒，十毫秒
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				int mm = Integer.parseInt(ss[2]);
				if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
					throw new RuntimeException("数字不合法!");
				}
				return (min * 60 + sec) * 1000L + mm * 10;
			} catch (Exception exe) {
				return -1;
			}
		} else {// 否则也非法
			return -1;
		}
	}

	public void setSong(Song song) {

		scroller.setFinalY(0);
		super.scrollTo(0, 0);

		list.clear();
		index = 0;
		if (song == null) {
			return;
		}
		this.song = song;
		// startY = mY / 2;// 重置开始位置
		// if (task != null && task.getStatus().equals(Status.RUNNING)) {
		// return;
		// }
		list.add(new Sentence(song.getSongName(), Integer.MIN_VALUE,
				Integer.MAX_VALUE));
		list.add(new Sentence("正在搜索歌词...", Integer.MIN_VALUE, Integer.MAX_VALUE));
		if (AdResourceUtils.checkNetwork(context)) {
			task = new LoadLyricTask();
			task.execute();
		}
		invalidate();
	}

	private void setLyric(String content) {
		list.clear();
		// 如果歌词的内容为空,则后面就不用执行了
		// 直接显示歌曲名就可以了
		if (content == null || content.trim().equals("")) {
			// Log.d("lyric file content is null");
			scroller.setFinalY(0);
			list.add(new Sentence(song.getSongName(), Integer.MIN_VALUE,
					Integer.MAX_VALUE));
			list.add(new Sentence("没找到歌词", Integer.MIN_VALUE, Integer.MAX_VALUE));
			return;
		}
		BufferedReader br = new BufferedReader(new StringReader(content));
		String temp = null;

		try {
			while ((temp = br.readLine()) != null) {
				parseLine(temp.trim());
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 读进来以后就排序了
		Collections.sort(list, new Comparator<Sentence>() {

			@Override
			public int compare(Sentence o1, Sentence o2) {
				return (int) (o1.getFromTime() - o2.getFromTime());
			}
		});
		// 处理第一句歌词的起始情况,无论怎么样,加上歌名做为第一句歌词,并把它的
		// 结尾为真正第一句歌词的开始
		if (list.size() == 0) {
			list.add(new Sentence(song.getSongName(), 0, Integer.MAX_VALUE));
			return;
		} else {
			Sentence first = list.get(0);
			list.add(0,
					new Sentence(song.getSongName(), 0, first.getFromTime()));
		}

		int size = list.size();
		for (int i = 0; i < size; i++) {
			Sentence next = null;
			if (i + 1 < size) {
				next = list.get(i + 1);
			}
			Sentence now = list.get(i);
			if (next != null) {
				now.setToTime(next.getFromTime() - 1);
			}
			// Log.d("xiami", String.format("%d - %d", now.fromTime,
			// now.toTime));
		}
		// 如果就是没有怎么办,那就只显示一句歌名了
		if (list.size() == 1) {
			list.get(0).setToTime(Integer.MAX_VALUE);
		} else {
			Sentence last = list.get(list.size() - 1);
			last.setToTime(Integer.MAX_VALUE);
		}

	}

	/**
	 * 将长度分割为短句 在设置歌词时进行
	 * 
	 * @param content
	 * @return
	 */
	protected String[] splitText(String content) {
		int maxWidth = (int) (getWidth() * 0.9);
		Paint p = mCurrPaint;
		ArrayList<String> splits = new ArrayList<String>();
		int end = content.length();
		int next = 0;
		while (next < end) {
			int bPoint = p.breakText(content, next, end, true, maxWidth, null);

			if (bPoint == 0)
				break;
			splits.add(content.substring(next, next + bPoint));
			next += bPoint;
		}
		String[] sps = new String[splits.size()];
		return splits.toArray(sps);
	}

	public void update(long time) {
		int newIndex = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isInTime(time)) {
				newIndex = i;
				break;
			}
		}
		if (newIndex > -1) {
			if (index != newIndex) {
				if (scroller != null) {
					int dy = (int) (newIndex * lineHeight) - getScrollY()
							- scrollOffset;
					if (scroller.isFinished()) {
						scroller.startScroll(0, scroller.getCurrY(), 0, dy, 600);
					} else {
						scroller.setFinalY((int) (newIndex * lineHeight - scrollOffset));
					}

				}
				index = newIndex;
			}
		} else {
			index = -1;
		}
		invalidate();
	}

	@Override
	public void scrollTo(int x, int y) {
		if (y == 0)
			return;
		super.scrollTo(x, y);
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			int y = scroller.getCurrY();
			scrollTo(0, y);
			postInvalidate();
		} else {
		}
	}

	public class LoadLyricTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String lyric = null;
			lyric = getLyricFromFile(song);
			if (lyric == null && LyricTextView.this.song.getLyricUrl() != null) {
				lyric = getLyricFromWeb(song);
			}
			return lyric;
		}

		public String getLyricFromWeb(Song song) {

			File file = new File(lyricDir, String.format("%d.lrc",
					song.getSongId()));
			if (!file.exists() && song.getLyricUrl() != null
					&& !"null".equals(song.getLyricUrl().trim())) {
				try {
					String str = NetWorkUtils.getStrFromUrl(song.getLyricUrl());
					FileOutputStream out = new FileOutputStream(file);
					byte[] data = str.getBytes();
					if (data == null) {
						return new String();
					}
					out.write(data);
					out.close();
					return new String(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new String();
		}

		private String getLyricFromFile(Song song) {
			if (song.getLyricUrl() == null) {
				return new String();
			}
			File file = new File(lyricDir, String.format("%d.lrc",
					song.getSongId()));
			if (file.exists()) {
				byte[] c = FileUtils.readFile(file);
				return new String(c);
			}
			return new String();
		}

		@Override
		protected void onPostExecute(String result) {
			LyricTextView.this.setLyric(result);
		}
	}

	public class Sentence implements Serializable {

		private static final long serialVersionUID = 20071125L;
		private String content;// 这一句的内容
		private long fromTime;// 这句的起始时间,时间是以毫秒为单位
		private String[] splits;
		private long toTime;// 这一句的结束时间

		public Sentence(String content) {
			this(content, 0, 0);
		}

		public Sentence(String content, long fromTime) {
			this(content, fromTime, 0);
		}

		public Sentence(String content, long fromTime, long toTime) {
			this.splits = LyricTextView.this.splitText(content);
			this.content = content;
			this.fromTime = fromTime;
			this.toTime = toTime;
		}

		/**
		 * 得到这一句的内容
		 * 
		 * @return 内容
		 */
		public String getContent() {
			return content;
		}

		/**
		 * 得到内容的宽度
		 * 
		 * @param g
		 *            画笔
		 * @return 宽度
		 */

		public int getContentWidth(Paint g) {
			return Math.round(g.measureText(content));
		}

		/**
		 * 得到这个句子的时间长度,毫秒为单位
		 * 
		 * @return 长度
		 */
		public long getDuring() {
			return toTime - fromTime;
		}

		public long getFromTime() {
			return fromTime;
		}

		public String[] getSplits() {
			return splits;
		}

		public int getSplitsLines() {
			return splits.length;
		}

		public long getToTime() {
			return toTime;
		}

		/**
		 * 检查某个时间是否包含在某句中间
		 * 
		 * @param time
		 *            时间
		 * @return 是否包含了
		 */
		public boolean isInTime(long time) {
			return time >= fromTime && time <= toTime;
		}

		public void setFromTime(long fromTime) {
			this.fromTime = fromTime;
		}

		public void setToTime(long toTime) {
			this.toTime = toTime;
		}

		@Override
		public String toString() {
			return "{" + fromTime + "(" + content + ")" + toTime + "}";
		}
	}

	/**
	 * Example Code
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// LyricTextView tvLyric = new LyicTextView(args[0]);
		//
		// tvLyric.setVisibility(View.VISIBLE);
		// tvLyric.setText("");
		// tvLyric.setSong(song);
		// tvLyric.setScrollOffset(5);

		// tvLyric.update(playCurTime);
	}

}
