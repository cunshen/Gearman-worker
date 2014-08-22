package org.p365;
/**
 * 图片信息处理类，继承fileHandle类
 * 提供图片处理：生成缩略图，上传 AWS S3,写入数据库
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

	private ArrayList<TempUploadFilesBean> fileList = new ArrayList<TempUploadFilesBean>();    //存放文件列表，一起插入数据库
	
	@Override
	public void handle(TempUploadFilesBean bean) {
		String pathstring = bean.getFilePath();
		String key = bean.getKey();
		String extName = key.substring(key.lastIndexOf(".") + 1);               //图片类型
		int FileClass = bean.getFileClass();
		String ImageSamll1Key = key.substring(0, key.lastIndexOf("."))
				+ "_small1." + extName;
		String image_samll1 = Setting.get("thumbnail_path", Constant.THUMBNAIL_PATH) + ImageSamll1Key;
		String ImageSamll2Key = key.substring(0, key.lastIndexOf("."))
				+ "_small2." + extName;
		String image_samll2 = Setting.get("thumbnail_path", Constant.THUMBNAIL_PATH) + ImageSamll2Key;
		

		// 原图处理
		
		//log 开始从S3获取图片
		MyLogBean log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "310", "开始从S3获取图片", 
				"开始从S3获取图片:文件大小:" + bean.getFileSize() + "s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		InputStream stream = null;                   //图片流
		byte[] fliebyte = null;                      //字节数组 存放原图
		try{
	    stream = AWSS3.getS3Pic(key);                //获取原图流
	    fliebyte = input2byte(stream);               //缓存原图
	    stream.close();
		}catch(Exception e){
			System.out.println("获取图片1失败：guid-" + bean.getGuid() + "key:" + key); 
		}
		
		//log 完成图片获取
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "320", "完成图片获取", 
						"完成图片获取:文件大小:" + bean.getFileSize() + "s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		bean.setFilePath(AWSS3.getAwsPath(key));    //设置图片路径
		fileList.add(bean);                         //原图放入列表 
		
		// 缩略图1
		TempUploadFilesBean thumb1Bean = new TempUploadFilesBean(
				bean.getGuid(), bean.getUserID(), 0, "0", "", ImageSamll1Key);
		
		//log 开始Thumb1 Resize图片
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "330", "开始Thumb1 Resize图片", 
						"开始Thumb1 Resize图片:s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		stream = new ByteArrayInputStream(fliebyte); //获取原图inputstream
		
		//生成Thumbnail1
		InputStream thum1Stream = resizeImage(stream, Setting.getIntFromConfig("thumbnail1_width", Constant.THUMBNAIL1_WIDTH),
				Setting.getIntFromConfig("thumbnail1_height", Constant.THUMBNAIL1_HEIGHT), extName);
//		InputStream thum1Stream = GMScale.ScaleImage(stream, Setting.getIntFromConfig("thumbnail1_width", Constant.THUMBNAIL1_WIDTH), 
//				Setting.getIntFromConfig("thumbnail1_height", Constant.THUMBNAIL1_HEIGHT));
		
		//log 完成Thumb1 Resize
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "340", "完成Thumb1 Resize", 
						"完成Thumb1 Resize :s3 key:" + bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb1Bean.setFileSize(getFileLength(thum1Stream)); // 获取thumb1文件大小
		
		
		//log Thumb1 开始上传S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "350", "Thumb1 开始上传S3", 
						"Thumb1 开始上传S3 :s3 key:" + thumb1Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		try{
		image_samll1 = AWSS3.upLoadToS3(thumb1Bean.getKey(), thum1Stream);
		stream.close();
		thum1Stream.close();
		}catch(Exception e){
			System.out.println("图片上传1失败：guid-" + bean.getGuid() + "key:" + thumb1Bean.getKey()); 
		}
		
		
		//log Thumb1 完成上传S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "360", "Thumb1 完成上传S3", 
						"Thumb1 完成上传S3 :s3 key:" + thumb1Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb1Bean.setFilePath(image_samll1);
		
		if (FileClass == Constant.ORIGNAL_IMAGE) {
			thumb1Bean.setFileClass(Constant.ORIGNAL_THUMBNAIL1_IMAGE);
		} else if (FileClass == Constant.MAKEUP_IMAGE) {
			thumb1Bean.setFileClass(Constant.MAKEUP_THUMBNAIL1_IMAGE);
		}
		fileList.add(thumb1Bean);
		
		// 缩略图2
		TempUploadFilesBean thumb2Bean = new TempUploadFilesBean(
				bean.getGuid(), bean.getUserID(), 0, "0", "", ImageSamll2Key);
		//stream = AWSS3.getS3Pic(key);    //获取原图流
		stream = new ByteArrayInputStream(fliebyte);       //获取原图inputstream       
		
		
		//log Thumb2 开始Resize图片
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "330", "Thumb2 开始Resize图片", 
						"Thumb2 开始Resize图片 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		InputStream thum2Stream = resizeImage(stream, Setting.getIntFromConfig("thumbnail2_width", Constant.THUMBNAIL2_WIDTH),
				Setting.getIntFromConfig("thumbnail2_height", Constant.THUMBNAIL2_HEIGHT), extName);
//		InputStream thum2Stream =GMScale.ScaleImage(stream, Setting.getIntFromConfig("thumbnail2_width", Constant.THUMBNAIL2_WIDTH),
//				Setting.getIntFromConfig("thumbnail2_height", Constant.THUMBNAIL2_HEIGHT));
		
		
		//log 完成Thumb2 Resize
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "340", "完成Thumb2 Resize", 
						"完成Thumb2 Resize图片 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb2Bean.setFileSize(getFileLength(thum2Stream)); // 获取文件大小
		
		
		//log Thumb2开始上传S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "350", "Thumb2 开始上传S3", 
						"Thumb2 开始上传S3 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		try{
		image_samll2 = AWSS3.upLoadToS3(thumb2Bean.getKey(), thum2Stream);
		thum2Stream.close();
		}catch(Exception e){
			System.out.println("图片上传2失败：guid-" + bean.getGuid() + "key:" + thumb2Bean.getKey()); 
		}
		
		
		//log Thumb2完成上传S3
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "360", "Thumb2 完成上传S3", 
						"Thumb2 完成上传S3 :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		thumb2Bean.setFilePath(image_samll2);
		
		if (FileClass == Constant.ORIGNAL_IMAGE) {
			thumb2Bean.setFileClass(Constant.ORIGNAL_THUMBNAIL2_IMAGE);
		} else if (FileClass == Constant.MAKEUP_IMAGE) {
			thumb2Bean.setFileClass(Constant.MAKEUP_THUMBNAIL2_IMAGE);
		}
		fileList.add(thumb2Bean);
		
		//关闭stream
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
		
		
		//log 开始调用DB
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "370", "开始调用DB", 
						"开始调用DB :s3 key:" + thumb2Bean.getKey());
		MyLog.insertSysLog(log);
		
		
		// 写入数据库
		topics.insertImages(fileList);
		// 检查是否完成
		CheckComplate(bean);
		
		
		//log 完成DB写入
	    log = new MyLogBean(bean.getGuid(), 0, bean.getFileClass(), "380", "完成DB写入", 
						"完成DB写入 :s3 key:" + thumb2Bean.getKey());
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
	 * inputstream 转成  Byte数组
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
