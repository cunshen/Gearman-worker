package org.p365.util;

public class DatabaseConfigure {


    /**静态类*/
    private static DatabaseConfigure instance = null;

    private String databaseName = Setting.get("databaseName");
    private String datebasePassword = Setting.get("datebasePassword");
    private String databaseURL = Setting.get("databaseURL");
    private String jdbcDriver= Setting.get("jdbcDriver");

    /** 数据库连接池名称*/
    private String datapool="java:comp/env/jdbc/hdptdep";
   

    /**
     * 构造函数
     */
    private DatabaseConfigure() {
    }

    /**
     * 单例函数
     *
     * @return 一个实例
     */
    public static DatabaseConfigure getInstance() {
        if (instance == null) {
            instance = new DatabaseConfigure();
        }
        return instance;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatebasePassword() {
        return this.datebasePassword;
    }

    public void setDatebasePassword(String datebasePassword) {
        this.datebasePassword = datebasePassword;
    }

    public String getDatabaseURL() {
        return this.databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getJdbcDriver() {
        return this.jdbcDriver;
    }

    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }
   
    public String getDatapool() {
        return datapool;
    }

    public void setDatapool(String datapool) {
        this.datapool = datapool;
    }
}
