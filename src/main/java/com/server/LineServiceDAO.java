package com.server;
/*
 *  LineServiceDAO class returns an instance of mapDB wrapped in ConcurrentMap reference. In other words, Loads mapDB and return its instance.
 */

import java.util.concurrent.*;

import org.mapdb.*;

public class LineServiceDAO {
	DB db = null;
	
	public ConcurrentMap<Long, Long> initDB(String dbPath) {
		db = DBMaker.fileDB(dbPath).fileMmapEnable().make();
		@SuppressWarnings("unchecked")
		ConcurrentMap<Long, Long> map = (ConcurrentMap<Long, Long>) db.hashMap(Constants.STR_BYTE_LINE_MAP_NAME).createOrOpen();
		Constants.maxLineNumber = map.size();
		return map;		
	}	
}
