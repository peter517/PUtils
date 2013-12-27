package com.pengjun.android.component;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class FragmentDirector {

	public static void replaceFragment(FragmentActivity activity, int layoutId,
			Fragment fragment) {
		FragmentTransaction ft = activity.getSupportFragmentManager()
				.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(layoutId, fragment);
		ft.commitAllowingStateLoss();
	}

	// @SuppressLint("NewApi")
	// public static void replaceFragment(Activity activity, int layoutId,
	// android.app.Fragment fragment) {
	// FragmentManager fragmentManager = activity.getFragmentManager();
	// android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
	// ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
	// ft.replace(layoutId, fragment);
	// ft.commitAllowingStateLoss();
	// }

}
