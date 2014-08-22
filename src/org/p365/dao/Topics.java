/**
 * 帖子业务类
 * 数据库逻辑处理类
 */
package org.p365.dao;

import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.p365.cloudsearch.model.Content;
import org.p365.cloudsearch.model.Document;
import org.p365.model.TempUploadFilesBean;
import org.p365.util.DatabaseBean;
import org.p365.util.DatabaseConfigure;

public class Topics {

	DatabaseBean db = new DatabaseBean(DatabaseConfigure.getInstance());
	/**
	 * 判断是否所有图片处理完成
	 * @param guid
	 * @return true 处理完成    false 未处理完成
	 * @author zqs1886
	 */
	public boolean checkComplate(String guid) {
		boolean state = false;
		String sqlString = "SELECT fileCount,fileDoneCount FROM tempUploadInfo WHERE guid='" + guid + "'";
		
		try {
			ResultSet rs = db.excutequery(sqlString);
			while (rs.next()) {
				if(rs.getInt(1) == rs.getInt(2)) {
					state = true;
				}
			}
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return state;
	}
	/**
	 * 更新已处理图片数
	 * @param guid
	 * @return boolean
	 */
	public boolean updateImageNum(String guid) {
		boolean state = false;
		String sqlString = "UPDATE tempUploadInfo SET fileDoneCount=fileDoneCount+1 WHERE guid='" + guid +"'";
		try {
			state = db.execute(sqlString);
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return state;
	}
	
	/**
	 * 插入图片
	 * @param url
	 * @return
	 */
	public boolean insertImage(TempUploadFilesBean bean) {
		boolean state = false;
		String sqlString = "INSERT INTO tempUploadFiles (guid,userID,fileClass,fileSize,filePath,createTime) " + "values (" 
	            + "'"+ bean.getGuid() +"', " 
				+ "'"+ bean.getUserID() +"', " 
				+ "'"+ bean.getFileClass() +"', "
				+ "'"+ bean.getFileSize() +"', " 
				+ "'"+ bean.getFilePath() +"', now()" 
				+		")";
		DatabaseBean db = new DatabaseBean(DatabaseConfigure.getInstance());
		try {
			state = db.execute(sqlString);
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return state;
	}
	/**
	 * 插入图片列表
	 * @param url
	 * @return
	 */
	public boolean insertImages(ArrayList<TempUploadFilesBean>  beans) {
		boolean state = false;
		ArrayList<String> batchSql = new ArrayList<String>();
		for(int i=0; i<beans.size();i++) {
			TempUploadFilesBean bean = beans.get(i);
			String sqlString = "INSERT INTO tempUploadFiles (guid,userID,fileClass,fileSize,filePath,createTime) "
					+ "values ("
					+ "'"
					+ bean.getGuid()
					+ "', "
					+ "'"
					+ bean.getUserID()
					+ "', "
					+ "'"
					+ bean.getFileClass()
					+ "', "
					+ "'"
					+ bean.getFileSize()
					+ "', "
					+ "'"
					+ bean.getFilePath() + "', now()" + ")";
			
			batchSql.add(sqlString);
		}
		try {
			db.executeBatch(batchSql);
			state = true;
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public boolean TopicToLive(String guid) {
		boolean state = false;
		String procString = "{call pr_TempUploadToTopics(?,?)}";
		try {
			db.callExecuteProcedure(procString, guid);
			state = true;
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	
	public Document GetTopics(String guid) {
		Document doc;
		String sqlString = "SELECT topicID,parentID,t.userID,t.email,content,t.createTime," +
				"u.nickName,u.picPath " +
				"FROM topics t " +
				"INNER JOIN userList u on t.userID = u.userID " +
				"WHERE t.guid='" + guid + "'";
		DatabaseBean db = new DatabaseBean(DatabaseConfigure.getInstance());
		try {
			ResultSet result = db.excutequery(sqlString);
			doc = new Document("add", "topics_"+result.getInt("topicID"), 
					new Content(URLEncoder.encode(result.getString("content")), result.getString("email"), URLEncoder.encode(result.getString("nickName")), result.getInt("parentID"), 
							result.getString("picPath"), result.getInt("topicID"), result.getInt("userID"), result.getString("createTime"), 
							1, "", "", "", ""));
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}
	
	
	public static void main(String[] args) {
		
		Topics topics = new Topics();
		String url = "http://zqs.s3.amazon.com/31/2014/12/1.jpg";
		System.out.println(topics.checkComplate("1"));
		System.out.println(topics.checkComplate("2"));
		System.out.println(topics.checkComplate("3"));
		System.out.println(topics.updateImageNum("3"));
		System.out.println(topics.checkComplate("3"));
	}

}
