package org.p365;

/*
 * this Function extends from  AbstractGearmanFunction
 * The really handle message function 
 * step 1: Scale Image
 * step 2: upload to s3
 * step 3: write DB
 * @author by zqs
 */

import org.gearman.client.GearmanJobResult;
import org.gearman.client.GearmanJobResultImpl;
import org.gearman.util.ByteUtils;
import org.gearman.worker.AbstractGearmanFunction;
import org.p365.dao.MyLog;
import org.p365.model.MyLogBean;
import org.p365.model.TempUploadFilesBean;
import org.p365.util.Setting;

public class P365Function extends AbstractGearmanFunction {

	public GearmanJobResult executeFunction() {
		
		String messageString = ByteUtils.fromUTF8Bytes((byte[]) this.data);
		//messageString = "e22ba69c-96f6-466c-8cf0-72d3a904f2af,/topic/2014/05/07/15/e22ba69c-96f6-466c-8cf0-72d3a904f2af/p04295763.jpg,1,15";
		String[] arrayStrings = messageString.split(",");
		TempUploadFilesBean fileBean = null; 
		System.out.println("**************************************************");
		System.out.println("*******************wokr:"+messageString+" start!");
		System.out.println("**************************************************");
		
		try {
			//log 开始接收任务
			MyLogBean log = new MyLogBean(arrayStrings[0], 0, Integer.parseInt(arrayStrings[2]), "300", "开始接收任务", 
					"接收到消息:文件大小:" + arrayStrings[4] + "s3 key:" + arrayStrings[1]);
			MyLog.insertSysLog(log);
			fileBean = new TempUploadFilesBean(arrayStrings[0], Integer.parseInt(arrayStrings[3]), 
					Integer.parseInt(arrayStrings[2]), arrayStrings[4], Setting.get("nfs_path", Constant.NFS_PATH) + arrayStrings[1], arrayStrings[1]); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		FileHandle filehanle = null;
		//==3 普通文件
		if(3 == fileBean.getFileClass()){
			filehanle = new CommonFileHandle();
		}
		//图片文件
		else {
			filehanle = new ImageHandle();
		}
		
		filehanle.handle(fileBean);
		
		System.out.println("**************************************************");
		System.out.println("*******************wokr:"+messageString+" finish!");
		System.out.println("**************************************************");
		
		
		GearmanJobResult gjr = new GearmanJobResultImpl(this.jobHandle, true,
				"ok".getBytes(), new byte[0], new byte[0],
				0, 0);
		return gjr;
		//return null;
	}

}
