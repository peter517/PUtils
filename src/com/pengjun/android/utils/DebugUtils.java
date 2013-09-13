package com.pengjun.android.utils;

import android.util.Log;

public class DebugUtils {

	public static void printFromPJ(String info) {
		Log.e("pj", info);
	}

	public static void print(String tag, String info) {
		Log.e(tag, info);
	}
}
