package org.p365;
/**
 * ͼƬ��Ϣ�����࣬�̳�fileHandle��
 * �ṩͼƬ������������ͼ���ϴ� AWS S3,д�����ݿ�
 */
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.p365.dao.MyLog;
import org.p365.imagescale.GMScale;
import org.p365.model.MyLogBean;
import org.p365.model.TempUploadFilesBean;
import org.p365.util.AWSS3;
import org.p365.util.Setting;


public class ImageHandle extends FileHandle {

	private ArrayList<TempUploadFilesBean> fileList = new ArrayList<TempUploadFilesBean>();    //����ļ��б�һ��������ݿ�
	
	@Override
	public void handle(TempUploadFilesBean bean) {
		String pathstring = bean.getFilePath();
		String key = bean.getKey();
		String extName = key.substring(key.lastIndexOf(".") + 1);               //ͼƬ����
		int FileClass = bean.getFileClass();
		String ImageSamll1Key = key.substring(0, key.lastIndexOf("."))
				+ "_small1." + extName;
		String image_samll1 = Setting.get("thumbnail_path", Constant.THUMBNAIL_PATH) + ImageSamll1Key;
		String ImageSamll2Key = key.substring(0, key.lastIndexOf("."))
				+ "_small2." + extName;
		String image_samll2 = Setting.get("thumbnail_path", Constant.THUMBNAIL_PATH) + ImageSamll2Key;
		

		// ԭͼ����
		
		//log ��ʼ��S3��ȡͼƬ
		MyLogBean log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "310", "��ʼ��S3��ȡͼƬ", 
				"��ʼ��S3��ȡͼƬ:�ļ���С:" + bean.getFileSize() + "s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		InputStream stream = null;                   //ͼƬ��
		byte[] fliebyte = null;                      //�ֽ����� ���ԭͼ
		try{
	    stream = AWSS3.getS3Pic(key);                //��ȡԭͼ��
	    fliebyte = input2byte(stream);               //����ԭͼ
	    stream.close();
		}catch(Exception e){
			System.out.println("��ȡͼƬ1ʧ�ܣ�guid-" + bean.getGuid() + "key:" + key); 
		}
		
		//log ���ͼƬ��ȡ
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "320", "���ͼƬ��ȡ", 
						"���ͼƬ��ȡ:�ļ���С:" + bean.getFileSize() + "s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		bean.setFilePath(AWSS3.getAwsPath(key));    //����ͼƬ·��
		fileList.add(bean);                         //ԭͼ�����б� 
		
		// ����ͼ1
		TempUploadFilesBean thumb1Bean = new TempUploadFilesBean(
				bean.getGuid(), bean.getUserID(), 0, "0", "", ImageSamll1Key);
		
		//log ��ʼThumb1 ResizeͼƬ
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "330", "��ʼThumb1 ResizeͼƬ", 
						"��ʼThumb1 ResizeͼƬ:s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		stream = new ByteArrayInputStream(fliebyte); //��ȡԭͼinputstream
		
		//����Thumbnail1
		InputStream thum1Stream = resizeImage(stream, Setting.getIntFromConfig("thumbnail1_width", Constant.THUMBNAIL1_WIDTH),
				Setting.getIntFromConfig("thumbnail1_height", Constant.THUMBNAIL1_HEIGHT), extName);
//		InputStream thum1Stream = GMScale.ScaleImage(stream, Setting.getIntFromConfig("thumbnail1_width", Constant.THUMBNAIL1_WIDTH), 
//				Setting.getIntFromConfig("thumbnail1_height", Constant.THUMBNAIL1_HEIGHT));
		
		//log ���Thumb1 Resize
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "340", "���Thumb1 Resize", 
						"���Thumb1 Resize :s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb1Bean.setFileSize(getFileLength(thum1Stream)); // ��ȡthumb1�ļ���С
		
		
		//log Thumb1 ��ʼ�ϴ�S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "350", "Thumb1 ��ʼ�ϴ�S3", 
						"Thumb1 ��ʼ�ϴ�S3 :s3 key:" + thumb1Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		try{
		image_samll1 = AWSS3.upLoadToS3(thumb1Bean.getKey(), thum1Stream);
		stream.close();
		thum1Stream.close();
		}catch(Exception e){
			System.out.println("ͼƬ�ϴ�1ʧ�ܣ�guid-" + bean.getGuid() + "key:" + thumb1Bean.getKey()); 
		}
		
		
		//log Thumb1 ����ϴ�S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "360", "Thumb1 ����ϴ�S3", 
						"Thumb1 ����ϴ�S3 :s3 key:" + thumb1Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb1Bean.setFilePath(image_samll1);
		
		if (FileClass == Constant.ORIGNAL_IMAGE) {
			thumb1Bean.setFileClass(Constant.ORIGNAL_THUMBNAIL1_IMAGE);
		} else if (FileClass == Constant.MAKEUP_IMAGE) {
			thumb1Bean.setFileClass(Constant.MAKEUP_THUMBNAIL1_IMAGE);
		}
		fileList.add(thumb1Bean);
		
		// ����ͼ2
		TempUploadFilesBean thumb2Bean = new TempUploadFilesBean(
				bean.getGuid(), bean.getUserID(), 0, "0", "", ImageSamll2Key);
		//stream = AWSS3.getS3Pic(key);    //��ȡԭͼ��
		stream = new ByteArrayInputStream(fliebyte);       //��ȡԭͼinputstream       
		
		
		//log Thumb2 ��ʼResizeͼƬ
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "330", "Thumb2 ��ʼResizeͼƬ", 
						"Thumb2 ��ʼResizeͼƬ :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		InputStream thum2Stream = resizeImage(stream, Setting.getIntFromConfig("thumbnail2_width", Constant.THUMBNAIL2_WIDTH),
				Setting.getIntFromConfig("thumbnail2_height", Constant.THUMBNAIL2_HEIGHT), extName);
//		InputStream thum2Stream =GMScale.ScaleImage(stream, Setting.getIntFromConfig("thumbnail2_width", Constant.THUMBNAIL2_WIDTH),
//				Setting.getIntFromConfig("thumbnail2_height", Constant.THUMBNAIL2_HEIGHT));
		
		
		//log ���Thumb2 Resize
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "340", "���Thumb2 Resize", 
						"���Thumb2 ResizeͼƬ :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb2Bean.setFileSize(getFileLength(thum2Stream)); // ��ȡ�ļ���С
		
		
		//log Thumb2��ʼ�ϴ�S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "350", "Thumb2 ��ʼ�ϴ�S3", 
						"Thumb2 ��ʼ�ϴ�S3 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		try{
		image_samll2 = AWSS3.upLoadToS3(thumb2Bean.getKey(), thum2Stream);
		thum2Stream.close();
		}catch(Exception e){
			System.out.println("ͼƬ�ϴ�2ʧ�ܣ�guid-" + bean.getGuid() + "key:" + thumb2Bean.getKey()); 
		}
		
		
		//log Thumb2����ϴ�S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "360", "Thumb2 ����ϴ�S3", 
						"Thumb2 ����ϴ�S3 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb2Bean.setFilePath(image_samll2);
		
		if (FileClass == Constant.ORIGNAL_IMAGE) {
			thumb2Bean.setFileClass(Constant.ORIGNAL_THUMBNAIL2_IMAGE);
		} else if (FileClass == Constant.MAKEUP_IMAGE) {
			thumb2Bean.setFileClass(Constant.MAKEUP_THUMBNAIL2_IMAGE);
		}
		fileList.add(thumb2Bean);
		
		//�ر�stream
		try {
			if (null != stream) {
				stream.close();
			}
			if (null != thum1Stream) {
				thum1Stream.close();
			}
			if (null != thum2Stream) {
				thum2Stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//log ��ʼ����DB
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "370", "��ʼ����DB", 
						"��ʼ����DB :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		// д�����ݿ�
		topics.insertImages(fileList);
		// ����Ƿ����
		CheckComplate(bean);
		
		
		//log ���DBд��
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "380", "���DBд��", 
						"���DBд�� :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
	}
	
	/**
	 * Resize Image 
	 * @param imagepath
	 *                 the old image stream to resize
	 * @param width
	 *                 the new image width
	 * @param height
	 *                 the new image height
	 * @param extName
	 *                 the image Extension
	 *                 
	 * @return         new new image steam
	 * 
	 * @throws         IOException
	 */
	private InputStream resizeImage(InputStream imagepath, int width, int height, String extName) {
		ImageScale is = new ImageScale();
		try {
			BufferedImage image1 = ImageIO.read(imagepath);

			BufferedImage image2 = is.imageZoomOut(image1, width, height, true);
			image1.flush();
			return getImageStream(image2, extName);
	
		} catch (IOException ioe) {
			// TODO: handle exception
			ioe.printStackTrace();
			System.out.println(ioe.getMessage()); 
		}
		
		return null;
	}
	
	/**
	 * inputstream ת��  Byte����
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        swapStream.close();
        return in2b;  
    }
	
	/**
	 * BufferedImage into InputStream
	 * @param bi
	 * @param extName
	 * @return
	 */
	private InputStream getImageStream(BufferedImage bi, String extName){ 
        
		InputStream is = null;  

        ByteArrayOutputStream bs = new ByteArrayOutputStream();  
         
        ImageOutputStream imOut; 
        try { 
            imOut = ImageIO.createImageOutputStream(bs); 
            bi.flush(); 
            if("jpg".equals(extName)) {
            	extName = "jpeg";
            }
            ImageIO.write(bi, extName,imOut); 
             
            is= new ByteArrayInputStream(bs.toByteArray()); 
            
             
        } catch (IOException e) { 
            e.printStackTrace(); 
        }  
        return is; 
    } 
    
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
