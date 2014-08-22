/**
 * �ļ�������� abstract class
 * �ṩ��
 * 1.handle �����ļ�������ڷ���
 * 2.getFileLength ��ȡ�ļ���С����
 * 3.CheckComplate ��������Ƿ����
 */
package org.p365;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.p365.cloudsearch.CloudSearchHelper;
import org.p365.cloudsearch.model.Document;
import org.p365.dao.Topics;
import org.p365.model.TempUploadFilesBean;

import com.google.gson.Gson;

public abstract class FileHandle {
    protected Topics topics = new Topics();
    
	public abstract void handle(TempUploadFilesBean bean);
	
	public void CheckComplate(TempUploadFilesBean bean) {
		topics.updateImageNum(bean.getGuid());
        if(topics.checkComplate(bean.getGuid())) {
        	topics.TopicToLive(bean.getGuid());
        	List<Document> list = new LinkedList<Document>();
        	Document doc = topics.GetTopics(bean.getGuid());
        	list.add(doc);
        	Gson gson = new Gson();
        	String jsonString = gson.toJson(list);
        	try {
        		if(CloudSearchHelper.updateIndex(jsonString)==null){
        			
        		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
	}
	
	protected String getFileLength(String filePath) {
		try {
			File file = new File(filePath);
			if(file != null) {
				return Long.toString(file.length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	protected String getFileLength(InputStream filestream) {
		try {
			
			if(filestream != null) {
				return String.valueOf(filestream.available());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}
}
