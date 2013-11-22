package com.pengjun.android.loadresource.factory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

import com.pengjun.android.loadresource.LoadResourceManager;

public class ImageFactory implements ObjectFactory {

	public static class Options extends BitmapFactory.Options {
		public int outRotate = 0;
	}

	public final static String IN_PARAM_EXPECT_WIDTH_KEY = "in_expect_width";
	public final static String IN_PARAM_EXPECT_HEIGHT_KEY = "in_expect_height";

	public final static String IN_PARAM_CAN_MOVIE_KEY = "in_can_movie";

	public final static String OUT_PARAM_OPTIONS_KEY = "out_options";

	public static volatile SampleOption sampleOption = SampleOption.OPTIMAL_QUALITY;

	public static enum SampleOption {
		MINIMAL_MEMORY, OPTIMAL_QUALITY
	}

	@Override
	public Result decodeSize(File file, long maxSize, Map<String, Object> params)
			throws IOException {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
				options);

		if (options.outWidth <= 0 || options.outHeight <= 0
				|| options.outMimeType == null) {
			if (file.exists()) {
				throw new FileParseException(file.getAbsolutePath() + " "
						+ options.toString());
			} else {
				throw new FileNotFoundException(file.getAbsolutePath() + " "
						+ options.toString());
			}
		}

		if (params != null) {
			params.put(OUT_PARAM_OPTIONS_KEY, options);
		}

		final Map<String, Object> paramsMap = params;

		int fullSize;
		if ("image/png".equalsIgnoreCase(options.outMimeType)) {
			fullSize = options.outHeight * options.outWidth * 4;
		} else if ("image/jpeg".equalsIgnoreCase(options.outMimeType)) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			fullSize = options.outHeight * options.outWidth * 2;
			options.outRotate = getJpegRotate(file.getAbsolutePath());
		} else if ("image/bmp".equalsIgnoreCase(options.outMimeType)) {
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			fullSize = options.outHeight * options.outWidth * 2;
		} else {
			fullSize = options.outHeight * options.outWidth * 4;
		}

		int sampleSize = 1;

		for (; fullSize / (sampleSize * sampleSize) > maxSize; ++sampleSize) {
		}

		switch (sampleOption) {
		case MINIMAL_MEMORY:
			options.inPreferQualityOverSpeed = false;
			if (paramsMap != null) {
				Object ew = paramsMap.get(IN_PARAM_EXPECT_WIDTH_KEY);
				Object eh = paramsMap.get(IN_PARAM_EXPECT_HEIGHT_KEY);

				int expectWidth;
				int expectHeight;

				if (ew instanceof Integer) {
					expectWidth = (Integer) ew;
				} else {
					expectWidth = options.outWidth;
				}

				if (eh instanceof Integer) {
					expectHeight = (Integer) eh;
				} else {
					expectHeight = options.outHeight;
				}

				sampleSize = Math.max(sampleSize, Math.min(options.outWidth
						/ expectWidth, options.outHeight / expectHeight));
			}
			break;
		default:
			options.inPreferQualityOverSpeed = true;
			break;
		}

		options.inSampleSize = ceil2N(sampleSize);
		return new Result(null, fullSize
				/ (options.inSampleSize * options.inSampleSize), options);
	}

	public static int ceil2N(int number) {
		final int highestOneBit = Integer.highestOneBit(number);
		return highestOneBit == number ? number : highestOneBit << 1;
	}

	public static boolean is2N(int number) {
		return (number & (number - 1)) == 0;
	}

	@Override
	public Result decodeFile(File file, Map<String, Object> params, Object opt)
			throws IOException {
		Options options = (Options) opt;
		if ("image/gif".equalsIgnoreCase(options.outMimeType)) {
			final Boolean canMovie = (Boolean) params
					.get(IN_PARAM_CAN_MOVIE_KEY);
			if (canMovie != null && canMovie) {
				return new Result(file, options.outHeight * options.outWidth
						* 4, null);
			}
		}

		options.inJustDecodeBounds = false;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
					options);
			if (bitmap == null) {
				if (file.exists()) {
					throw new FileParseException(file.getAbsolutePath() + " "
							+ options.toString());
				} else {
					throw new FileNotFoundException(file.getAbsolutePath()
							+ " " + options.toString());
				}
			}
			return new Result(bitmap,
					bitmap.getRowBytes() * bitmap.getHeight(), opt);
		} catch (java.lang.OutOfMemoryError e) {
			throw new FileParseException(e);
		}
	}

	@Override
	public void destroy(Object obj, Object opt) {
		if (obj instanceof Bitmap) {
			Bitmap bitmap = (Bitmap) obj;
			if (!bitmap.isRecycled()) {
				synchronized (bitmap) {
					bitmap.recycle();
				}
			}
		}
	}

	@Override
	public boolean verify(Object obj, Map<String, Object> params, Object opt) {

		if (opt != null && params != null) {
			Options options = (Options) opt;
			params.put(OUT_PARAM_OPTIONS_KEY, options);
			if (obj instanceof File) {
				final Boolean canMovie = (Boolean) params
						.get(IN_PARAM_CAN_MOVIE_KEY);
				return canMovie != null && canMovie;
			} else {
				final Map<String, Object> paramsMap = params;
				Object ew = paramsMap.get(IN_PARAM_EXPECT_WIDTH_KEY);
				Object eh = paramsMap.get(IN_PARAM_EXPECT_HEIGHT_KEY);

				int expectWidth = -1;
				int expectHeight = -1;

				if (ew instanceof Integer) {
					expectWidth = Math.min((Integer) ew, options.outWidth);
				}

				if (eh instanceof Integer) {
					expectHeight = Math.min((Integer) eh, options.outHeight);
				}

				Bitmap bitmap = (Bitmap) obj;
				return bitmap.getWidth() >= expectWidth
						&& bitmap.getHeight() >= expectHeight;
			}
		} else {
			return true;
		}
	}

	public static int getJpegRotate(String filename) {
		int degree = 0;
		try {
			final ExifInterface exifInterface = new ExifInterface(filename);
			final int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
		}
		return degree;
	}
}
