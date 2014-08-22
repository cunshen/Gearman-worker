package org.p365.util;

public class DatabaseConfigure {


    /**��̬��*/
    private static DatabaseConfigure instance = null;

    private String databaseName = Setting.get("databaseName");
    private String datebasePassword = Setting.get("datebasePassword");
    private String databaseURL = Setting.get("databaseURL");
    private String jdbcDriver= Setting.get("jdbcDriver");

    /** ���ݿ����ӳ�����*/
    private String datapool="java:comp/env/jdbc/hdptdep";
   

    /**
     * ���캯��
     */
    private DatabaseConfigure() {
    }

    /**
     * ��������
     *
     * @return һ��ʵ��
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
