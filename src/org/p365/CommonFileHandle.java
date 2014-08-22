package org.p365;
/**
 * 普通文件处理类，继承FileHandle类
 * 
 */
import org.p365.model.TempUploadFilesBean;
import org.p365.util.AWSS3;

public class CommonFileHandle extends FileHandle {

	@Override
	public void handle(TempUploadFilesBean bean) {
		
		bean.setFilePath(AWSS3.getAwsPath(bean.getKey()));
		topics.insertImage(bean);
		CheckComplate(bean);
	}

}
