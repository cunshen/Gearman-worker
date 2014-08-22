package org.p365.aws;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudsearchv2.AmazonCloudSearchClient;
import com.amazonaws.services.cloudsearchv2.model.CreateDomainRequest;

public class AWSCloudSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static AmazonCloudSearchClient cloudSearchClient = null;
	
	static {
		cloudSearchClient = new AmazonCloudSearchClient(new ClasspathPropertiesFileCredentialsProvider());
		Region usWest2 = Region.getRegion(Regions.US_WEST_1);
		cloudSearchClient.setRegion(usWest2);
		//aClient.setEndpoint("http://s3.amazonaws.com");
	}
	public static void uploadDocument() {
		CreateDomainRequest request = new CreateDomainRequest();
		
		//cloudSearchClient.createDomain(request.withDomainName("ss"));
	}

}
