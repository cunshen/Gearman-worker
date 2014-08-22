package org.p365;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.gearman.common.GearmanNIOJobServerConnection;
import org.gearman.worker.GearmanFunction;
import org.gearman.worker.GearmanWorker;
import org.gearman.worker.GearmanWorkerImpl;

public class WorkerMain {
	
	GearmanNIOJobServerConnection conn;
	List<GearmanNIOJobServerConnection> conns;
    List<Class<GearmanFunction>> functions;
    private GearmanWorker worker = null;
    
    @SuppressWarnings(value = "unchecked")
    public WorkerMain(String host, int port,
            List<Class<GearmanFunction>> funs) {
        conn = new GearmanNIOJobServerConnection(host, port);
        functions = new ArrayList<Class<GearmanFunction>>();
        functions.addAll(funs);
    }
    
    /**
     * 支持多job server worker 初始化
     * @param hostList
     * @param funs
     */
    @SuppressWarnings(value = "unchecked")
    public WorkerMain(String hostList, 
            List<Class<GearmanFunction>> funs) {
    	
    	conns = new ArrayList<GearmanNIOJobServerConnection>();
    	String[] hosts = hostList.split(",");
    	for(int i=0; i<hosts.length; i++) {
    		String[] parasString =  hosts[i].split(":");
    		conns.add(new GearmanNIOJobServerConnection(parasString[0], Integer.parseInt(parasString[1])));
    	}
    	
        functions = new ArrayList<Class<GearmanFunction>>();
        functions.addAll(funs);
        worker = new GearmanWorkerImpl();
    }
    
    public void ShutDownWorker(){
    	worker.shutdown();
    }
    
    public void stop(){
    	worker.stop();
    }
    
    public void start() {
    	
        //GearmanWorker worker = new GearmanWorkerImpl();
        for (int i = 0; conns!=null && i < conns.size(); i++) {
        	worker.addServer(conns.get(i));
		}
        for (Class<GearmanFunction> fun : functions) {
            worker.registerFunction(fun);
        }
        
        if(conn!=null){
        	worker.addServer(conn);
        }
        worker.work();
        
    }

    @SuppressWarnings(value = "unchecked")
    public static void main(String[] args) {
        List<Class<GearmanFunction>> functions =
                new ArrayList<Class<GearmanFunction>>();
        
        String host = Constant.GEARMAN_HOST;
        int port = Constant.GEARMAN_PORT;
        
		Class c;
		try {
			c = Class.forName(Constant.GEARMAN_FUNCTION);
			if (!GearmanFunction.class.isAssignableFrom(c)) {
				System.out.println(Constant.GEARMAN_FUNCTION
						+ " is not an instance of " + // NOPMD
						GearmanFunction.class.getCanonicalName());
				return;
			}
			functions.add((Class<GearmanFunction>) c);
		} catch (ClassNotFoundException cfne) {
			System.out.println("Can not find function "
					+ Constant.GEARMAN_FUNCTION + // NOPMD
					" on class path");
			return;
		}
            
        
        System.out.println("host="+host+" port = "+port+" function ="+functions);
        new WorkerMain(host, port,functions).start();
    }
    
}
