package com.server;

import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class DbInstance {
	static DB db = null;
	private static DbInstance dbInstance = null;
	static ConcurrentMap<Long, Long> map = null;

	private DbInstance() {

	}

	public static ConcurrentMap<Long, Long> getMapInstance(String dbPath) {
		if (dbInstance == null) {
			dbInstance = new DbInstance();
			map = initDB(dbPath);
		}
		return map;
	}

	private static ConcurrentMap<Long, Long> initDB(String dbPath) {
		db = DBMaker.fileDB(dbPath).fileMmapEnable().make();
		@SuppressWarnings("unchecked")
		ConcurrentMap<Long, Long> map = (ConcurrentMap<Long, Long>) db.hashMap(Constants.STR_BYTE_LINE_MAP_NAME)
				.createOrOpen();
		Constants.maxLineNumber = map.size();
		return map;
	}

	public static void closeDB() {
		db.close();
		dbInstance = null;
	}
}
