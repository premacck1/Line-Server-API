package com.server;

/*import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapdb.DBMaker;*/

public class PreprocessorTest {
	/*static Preprocessor preObj = null;
	static String filePath;
	static File f;
	
	@BeforeAll
	static void init() {
		preObj = new Preprocessor();
		filePath = "test_file.txt";
		f = new File(filePath);
		try {
			Constants.STR_DB_PATH = "db_test";
			boolean b = f.createNewFile();
			System.out.println("New Test File Created: "+b);
			FileOutputStream out = new FileOutputStream(f);
			for(int i=1;i<=100;i++) {
				out.write(String.valueOf(i+Constants.NEW_LINE_CHAR).getBytes());
			}
			out.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterAll
	static void cleanup() {
		boolean b = f.delete();
		File f = new File(Constants.STR_DB_PATH);
		f.delete();
		System.out.println("Deleting test file: "+b);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testPreProcessInputFileMemoryMapped() {
		try {
			preObj.preProcessInputFileMemoryMapped(filePath);
			preObj.db = DBMaker.fileDB(Constants.STR_DB_PATH).fileMmapEnable().make();
			ConcurrentMap<Long, Long> map = (ConcurrentMap<Long, Long>) preObj.db.hashMap(Constants.STR_BYTE_LINE_MAP_NAME).open();
			assertTrue(map.size()>0);
			assertNotNull(map.get(1l));
			preObj.db.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
