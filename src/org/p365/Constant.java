package org.p365;
/**
 * ƒ¨»œ≈‰÷√–≈œ¢
 * @author zqs1886
 *
 */
public interface Constant {                                                    
    /* Defines. */       
    int ORIGNAL_IMAGE = 1;
    int MAKEUP_IMAGE = 2;
    int MAKEUP_PARAMETERS = 3;
    int ORIGNAL_THUMBNAIL1_IMAGE = 11;
    int ORIGNAL_THUMBNAIL2_IMAGE = 12;
    int MAKEUP_THUMBNAIL1_IMAGE = 21;
    int MAKEUP_THUMBNAIL2_IMAGE = 22;
    
    int THUMBNAIL1_WIDTH = 609;
    int THUMBNAIL1_HEIGHT = 0;
    
    int THUMBNAIL2_WIDTH = 0;
    int THUMBNAIL2_HEIGHT = 344;
    
    String NFS_PATH = "/data/topic/";
    String THUMBNAIL_PATH = "/root/";
    String MYBUCKET = "social365";
    
    String GEARMAN_HOST = "172.17.128.250";
    int GEARMAN_PORT = 4730;
    String GEARMAN_FUNCTION = "org.p365.P365Function";
    String GEAR_HOST_LIST = "172.17.128.250:4730";
    
}

