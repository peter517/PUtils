package com.pengjun.android.utils;

import android.content.Context;
import android.media.AudioManager;

public class DeviceUtils {

	public static void setSpeakerVolume(Context context, int level) {
		if (context == null) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (audioManager != null) {
			audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
		}
	}

	public static int getCurrnetSpeakerVolume(Context context) {

		if (context == null) {
			return -1;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public static int getMaxSpeakerVolume(Context context) {

		if (context == null) {
			return -1;
		}

		int level = -1;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (audioManager != null) {
			level = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		return level;
	}

}
