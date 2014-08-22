package org.p365.signal;

import java.util.ArrayList;
import java.util.List;

import org.p365.WorkerEntry;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SignalHandle implements SignalHandler {
	
    private static List<WorkerEntry> workerList = new ArrayList<WorkerEntry>();
	 
	@Override
	public void handle(Signal arg0) {
		stopThread();
	}
	
	public SignalHandle(List<WorkerEntry> workerList) {
		this.workerList = workerList;
	}
	
	/**
	 * 收到退出信号后处理
	 */
	public void stopThread() {
		for(int i=0; i<workerList.size(); i++) {
			WorkerEntry wentry = workerList.get(i);
			wentry.stops();
			
			/*while(wentry.isAlive()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			
			
		}
		//System.exit(1);
	}
	

}
