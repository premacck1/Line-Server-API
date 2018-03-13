package com.server;

import static org.junit.Assert.assertTrue;
import java.io.*;
import org.junit.*;


public class LineServiceControllerTest {
	static LineServiceController lsObj = null;
	static Preprocessor preObj = null;
	static String filePath;
	static File f;

	@Before
	public void init() {
		Constants.STR_DB_PATH = "db_test";
		preObj = new Preprocessor();
		filePath = "test_file.txt";
		f = new File(filePath);
		try {
			boolean b = f.createNewFile();
			System.out.println("New Test File Created: " + b);
			FileOutputStream out = new FileOutputStream(f);
			for (int i = 1; i <= 100; i++) {
				out.write(String.valueOf(i +" hey "+ Constants.NEW_LINE_CHAR).getBytes());
			}
			out.close();
			
			preObj.preProcessInputFileMapping(filePath);
			lsObj = new LineServiceController();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void cleanup() {
		boolean b = f.delete();
		System.out.println("Deleting test file: " + b);
		Constants.STR_DB_PATH = "db2";
		DbInstance.closeDB();
		lsObj.cache.clear();
	}

	@Test
	public void testGetLineFromLineNumberForValidLine() {
		String output = lsObj.getLineFromLineNumber(50, filePath);
		assertTrue(output.trim().equals("50 hey"));
	}

	@Test
	public void testGetLineFromLineNumberForInvalidLine() {
		String output = lsObj.getLineFromLineNumber(101, filePath);
		assertTrue(output.equals(""));
	}

	@Test
	public void testPutDataInCache() {
		lsObj.cache.put(4567l, "Line having some data");
		assertTrue(lsObj.cache.get(4567l).equals("Line having some data"));
	}

}
