package com.pengjun.android.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return createView(inflater, container, savedInstanceState);
	}
}
