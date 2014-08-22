package org.p365;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.gearman.client.GearmanClient;
import org.gearman.client.GearmanClientImpl;
import org.gearman.client.GearmanJob;
import org.gearman.client.GearmanJobImpl;
import org.gearman.common.GearmanNIOJobServerConnection;
import org.gearman.util.ByteUtils;
import org.p365.util.AWSS3;

public class P365Client {

	private GearmanClient client;
	
	public static P365Client p365client;
	
	public static P365Client getInsatnce(String gearman_host_list){
		if(null == p365client){
			synchronized(P365Client.class){
				P365Client temp = p365client;
	            if(null == temp) {
	               temp = new P365Client(gearman_host_list);
	               p365client = temp;
	            }
			}
		}
		return p365client;
	}
    
    public P365Client(String hostList) {
    	
    	client = new GearmanClientImpl();
		try {
			String[] hosts = hostList.split(",");
			for (int i = 0; i < hosts.length; i++) {
				String[] parasString = hosts[i].split(":");
				client.addJobServer(new GearmanNIOJobServerConnection(
						parasString[0], Integer.parseInt(parasString[1])));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    public void SubmitJob(String input, String function) {
        //String function = P365Function.class.getCanonicalName();
        String uniqueId = null;
        byte[] data = ByteUtils.toUTF8Bytes(input);
        GearmanJob job = GearmanJobImpl.createBackgroundJob(function, data, uniqueId);
        client.submit(job);

    }

    public void shutdown() throws IllegalStateException {
        if (client == null) {
            throw new IllegalStateException("No client to shutdown");
        }
        client.shutdown();
    }

    public static void main(String[] args) {
    	
        String function = "org.p365.P365Function";                                 //执行函数
        String  gearman_host_list="172.17.128.250:4730";         //MQ 服务器列表，多个使用 , 隔开
        P365Client rc = P365Client.getInsatnce(gearman_host_list);
        for(int i=0; i<1000; i++) {
        	UUID uuid = UUID.randomUUID();
        	String guid = uuid.toString();
        	String imageName = guid + "-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-1.jpg";
        	AWSS3.upLoadToS3("d:\\java\\DSC09820.JPG", imageName);
            String paraString = "d776af55-173f-41d4-a959-a7f122042252," + imageName + ",1,15,12"; //参数内容
            rc.SubmitJob(paraString, function);                                           //提交任务
        }
        rc.shutdown();
        
    }

}
