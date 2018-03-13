package com.server;

import static org.junit.Assert.assertTrue;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.*;
/*
 * DB and Input File is mocked for testing. So this class is testing both Pre-processing and LineServiceController.
 */

public class LineServiceControllerTest {
	static LineServiceController lsObj = null;
	static Preprocessor preObj = null;
	static String filePath;
	static File f;

	@BeforeClass
	public static void init() {
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

	@AfterClass
	public static void cleanup() {
		boolean b = f.delete();
		System.out.println("Deleting test file: " + b);
		lsObj.cache.clear();
		Constants.STR_DB_PATH = "db2";
		DbInstance.closeDB();
		try {
			Files.deleteIfExists(Paths.get("db_test"));
		} catch (IOException e) {
			System.out.println("Failed to delete db_test file. Please remove manually.");
		}
	}

	@Test
	public void testGetLineFromLineNumberForValidLine1() {
		String output = lsObj.getLineFromLineNumber(50, filePath);
		assertTrue(output.trim().equals("50 hey"));
	}
	
	@Test
	public void testGetLineFromLineNumberForValidLine2() {
		String output = lsObj.getLineFromLineNumber(19, filePath);
		assertTrue(output.trim().equals("19 hey"));
	}
	
	@Test
	public void testGetLineFromLineNumberForValidLine3() {
		String output = lsObj.getLineFromLineNumber(77, filePath);
		assertTrue(output.trim().equals("77 hey"));
	}
	
	@Test
	public void testGetLineFromLineNumberForValidLine4() {
		String output = lsObj.getLineFromLineNumber(100, filePath);
		assertTrue(output.trim().equals("100 hey"));
	}
	
	@Test
	public void testGetLineFromLineNumberForValidLine5() {
		String output = lsObj.getLineFromLineNumber(1, filePath);
		assertTrue(output.trim().equals("1 hey"));
	}
	
	@Test
	public void testGetLineFromLineNumberForValidLine6() {
		String output = lsObj.getLineFromLineNumber(50, filePath);
		assertTrue(output.trim().equals("50 hey"));
	}

	@Test
	public void testGetLineFromLineNumberForInvalidLine1() {
		String output = lsObj.getLineFromLineNumber(101, filePath);
		assertTrue(output.equals(""));
	}
	
	@Test
	public void testGetLineFromLineNumberForInvalidLine2() {
		String output = lsObj.getLineFromLineNumber(0, filePath);
		assertTrue(output.equals(""));
	}
	
	@Test
	public void testGetLineFromLineNumberForInvalidLine3() {
		String output = lsObj.getLineFromLineNumber(-2, filePath);
		assertTrue(output.equals(""));
	}

	@Test
	public void testPutDataInCache() {
		lsObj.cache.put(4567l, "Line having some data");
		assertTrue(lsObj.cache.get(4567l).equals("Line having some data"));
	}

}
