package com.pengjun.db;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class BaseDbHelper {

	private String dbUrl = null;
	@SuppressWarnings("rawtypes")
	private ConnectionSource cs;

	protected BaseDbHelper(String dbUrl, Class[] dbClass) {
		this.dbUrl = dbUrl;
		try {
			for (int i = 0; i < dbClass.length; i++) {
				TableUtils.createTableIfNotExists(getConnectionSource(), dbClass[i]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ConnectionSource getConnectionSource() {
		if (cs == null) {
			try {
				cs = new JdbcConnectionSource(dbUrl);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cs;
	}

}
