package com.server;
/*
 * Constants Class for the Project
 */
public class Constants {
	
	public static String STR_DB_PATH = "db2";
	public static final int FAILURE_CODE = 413;
	public static final String STR_BYTE_LINE_MAP_NAME = "byteLineMap";
	public static final String STR_LINE_OUT_OF_FILE_MESSAGE = "Line number requested is beyond the length of text file.";
	public static final String STR_EMPTY_STRING = "";
	public static final Long CLOSE_NUM = -1L;
	public static final int CHUNK_SIZE = 30; // to be configured
	public static final Character NEW_LINE_CHAR = '\n'; // to be configured

	public static long maxLineNumber = Integer.MAX_VALUE;

	public static long getMaxLineNumber() {
		return maxLineNumber;
	}

	public static void setMaxLineNumber(long maxLineNumber) {
		Constants.maxLineNumber = maxLineNumber;
	}

}
