package com.server;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TestLineServiceController {
	static LineServiceController lsObj = null;
	static String filePath;
	static File f;

	@BeforeAll
	static void init() {
		Constants.STR_DB_PATH = "db_test";
		lsObj = new LineServiceController();
		filePath = "test_file.txt";
		f = new File(filePath);
		try {
			boolean b = f.createNewFile();
			System.out.println("New Test File Created: " + b);
			FileOutputStream out = new FileOutputStream(f);
			for (int i = 1; i <= 100; i++) {
				out.write(String.valueOf(i + Constants.NEW_LINE_CHAR).getBytes());
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterAll
	static void cleanup() {
		boolean b = f.delete();
		System.out.println("Deleting test file: " + b);
		lsObj.cache.clear();
	}

	@Test
	void testGetLineFromLineNumberForValidLine() {
		String output = lsObj.getLineFromLineNumber(101, filePath);
		assertEquals("", output);
	}

	@Test
	void testGetLineFromLineNumberForInvalidLine() {
		String output = lsObj.getLineFromLineNumber(101, filePath);
		assertEquals("", output);
	}

	@Test
	void testPutDataInCache() {
		lsObj.cache.put(4567l, "Line having some data");
		assertTrue(lsObj.cache.get(4567l).equals("Line having some data"));
	}

}
