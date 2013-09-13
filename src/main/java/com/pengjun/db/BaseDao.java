package com.pengjun.db;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class BaseDao<T> {

	protected Dao<T, Integer> dao = null;

	protected ConnectionSource cs;

	// DB search error
	public final static List DB_SEARCH_LIST_NOT_FOUND = null;
	public final static float DB_SEARCH_FLOAT_NOT_FOUND = -1.0f;
	public final static String DB_SEARCH_STRING_NOT_FOUND = null;
	public final static int DB_SEARCH_INT_NOT_FOUND = -1;

	protected BaseDao(ConnectionSource cs, Class<T> modelClass) {
		this.cs = cs;
		try {
			dao = DaoManager.createDao(cs, modelClass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insert(T object) {
		try {
			dao.create(object);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public T search(T object) {
		try {
			return (T) dao.queryForMatching(object);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update(T ar) {
		try {
			dao.update(ar);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delete(T ar) {
		try {
			dao.delete(ar);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<T> queryAll() {
		try {
			QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
			return queryBuilder.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return DB_SEARCH_LIST_NOT_FOUND;
	}

}
