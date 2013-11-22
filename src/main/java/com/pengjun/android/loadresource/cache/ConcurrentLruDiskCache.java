package com.pengjun.android.loadresource.cache;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import android.util.Log;
import android.util.Pair;

import com.pengjun.android.loadresource.LoadResourceManager;

/**
 * 
 * 
 */
public class ConcurrentLruDiskCache {
	private static final String TAG = ConcurrentLruDiskCache.class
			.getSimpleName();

	private static final String TEMP_SUFFIX = ".d";

	private volatile String cacheDirectory = null;
	private volatile long maximumCacheSize;
	protected AtomicLong currentCacheSize = new AtomicLong(-1);
	private final Set<String> preparingSet = Collections
			.synchronizedSet(new HashSet<String>());

	/**
     * 
     * 
     */
	public ConcurrentLruDiskCache(int maximumCacheSize) {
		this.maximumCacheSize = maximumCacheSize;
	}

	/**
     * 
     * 
     */
	final public String getCacheDirectory() {
		return cacheDirectory;
	}

	/**
     * 
     * 
     */
	final public void setCacheDirectory(String dir) {
		String newMendCacheDir = mendPath(dir);
		if (cacheDirectory == null) {
			cacheDirectory = newMendCacheDir;
			currentCacheSize.set(expire());
		} else if (!cacheDirectory.equals(newMendCacheDir)) {
			clear();
			cacheDirectory = newMendCacheDir;
			currentCacheSize.set(expire());
		}
	}

	/**
     * 
     * 
     */
	final public long getCurrentCacheSize() {
		long cacheSize = currentCacheSize.get();
		if (cacheSize < 0) {
			cacheSize = expire();
			currentCacheSize.set(cacheSize);
		}
		return cacheSize;
	}

	/**
     * 
     * 
     */
	final public long getMaximumCacheSize() {
		return maximumCacheSize;
	}

	/**
     * 
     * 
     */
	final public void setMaximumCacheSize(long size) {
		assert size >= 0 : "size>=0";
		boolean expireCache = (size < maximumCacheSize);
		maximumCacheSize = size;
		if (expireCache)
			currentCacheSize.set(expire());
	}

	/**
     * 
     * 
     */
	final public File getData(String urn) {
		String filename = generateCacheFilename(urn);
		File file = new File(filename);
		if (file.exists()) {
			if (!file.setLastModified(System.currentTimeMillis()))
				Log.w(TAG, "set last modified failed: ");
		}
		return file;
	}

	/**
     * 
     * 
     */
	final public File getPreparingData(String urn) {
		String filename = generateTempCacheFilename(urn);
		return new File(filename);
	}

	/**
     * 
     * 
     */
	final public File prepare(String urn) throws IOException {
		if (mkCacheDirs() == null) {
			throw new IOException("mkdirs cache failed");
		}

		String tempCacheFilename = generateTempCacheFilename(urn);

		{
			if (startMission(tempCacheFilename)) {
				Log.v(TAG, "when prepare, start mission: " + tempCacheFilename);
			} else {
				Log.v(TAG, "when prepare, start existing mission : "
						+ tempCacheFilename);
				return null;
			}
		}

		File file = new File(tempCacheFilename);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					Log.w(TAG, "when prepare, create new file failed: ");
				}
				return file;
			} catch (IOException e) {
				Log.w(LoadResourceManager.TAG, "Caught: " + e, e);
				if (endMission(tempCacheFilename)) {
					Log.v(TAG, "when prepare, cancel mission: "
							+ tempCacheFilename);
					throw e;
				} else {
					throw new RuntimeException(
							"when prepare, cancel mission failed: "
									+ tempCacheFilename);
				}
			}
		} else {
			return file;
		}
	}

	/**
     * 
     * 
     */
	final public boolean cancel(File prepareingFile) {
		String prepareingFilename = prepareingFile.getAbsolutePath();
		return endMission(prepareingFilename);
	}

	/**
     * 
     * 
     */
	final public void insert(File preparedFile) {
		String tempCacheFilename = preparedFile.getAbsolutePath();
		String cacheFilename = getCacheFilename(tempCacheFilename);

		File cacheFile = new File(cacheFilename);

		{
			if (endMission(tempCacheFilename)) {
				Log.v(TAG, "when insert, finish mission: " + tempCacheFilename);
			} else {
				throw new RuntimeException(
						"when insert, finish mission failed: "
								+ tempCacheFilename);
			}
		}

		if (cacheFile.exists()) {
			Log.w(TAG, "when insert, cache file existed before rename: "
					+ cacheFilename);
			if (cacheFile.delete()) {
				currentCacheSize.addAndGet(-cacheFile.length());
			} else {
				Log.w(TAG, "when insert, delete existed cache file failed: "
						+ cacheFilename);
			}
		}

		if (preparedFile.renameTo(cacheFile)) {
			if (currentCacheSize.get() < 0)
				currentCacheSize.set(expire());
			else {
				currentCacheSize.addAndGet(cacheFile.length());
				currentCacheSize.set(expire());
			}
		} else {
			Log.w(TAG, "rename cache file failed: " + tempCacheFilename);
		}
	}

	/**
     * 
     * 
     */
	final public boolean remove(String urn) {
		File file = getData(urn);
		final long size = file.length();
		if (file.delete()) {
			currentCacheSize.addAndGet(-size);
			return true;
		}
		return false;
	}

	/**
     * 
     * 
     */
	final public void clear() {
		long size = maximumCacheSize;
		maximumCacheSize = 0;
		currentCacheSize.set(expire());
		maximumCacheSize = size;
	}

	/**
     * 
     * 
     */
	protected long expire() {
		Log.v(TAG, "expire");

		assert cacheDirectory != null : "cacheDirectory!=null";

		long goal = (maximumCacheSize * 9) / 10;

		final long cacheSize = currentCacheSize.get();
		if (0 <= cacheSize && cacheSize < goal)
			return cacheSize;

		File dir = new File(cacheDirectory);
		if (!dir.exists())
			return 0;

		File[] files = dir.listFiles();

		if (files == null || files.length <= 0)
			return 0;

		PriorityQueue<Pair<Long, File>> cacheItems = new PriorityQueue<Pair<Long, File>>(
				files.length, new Comparator<Pair<Long, File>>() {
					@Override
					public int compare(Pair<Long, File> lhs,
							Pair<Long, File> rhs) {
						return (int) (lhs.first - rhs.first);
					}
				});

		long totalSize = 0;
		for (int i = 0; i < files.length; ++i) {
			File file = files[i];
			if (file.isFile()) {
				cacheItems
						.offer(new Pair<Long, File>(file.lastModified(), file));

				if (!isTempFilename(file.getAbsolutePath())) {
					totalSize += file.length();
				}
			}
		}

		Pair<Long, File> item = null;
		while (totalSize >= goal && (item = cacheItems.poll()) != null) {
			File file = item.second;
			long fileLength = file.length();

			if (file.delete()) {
				if (!isTempFilename(file.getAbsolutePath()))
					totalSize -= fileLength;
			} else {
				Log.w(TAG,
						"when expire, can't delete file: "
								+ file.getAbsolutePath());
			}

		}

		assert 0 <= totalSize && totalSize <= goal : "0<=totalSize && totalSize<=goal";
		return totalSize;
	}

	/**
     * 
     * 
     */
	private boolean startMission(String prepareingFilename) {
		return preparingSet.add(prepareingFilename);
	}

	/**
     * 
     * 
     */
	private boolean endMission(String prepareingFilename) {
		assert preparingSet.contains(prepareingFilename) : "preparingSet.contains(prepareingFilename)";
		return preparingSet.remove(prepareingFilename);
	}

	/**
     * 
     * 
     */
	private String generateCacheFilename(String urn) {
		assert cacheDirectory != null : "cacheDirectory!=null";
		return cacheDirectory + urn;
	}

	/**
     * 
     * 
     */
	private String generateTempCacheFilename(String urn) {
		assert cacheDirectory != null : "cacheDirectory!=null";
		return generateCacheFilename(urn) + TEMP_SUFFIX;
	}

	/**
     * 
     * 
     */
	private String getCacheFilename(String tempCacheFilename) {
		assert isTempFilename(tempCacheFilename) : "isTempFilename(tempCacheFilename)";
		return tempCacheFilename.substring(0, tempCacheFilename.length()
				- TEMP_SUFFIX.length());
	}

	/**
     * 
     * 
     */
	private boolean isTempFilename(String filename) {
		return filename.endsWith(TEMP_SUFFIX);
	}

	/**
     * 
     * 
     */
	private File mkCacheDirs() {
		assert cacheDirectory != null : "cacheDirectory!=null";
		File dir = new File(cacheDirectory);
		if (!dir.exists() && !dir.mkdirs()) {
			Log.e(TAG, "mkDirs failed");
			return null;
		}
		return dir;
	}

	/**
     * 
     * 
     */
	private static String mendPath(String path) {
		assert path != null : "path!=null";
		String newPath = path;
		if (!newPath.endsWith("/")) {
			newPath += '/';
		}
		return newPath;
	}
}
