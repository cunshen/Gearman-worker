package org.p365.model;

public class MyLogBean {
	private String GUID;
	private int logType;
	private int fileClass;
	private String logCode;
	private String title;
	private String msg;
	
	
	public MyLogBean(String gUID, int logType, int fileClass, String logCode,
			String title, String msg) {
		GUID = gUID;
		this.logType = logType;
		this.fileClass = fileClass;
		this.logCode = logCode;
		this.title = title;
		this.msg = msg;
	}
	
	public String getGUID() {
		return GUID;
	}
	public void setGUID(String GUID) {
		this.GUID = GUID;
	}
	public int getlogType() {
		return logType;
	}
	public void setlogType(int logType) {
		this.logType = logType;
	}
	public int getfileClass() {
		return fileClass;
	}
	public void setfileSize(int fileClass) {
		this.fileClass = fileClass;
	}
	public String getlogCode() {
		return logCode;
	}
	public void setlogCode(String logCode) {
		this.logCode = logCode;
	}
	public String gettitle() {
		return title;
	}
	public void settitle(String title) {
		this.title = title;
	}
	public String getmsg() {
		return msg;
	}
	public void setmsg(String msg) {
		this.msg = msg;
	}
	
	
}
