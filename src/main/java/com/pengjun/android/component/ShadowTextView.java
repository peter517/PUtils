package com.pengjun.android.component;

public class ShadowTextView {

	// layout.xml
	// <?xml version="1.0" encoding="utf-8"?>
	// <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	// xmlns:putils="http://schemas.android.com/apk/res/com.pengjun.putils"//
	// com.pengjun.putils == apk package name

	// android:layout_width="match_parent"
	// android:layout_height="@dimen/tui_line_height_1"
	// android:minHeight="@dimen/tui_line_height_1"
	// android:orientation="horizontal"
	// >
	//
	// <com.yunos.xiami.view.ShadowTextView
	// android:id="@+id/pll.songname"
	// android:layout_width="match_parent"
	// android:layout_height="wrap_content"
	// android:layout_gravity="left|center_vertical"
	// android:layout_weight="4"
	// android:ellipsize="end"
	// android:maxLength="24"
	// putils:shadowDx="0"
	// putils:shadowDy="0"
	// putils:shadowRadius="10"
	// putils:shadowColors="@drawable/textview_item_shadowcolor"
	// android:singleLine="true"
	// android:textColor="@drawable/textview_item_color"
	// android:textSize="@dimen/tui_text_size_3" />
	//
	// </LinearLayout>

	// attrs.xml
	// <?xml version="1.0" encoding="utf-8"?>
	// <resources>
	// <declare-styleable name="ShadowTextView">
	// <attr name="shadowColors" format="color|reference"/>
	// <attr name="shadowDx" format="float"/>
	// <attr name="shadowDy" format="float"/>
	// <attr name="shadowRadius" format="float"/>
	// </declare-styleable>
	//
	// </resources>

	// ShdowTextView.java
	// private ColorStateList mShadowColors;
	// private float mShadowDx;
	// private float mShadowDy;
	// private float mShadowRadius;
	//
	// public ShadowTextView(Context context) {
	// super(context);
	// }
	//
	// public ShadowTextView(Context context, AttributeSet attrs) {
	// super(context, attrs, 0);
	// init(context, attrs, 0);
	// }
	//
	// public ShadowTextView(Context context, AttributeSet attrs, int defStyle)
	// {
	// super(context, attrs, defStyle);
	// init(context, attrs, defStyle);
	// }
	//
	// private void init(Context context, AttributeSet attrs, int defStyle) {
	// TypedArray a = context.obtainStyledAttributes(attrs,
	// R.styleable.ShadowTextView);
	//
	// final int attributeCount = a.getIndexCount();
	// for (int i = 0; i < attributeCount; i++) {
	// int curAttr = a.getIndex(i);
	// switch (curAttr) {
	// case R.styleable.ShadowTextView_shadowColors:
	// mShadowColors = a.getColorStateList(curAttr);
	// break;
	// case R.styleable.ShadowTextView_shadowDx:
	// mShadowDx = a.getFloat(curAttr, 0);
	// break;
	// case R.styleable.ShadowTextView_shadowDy:
	// mShadowDy = a.getFloat(curAttr, 0);
	// break;
	// case R.styleable.ShadowTextView_shadowRadius:
	// mShadowRadius = a.getFloat(curAttr, 0);
	// break;
	// default:
	// break;
	// }
	// }
	//
	// a.recycle();
	// updateShadowColor();
	// }
	//
	// private void updateShadowColor() {
	// if (mShadowColors != null) {
	// setShadowLayer(mShadowRadius, mShadowDx, mShadowDy,
	// mShadowColors.getColorForState(getDrawableState(), 0));
	// invalidate();
	// }
	// }
	//
	// @Override
	// protected void drawableStateChanged() {
	// super.drawableStateChanged();
	// updateShadowColor();
	// }
}
