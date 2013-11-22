package com.pengjun.android.media.musicplay;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.pengjun.android.component.LyricTextView.Song;
import com.pengjun.android.utils.AdLoggerUtils;

public class MusicPlayCtrl {

	private static final String TAG = MusicPlayCtrl.class.getSimpleName();

	private MusicPlayService musicPlayService;
	private static Context context;

	public static final String BD_SONG_CHANGED = "song_changed";
	public static final String BD_SERVICE_BINDED = "service_binded";
	public static final String BD_PlAY_PREPARED = "play_prepared";
	public static final String BD_NETWORK_DOWN = "network_down";
	public static final String BD_NEW_APPLICATION = "new_application";
	public static final String BD_PLAY_NEXT_WHEN_ERROR = "play_next_when_error";

	public MusicPlayCtrl(Context context) {

		this.context = context;
		context.startService(new Intent(context, MusicPlayService.class));
		context.bindService(new Intent(context, MusicPlayService.class),
				mConnection, Context.BIND_AUTO_CREATE);
	}

	public static void unConnectService() {
		context.stopService(new Intent(context, MusicPlayService.class));
	}

	public void unBindService() {
		context.unbindService(mConnection);
	}

	public ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicPlayService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicPlayService = ((MusicPlayService.MusicBinder) service)
					.getService();
			context.sendBroadcast(new Intent(BD_SERVICE_BINDED));
			AdLoggerUtils.printFromPJ(TAG, "onServiceConnected");
		}
	};

	public static MusicPlayCtrl getMusicPlayCtrl(Context context) {

		MusicPlayCtrl newController = new MusicPlayCtrl(context);
		return newController;
	}

	public void setVolumeUp() {
		if (musicPlayService != null) {
			musicPlayService.setVolumeUp();
		}
	}

	public void setVolumeDown() {
		if (musicPlayService != null) {
			musicPlayService.setVolumeDown();
		}
	}

	public boolean isMusicPlayServiceOn() {
		return (musicPlayService != null);
	}

	public int getPosition() {
		if (musicPlayService != null) {
			return musicPlayService.getPosition();
		}
		return 0;

	}

	public int getDuartion() {
		if (musicPlayService != null) {
			return musicPlayService.getDuartion();
		}
		return 0;
	}

	public boolean isReady() {
		if (musicPlayService != null) {
			return musicPlayService.isReady();
		}
		return false;
	}

	public void playSongList(List<Song> songs, int pos) {

		if (musicPlayService != null) {
			musicPlayService.playSongList(songs, pos, true);
		} else {
			Log.e(TAG, "mBinder == null");
		}
	}

	public void playorPause() {
		if (musicPlayService != null) {
			musicPlayService.playOrPause();
		}
	}

	public void start() {
		if (musicPlayService != null) {
			musicPlayService.start();
		}
	}

	public void restore() {
		if (musicPlayService != null) {
			musicPlayService.restore();
		}
	}

	public void pause() {
		if (musicPlayService != null) {
			musicPlayService.pause();
		}
	}

	public void reset() {
		if (musicPlayService != null) {
			musicPlayService.reset();
		}
	}

	public void moveNext() {
		if (musicPlayService != null) {
			musicPlayService.moveNext();
		}
	}

	public void movePre() {
		if (musicPlayService != null) {
			musicPlayService.movePrev();
		}
	}

	public void playCurrent() {
		if (musicPlayService != null) {
			musicPlayService.playCurrent();
		}
	}

	public void playNext() {
		if (musicPlayService != null) {
			musicPlayService.playNext();
		}
	}

	public void playPre() {
		if (musicPlayService != null) {
			musicPlayService.playPrev();
		}
	}

	public int getCurrntPlay() {
		if (musicPlayService != null) {
			return musicPlayService.getCurrentPlay();
		}
		return 0;

	}

	public int getCurrntPos() {
		if (musicPlayService != null) {
			return musicPlayService.getCurrentPos();
		}
		return 0;

	}

	public void seekTo(long newPosition) {
		if (musicPlayService != null) {
			musicPlayService.seekTo(newPosition);
		}
	}

	public Song getCurrentSong() {
		if (musicPlayService != null) {
			return musicPlayService.getCurrentSong();
		}
		return null;
	}

	public List<Song> getCurrentList() {
		if (musicPlayService != null) {
			return musicPlayService.getCurrentList();
		}
		return null;
	}

	public boolean isPlaying() {
		if (musicPlayService != null) {
			return musicPlayService.isPlaying();
		}
		return false;
	}

	public void setListplayerOn() {
		if (musicPlayService != null) {
			musicPlayService.setListPlayerOn();
		}
	}

	public void setRandplayerOn() {
		if (musicPlayService != null) {
			musicPlayService.setRandPlayerOn();
		}
	}

	public boolean isRandPlayerOn() {
		if (musicPlayService != null) {
			return musicPlayService.isRandPlayerOn();
		}
		return false;
	}

}
