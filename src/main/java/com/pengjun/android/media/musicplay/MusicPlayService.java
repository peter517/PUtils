package com.pengjun.android.media.musicplay;

/**
 * 
 */

import java.io.IOException;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.pengjun.android.component.LyricTextView.Song;
import com.pengjun.android.utils.AdLoggerUtils;
import com.pengjun.android.utils.AdResourceUtils;

public class MusicPlayService extends Service {

	private final IBinder mBinder = new MusicBinder();

	private static final String TAG = MusicPlayService.class.getSimpleName();

	private List<Song> playList;
	private int currentTrack;
	private int currentPlay;
	private MultiPlayer mPlayer;
	private int saveSeekPos = 0;
	private final int DATA_SOURCE_ERROR = -38;

	private AudioManager audioManager;

	public boolean isRandPlayerOn, isListPlayerOn;

	private PowerManager.WakeLock mWakeLock = null;

	public class MusicBinder extends Binder {
		public MusicPlayService getService() {
			return MusicPlayService.this;
		}
	}

	// 监听网络连接变化
	BroadcastReceiver conReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 如果不是正在播放则不处理;播放本地歌曲不处理
			if (mPlayer != null && !mPlayer.isPlaying()) {
				return;
			}

			if (!AdResourceUtils.checkNetwork(MusicPlayService.this)) {

				sendBroadcast(new Intent(MusicPlayCtrl.BD_NETWORK_DOWN));
				return;
			}
		}
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			AdLoggerUtils.printFromTag(TAG, action);
			if (MusicPlayCtrl.BD_NEW_APPLICATION.equals(action)) {
				if (mPlayer != null) {
					AdLoggerUtils.printFromTag(TAG, "onDestroy");
					MusicPlayService.this.onDestroy();
				}
			}
		}

	};

	private void releasePlayer() {

		if (mPlayer != null) {
			AdLoggerUtils.printFromTag(TAG, "releasePlayer");
			mPlayer.release();
			mPlayer = null;
		}
	}

	@Override
	public void onCreate() {

		super.onCreate();

		AdLoggerUtils.printFromTag(TAG, "onCreate " + (mPlayer == null));
		mPlayer = new MultiPlayer();
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);

		IntentFilter f = new IntentFilter();
		f.addAction("android.intent.action.ANY_DATA_STATE");
		f.addAction("android.intent.action.SERVICE_STATE");
		f.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		f.addAction(android.net.wifi.WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(conReceiver, f);

	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.e(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		AdLoggerUtils.printFromTag(TAG, "onDestroy");
		releasePlayer();
		unregisterReceiver(conReceiver);
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void setVolumeUp() {
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	public void setVolumeDown() {
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private int getNextTrack() {
		int len = playList.size();
		if (currentTrack >= len - 1 || currentTrack < 0) {
			return 0;
		}
		return currentTrack + 1;
	}

	private int getPrevTrack() {
		int len = playList.size();
		if (currentTrack > len - 1 || currentTrack <= 0) {
			return len - 1;
		}
		return currentTrack - 1;
	}

	public Song getCurrentSong() {
		if (playList == null || playList.size() == 0) {
			return null;
		}

		return playList.get(Math.min(currentTrack, playList.size() - 1));
	}

	private void playSong() {
		currentPlay = currentTrack;
		Log.d(TAG, "getSongFile:" + getCurrentSong().getSongUrl());
		mPlayer.setDataSourceAsync(getCurrentSong().getSongUrl().replaceAll(
				" ", "%20"));
	}

	public void playSongList(List<Song> songs, int playIndex, boolean startPlay) {
		if (mPlayer.isPlaying()) {
			Log.v(TAG, "pause before play");
			mPlayer.pause();
		}

		playList = songs;
		if (startPlay) {
			currentTrack = playIndex;
			playSong();
		}
	}

	public int getCurrentPlay() {
		return currentPlay;
	}

	public int getCurrentPos() {
		return currentTrack;
	}

	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	public void setLooping(boolean looping) {
		mPlayer.setLooping(looping);
	}

	public void playOrPause() {
		Log.v(TAG, "switch playOrPause");
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mPlayer.start();
		}
	}

	public void start() {
		mPlayer.start();
	}

	public void reset() {
		mPlayer.reset();
	}

	public void moveNext() {
		currentTrack = getNextTrack();
		sendBroadcast(new Intent(MusicPlayCtrl.BD_SONG_CHANGED));
	}

	public void movePrev() {
		currentTrack = getPrevTrack();
		sendBroadcast(new Intent(MusicPlayCtrl.BD_SONG_CHANGED));
	}

	public void playCurrent() {
		playSong();
	}

	public void playNext() {
		currentTrack = getNextTrack();
		sendBroadcast(new Intent(MusicPlayCtrl.BD_SONG_CHANGED));
		playSong();
	}

	public void playPrev() {
		currentTrack = getPrevTrack();
		sendBroadcast(new Intent(MusicPlayCtrl.BD_SONG_CHANGED));
		playSong();
	}

	public List<Song> getCurrentList() {
		return playList;
	}

	public int getDuartion() {
		return mPlayer.getDuration();
	}

	public int getPosition() {
		return mPlayer.getCurrentPosition();
	}

	public boolean isRandPlayerOn() {
		return isRandPlayerOn;
	}

	public void setRandPlayerOn() {
		MusicPlayService.this.isRandPlayerOn = true;
		MusicPlayService.this.isListPlayerOn = false;
	}

	public boolean isListPlayerOn() {
		return isListPlayerOn;
	}

	public void pause() {
		mPlayer.pause();
	}

	public void saveState() {
		if (mPlayer != null) {
			this.saveSeekPos = mPlayer.getCurrentPosition();
			releasePlayer();
		}

	}

	public void restore() {
		mPlayer = new MultiPlayer();
		mPlayer.seekTo(saveSeekPos);
	}

	public void setListPlayerOn() {
		MusicPlayService.this.isListPlayerOn = true;
		MusicPlayService.this.isRandPlayerOn = false;
	}

	public void seekTo(long whereto) {
		mPlayer.seekTo(whereto);
	}

	public boolean isReady() {
		if (mPlayer != null)
			return mPlayer.isReady();
		return false;
	}

	private void updateScreenSaver() {
		if (isPlaying()) {
			setScreenSaverOff();
		} else {
			setScreenSaverOn();
		}
	}

	public void setScreenSaverOn() {

		AdLoggerUtils.printFromTag(TAG, "setScreenSaverOn");
		if (mWakeLock != null) {
			try {
				mWakeLock.release();
				mWakeLock = null;
			} catch (Exception e) {
				Log.w(TAG, "Caught :" + e, e);
			}
		}
	}

	public void setScreenSaverOff() {
		AdLoggerUtils.printFromTag(TAG, "setScreenSaverOff");
		try {
			if (mWakeLock == null) {
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE
						| PowerManager.FULL_WAKE_LOCK, this.getClass()
						.getSimpleName());
				mWakeLock.acquire();
			}
		} catch (Exception e) {
			Log.w(TAG, "Caught :" + e, e);
			mWakeLock = null;
		}
	}

	private class MultiPlayer extends MediaPlayer {

		private boolean mIsInitialized = false;
		private String mPath;
		int retryCount = 0;

		public boolean isReady() {
			return mIsInitialized;
		}

		@Override
		public int getDuration() {
			if (mIsInitialized)
				return super.getDuration();
			else {
				Log.v(TAG, "getDuration not initialized");
				return 0;
			}
		}

		@Override
		public int getCurrentPosition() {
			if (mIsInitialized)
				return super.getCurrentPosition();
			else {
				Log.v(TAG, "getCurrentPosition not initialized");
				return 0;
			}

		}

		/**
		 * You CANNOT use this player anymore after calling release()
		 */
		@Override
		public void release() {
			stop();
			super.release();
		}

		public long seekTo(long whereto) {
			try {
				if (mIsInitialized)
					super.seekTo((int) whereto);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			return whereto;
		}

		public void setDataSourceAsync(String path) {
			try {
				mPath = path;
				Log.d(TAG, "play " + mPath);
				mIsInitialized = false;
				reset();
				try {
					setDataSource(mPath);
				} catch (IllegalStateException e) {
					// i have no idea,wtf!
					e.printStackTrace();
					AdLoggerUtils.printFromTag(TAG,
							"setDataSource IllegalStateException");
					pause();
				}
				setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						Log.d(TAG, "onPrepared " + mPath);
						mIsInitialized = true;
						retryCount = 0;
						mp.start();
						sendBroadcast(new Intent(MusicPlayCtrl.BD_PlAY_PREPARED));
					}
				});
				setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						switch (what) {
						case MediaPlayer.MEDIA_ERROR_SERVER_DIED:

							mPlayer.pause();
							AdLoggerUtils.printFromTag(TAG,
									"MediaPlayer.MEDIA_ERROR_SERVER_DIED");
							break;
						case DATA_SOURCE_ERROR:
							mPlayer.reset();
							AdLoggerUtils
									.printFromTag(TAG, "DATA_SOURCE_ERROR");
							break;
						default:
							AdLoggerUtils.printFromTag(TAG, "onError what: "
									+ what);
							if (retryCount < 10) {
								try {
									Log.d(TAG,
											"mediaPlayer try once again...........");
									setDataSourceAsync(mPath);
									retryCount++;
									return true;
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								AdLoggerUtils.printFromTag(TAG,
										"mediaPlayer source error");
							}
							break;
						}
						mIsInitialized = false;
						sendBroadcast(new Intent(
								MusicPlayCtrl.BD_PLAY_NEXT_WHEN_ERROR));
						return true;
					}
				});
				setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						playNext();
					}
				});
				try {
					prepareAsync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException ex) {
				mIsInitialized = false;
				return;
			} catch (IllegalArgumentException ex) {
				mIsInitialized = false;
				return;
			}
		}

		@Override
		public void start() {
			if (mIsInitialized) {
				super.start();
				AdLoggerUtils.printFromTag(TAG, "start");
				updateScreenSaver();
			}
		}

		@Override
		public void pause() {
			if (mIsInitialized) {
				super.pause();
				AdLoggerUtils.printFromTag(TAG, "pause");
				updateScreenSaver();
			}
		}

		@Override
		public void stop() {
			if (mIsInitialized) {
				super.reset();
				AdLoggerUtils.printFromTag(TAG, "stop");
				updateScreenSaver();
				mIsInitialized = false;
			}
		}
	}
}