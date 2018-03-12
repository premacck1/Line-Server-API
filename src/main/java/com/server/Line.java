package com.server;
/*
 * Line Object class
 */
public class Line {
	long lineNumber;
	String lineText;

	public Line(long lNum, String lText) {
		this.lineNumber = lNum;
		this.lineText = lText;
	}
	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLineText() {
		return lineText;
	}

	public void setLineText(String lineText) {
		this.lineText = lineText;
	}

}
