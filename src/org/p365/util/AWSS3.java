/**
 * AWS S3 Helper Class
 * @author zqs1886
 */
package org.p365.util;

import java.io.File;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.List;

import org.p365.Constant;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AWSS3 {
	
    private static AmazonS3 s3 = null;

    static {
    	s3 = new AmazonS3Client(
    			new ClasspathPropertiesFileCredentialsProvider());
		Region usEast = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(usEast);
		s3.setEndpoint("http://s3.amazonaws.com");
    }
    
    /**
     * Get URL with key
     * @param key S3
     * @return S3 URL
     */
	public static String getAwsPath(String key) {
		return "http://" + Setting.get("mybucket", Constant.MYBUCKET) + ".s3.amazonaws.com/" + key;
	}
	
	public static void deleteBucket(String bucketname){
		ObjectListing olist = s3.listObjects(bucketname);
		List<S3ObjectSummary> objectSummaries = olist.getObjectSummaries();
		for(S3ObjectSummary s : objectSummaries){
			s3.deleteObject(s.getBucketName(), s.getKey());
		}
//		s3.deleteObject(bucketName, key)
		s3.deleteBucket(bucketname);
	}
	
	public static void CreateBucket(String bucketname){
		if (!s3.doesBucketExist(bucketname)) {
			s3.createBucket(bucketname);
		}
	}
	public static String upLoadToS3(String filePath, String key) {


		try {
//			if (!s3.doesBucketExist(Setting.get("mybucket", Constant.MYBUCKET))) {
//				s3.createBucket(Setting.get("mybucket", Constant.MYBUCKET));
//			}

			// System.out.println("Uploading a new object to S3 from a file\n");
			PutObjectResult pr = s3.putObject(new PutObjectRequest(Setting.get("mybucket", Constant.MYBUCKET),
					key, new File(filePath))
					.withCannedAcl(CannedAccessControlList.PublicRead));
			//return pr.;
			return getAwsPath(key);

		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "error";
	}
	
	/**
	 * set post MetaData
	 * @param fileType
	 * @param ContentLength
	 * @return
	 */
	public static ObjectMetadata getMetadata(String fileType, int ContentLength) {

		String contentType = getContentType(fileType);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(ContentLength);
		objectMetadata.setContentEncoding("utf-8");
		objectMetadata.setContentType(contentType);
		return objectMetadata;
	}
	
	/**
	 * get post content type
	 * @param fileType
	 * @return ContentType
	 */
	public static String getContentType(String fileType) {
		/*
		 * .jpg --------> image/jpeg .jpeg --------> image/jpeg .bmp -------->
		 * application/x-bmp .gif --------> image/gif
		 */
		if (fileType.startsWith("."))
			fileType = fileType.replace(".", "");

		String retval = "";
		if ("jpg".equals(fileType) || "jpeg".equals(fileType)) {
			retval = "image/jpeg";
		} else if ("gif".equals(fileType)) {
			retval = "image/gif";
		} else if ("bmp".equals(fileType)) {
			retval = "application/x-bmp";
		} else {
			retval = "application/octet-stream";
		}
		return retval;

	}
	
    /**
     * Upload file to AWS S3
     * @param The file's AWS S3 key
     * @param fileInputStream
     * @return URL
     *            The file URL in S3
     */
	public static String upLoadToS3(String key, InputStream fileInputStream) throws Exception {
		
		
		
		try {
//			if (!s3.doesBucketExist(Setting.get("mybucket", Constant.MYBUCKET))) {
//				s3.createBucket(Setting.get("mybucket", Constant.MYBUCKET));
//			}
			
			int ContentLength = fileInputStream.available();
			String fileType = "jpg";
			fileType = key.substring(key.lastIndexOf("."));
			ObjectMetadata objectMetadata = getMetadata(fileType, ContentLength);
			PutObjectResult pr = s3.putObject(new PutObjectRequest(Setting.get("mybucket", Constant.MYBUCKET),
					key, fileInputStream, objectMetadata)
					.withCannedAcl(CannedAccessControlList.PublicRead));

			return "http://" + Setting.get("mybucket", Constant.MYBUCKET) + ".s3.amazonaws.com/" + key;

		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return "";
	}
	
	/**
	 * download the file from AWS s3
	 * @param the AWS S3 key
	 * @return FilterInputStream
	 */
	public static FilterInputStream getS3Pic(String key) {
		
		try {
			S3Object object = s3
					.getObject(new GetObjectRequest(Setting.get("mybucket", Constant.MYBUCKET), key));
			return object.getObjectContent();

		} catch (AmazonServiceException ase) {
			System.out.println("Get Object Error");
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(upLoadToS3("F:\\JAVA\\output_1.jpg",
				"2014/std1121list1211221.jpg"));
		//deleteBucket("social365");
		//CreateBucket("social3656");
		
		/*
		 * try { displayTextInputStream(getS3Pic("std121list12")); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

	}

}
