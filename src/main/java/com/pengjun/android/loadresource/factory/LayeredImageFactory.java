package com.pengjun.android.loadresource.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public final class LayeredImageFactory extends ImageFactory {

	private final Bitmap background;
	private final Bitmap shadow;
	private final Bitmap alpha;

	private final int width;
	private final int height;

	public LayeredImageFactory(Bitmap background, Bitmap shadow, Bitmap alpha,
			int width, int height) {
		super();
		this.background = background;
		this.shadow = shadow;
		this.alpha = alpha;
		this.width = width;
		this.height = height;
	}

	@Override
	public Result decodeFile(File file, Map<String, Object> params, Object opt)
			throws IOException {
		Result result = super.decodeFile(file, params, opt);

		Bitmap bitmap = (Bitmap) result.obj;
		Bitmap layeredBitmap = createLayeredBitmap(bitmap);
		bitmap.recycle();
		return new Result(layeredBitmap, layeredBitmap.getRowBytes()
				* layeredBitmap.getHeight(), opt);
	}

	private Bitmap createLayeredBitmap(final Bitmap source) {
		Bitmap bitmap = Bitmap.createScaledBitmap(source, width, height, true);

		Bitmap layeredBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas c = new Canvas(layeredBitmap);

		c.drawBitmap(background, 0, 0, null);
		c.drawBitmap(bitmap, 0, 0, null);
		c.drawBitmap(shadow, 0, 0, null);

		Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		c.drawBitmap(alpha, 0, 0, xferPaint);

		return layeredBitmap;

	}

}
