package com.pengjun.android.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BaseAndroidDbHelper extends OrmLiteSqliteOpenHelper {

	private static final int DBVERSION = 0x01;

	@SuppressWarnings("rawtypes")
	private Class[] dbClass = null;
	private Context context;
	private String dbName;

	protected BaseAndroidDbHelper(Context context, String dbName, Class[] dbClass) {
		super(context, dbName, null, DBVERSION);
		this.dbClass = dbClass;
		this.context = context;
		this.dbName = dbName;
	}

	private AndroidConnectionSource androidConnectionSource;

	public AndroidConnectionSource getAndroidConnectionSource() {
		if (androidConnectionSource == null) {
			androidConnectionSource = new AndroidConnectionSource(new BaseAndroidDbHelper(context, dbName,
					dbClass));
		}
		return androidConnectionSource;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

		try {
			for (int i = 0; i < dbClass.length; i++) {
				TableUtils.createTableIfNotExists(connectionSource, dbClass[i]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {

		for (int i = 0; i < dbClass.length; i++) {
			try {
				TableUtils.dropTable(connectionSource, dbClass[i], false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		onCreate(database, connectionSource);
	}

}
