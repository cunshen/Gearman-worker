package org.p365;
/**
 * ��ͨ�ļ������࣬�̳�FileHandle��
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
