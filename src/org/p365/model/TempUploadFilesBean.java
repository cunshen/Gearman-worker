/**
 * 临时文件库实体类
 * @author zqs1886
 */
package org.p365.model;

public class TempUploadFilesBean {

	private String guid;
	private int userID;
	private int fileClass;
	private String fileSize;
	private String filePath;
	private String createTime;
	private String key;
	
	public TempUploadFilesBean(String guid, int userID, int fileClass,
			String fileSize, String filePath, String key) {
		this.guid = guid;
		this.userID = userID;
		this.fileClass = fileClass;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.key = key;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
	}


	public int getFileClass() {
		return fileClass;
	}


	public void setFileClass(int fileClass) {
		this.fileClass = fileClass;
	}


	public String getFileSize() {
		return fileSize;
	}


	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}


	public String getFilePath() {
		return filePath;
	}


	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	

}
