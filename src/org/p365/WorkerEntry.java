package org.p365;
/**
 * The Entry class of worker
 * Create Thread for work and start
 */
import java.util.ArrayList;
import java.util.List;
import org.gearman.worker.GearmanFunction;
import org.p365.signal.SignalHandle;
import org.p365.util.Setting;
import sun.misc.Signal;

public class WorkerEntry extends Thread {
	
	private static List<WorkerEntry> workerList = new ArrayList<WorkerEntry>();
	WorkerMain wMain;
	public WorkerEntry(WorkerMain wMain) {
		this.wMain = wMain;
	}
    
	
	@Override
	public void run() {
		wMain.start();
	}
	
	public void ShutDown() {
		wMain.ShutDownWorker();
	}
	
	public void stops() {
		wMain.stop();
	}

	public static void main(String[] args) {
		
		String hostList = Setting.get("gearman_host_list", Constant.GEAR_HOST_LIST);
		
		List<Class<GearmanFunction>> funs = new ArrayList<Class<GearmanFunction>>();
		Class c;
		try {
			c = Class.forName(Setting.get("gearman_function", Constant.GEARMAN_FUNCTION));
			if (!GearmanFunction.class.isAssignableFrom(c)) {
				System.out.println(Setting.get("gearman_function", Constant.GEARMAN_FUNCTION)
						+ " is not an instance of " + // NOPMD
						GearmanFunction.class.getCanonicalName());
				return;
			}
			funs.add((Class<GearmanFunction>) c);
		} catch (ClassNotFoundException cfne) {
			System.out.println("Can not find function "
					+ Setting.get("gearman_function", Constant.GEARMAN_FUNCTION) + // NOPMD
					" on class path");
			return;
		}
           
		for (int i = 0; i < Setting.getIntFromConfig("gearman_workernum", 1); i++) {
			WorkerMain wMain = new WorkerMain(hostList, funs);
			WorkerEntry wEntry = new WorkerEntry(wMain);
			workerList.add(wEntry);
			wEntry.setName("gearman_workernum--"+i);
			wEntry.start();
		}
		 
		SignalHandle dignal = new SignalHandle(workerList);
		Signal.handle(new Signal("TERM"), dignal);
        Signal.handle(new Signal("USR2"), dignal);
        
	}
}
