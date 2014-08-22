package org.p365.dao;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.p365.model.MyLogBean;
import org.p365.util.DatabaseBean;
import org.p365.util.DatabaseConfigure;

public class MyLog {

	

	private static String nowString() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
	/**
	 * 插入日志
	 * @param url
	 * @return
	 */
	public static boolean insertSysLog(MyLogBean bean) {
		boolean st = false;
		String sqlString = "INSERT INTO sysLog(logType,GUID,fileClass,logCode,title,msg,createTime) " + "values (" 
	            + bean.getlogType() +", " 
				+ "'"+ bean.getGUID() +"', " 
				+ bean.getfileClass() +", "
				+ "'"+ bean.getlogCode() +"', " 
				+ "'"+ bean.gettitle() +"', " 
				+ "'"+ bean.getmsg() +"',"
				+ "'"+ nowString() +"' "
				+		")";
		DatabaseBean db = new DatabaseBean(DatabaseConfigure.getInstance());
		try {
			st = db.execute(sqlString);
			db.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.print(sqlString);
		}
		return st;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyLogBean bean = new MyLogBean("guid", 1, 11, "300", "title", "msg");
		insertSysLog(bean);
	}

}
