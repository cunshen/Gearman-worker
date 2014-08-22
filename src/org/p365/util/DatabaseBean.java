package org.p365.util;


import java.util.ArrayList;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;

import aj.org.objectweb.asm.Type;

public class DatabaseBean {

    private DatabaseConfigure databaseConfigure = null;

    public Connection conn = null;

    public Statement stmt = null;

    private CallableStatement cstmt = null;

    public DatabaseBean(DatabaseConfigure databaseConfigure) {
        this.databaseConfigure = databaseConfigure;
    }

    /**
     * ��ȡ��ǰ����
     *
     * @return ���ص�ǰ����connection
     */
    public Connection getConnection(){
        return this.conn;
    }
   
    /**
     * ֱ���������ݿ⣬Ч�ʵ�
     *
     * @throws Exception ����ʧ���쳣
     */
    public void getDirectConnect()throws Exception{
       
        if(this.conn==null){
            try {
                Class.forName(databaseConfigure.getJdbcDriver());
            } catch (ClassNotFoundException e) {
                System.out.println(" class not found: " + e.getMessage());
                e.printStackTrace();
                throw new SQLException("-Database driver notFind ");
            }
   
            try {
                conn = DriverManager.getConnection(databaseConfigure
                        .getDatabaseURL(), databaseConfigure
                        .getDatabaseName(), databaseConfigure
                        .getDatebasePassword());
            } catch (SQLException sqlex) {
                System.err.println("DatabaseBean connection error"
                        + sqlex.getMessage());
                sqlex.printStackTrace();
                throw new SQLException("-Database connection error ");
            }
        }
    }
   
    /**
     * ͨ�����ݿ����ӳ����ӣ�Ч�ʸ�
     * ��Ҫ�������ݿ����ӳ�
     * @throws Exception ����ʧ���쳣
     */
    public void getConnectFromdataPool()throws Exception{
        if (this.conn == null) {
            Context initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup(this.databaseConfigure.getDatapool());
            if (ds != null)
                conn = ds.getConnection();
        }
    }
   
    /**
     * �˺����Ժ󲻽���ʹ�ã�������ʷ�������Ᵽ��
     * @throws Exception
     */
    public void connection() throws Exception {

        if (this.conn == null) {
/*
            // ��ͨ�����ݿ����ӳ�����
            if (this.databaseConfigure.getDatapool() != null) {
                Context initCtx = new InitialContext();

                DataSource ds = (DataSource) initCtx.lookup(this.databaseConfigure.getDatapool());
                if (ds != null)
                    conn = ds.getConnection();
            } else {
*/       
                //���û�����ݿ����ӳأ���ֱ������
                try {
                    Class.forName(databaseConfigure.getJdbcDriver());
                } catch (ClassNotFoundException e) {
                    System.out.println(" class not found: " + e.getMessage());
                    e.printStackTrace();
                    throw new SQLException("-Database driver notFind ");
                }

                try {
                    conn = DriverManager.getConnection(databaseConfigure
                            .getDatabaseURL(), databaseConfigure
                            .getDatabaseName(), databaseConfigure
                            .getDatebasePassword());
                } catch (SQLException sqlex) {
                    System.err.println("DatabaseBean connection error"
                            + sqlex.getMessage());
                    sqlex.printStackTrace();
                    throw new SQLException("-Database connection error ");
                }
            //}
        }
    }

    public void disconnect() throws SQLException {
        try {
            if (stmt != null) {
                stmt.close();
                //ע�����close�󣬸ñ�����Ϊnull���������stmt!=nullΪ����������������
                stmt=null;
            }

            if (conn != null) {
                conn.close();
                //ע�����close�������ݿ�����ӷ����������ݿ����ӳأ����ñ�����Ϊnull��
                //��������ж�conn!=nullΪ����������������
                conn=null;
            }
        } catch (SQLException e) {
            System.err.println("DatabaseBean disconnect error");
            e.printStackTrace();
            throw new SQLException("-Database disConnection error ");
        }
    }

    public boolean isConnected() {
        return (conn != null);
    }

    public ResultSet excutequery(String sql) throws SQLException {
        ResultSet rs1 = null;
        try {
            if (!isConnected())
                this.connection();
            if (this.stmt == null)
                stmt = conn.createStatement();
            rs1 = stmt.executeQuery(sql);
            return rs1;
        } catch (Exception e) {
            System.err.println("DatabaseBean query error" + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-Query error ");
        }
    }

    public boolean execute(String sql) throws SQLException {
        try {
            if (!isConnected())
                this.connection();
            if (this.stmt == null)
                stmt = conn.createStatement();
            return stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("DatabaseBean execute error" + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-Execute error ");
        }
    }

    public void statementClose() {
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet callQueryProcedure(String sql) throws SQLException {
        ResultSet rs = null;
        try {
            if (!isConnected())
                this.connection();
            if (this.cstmt == null)
                cstmt = conn.prepareCall(sql);
            cstmt.executeQuery();
        } catch (Exception e) {
            System.err.println("DatabaseBean callQueryProcedure error"
                    + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-CallQueryProcedure error ");
        }
        return rs;
    }

    public boolean callExecuteProcedure(String sql) throws SQLException {
        try {
            if (!isConnected())
                this.connection();
            if (this.cstmt == null)
                cstmt = conn.prepareCall(sql);
            return cstmt.execute();
        } catch (Exception e) {
            System.err.println("DatabaseBean callExecuteProcedure error"
                    + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-CallExecuteProcedure error ");
        }
    }
    
    public boolean callExecuteProcedure(String sql,String[] paraStrings) throws SQLException {
        try {
            if (!isConnected())
                this.connection();
            if (this.cstmt == null)
                cstmt = conn.prepareCall(sql);
            for(int i=0; i<paraStrings.length; i++) {
                cstmt.setString(i+1, paraStrings[i]);
            }
            cstmt.registerOutParameter(2, Types.INTEGER);
            return cstmt.execute();
        } catch (Exception e) {
            System.err.println("DatabaseBean callExecuteProcedure error"
                    + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-CallExecuteProcedure error ");
        }
    }
    
    public boolean callExecuteProcedure(String sql,String paraString) throws SQLException {
		try {
			if (!isConnected())
				this.connection();
			if (this.cstmt == null)
				cstmt = conn.prepareCall(sql);
			cstmt.setString(1, paraString);
			cstmt.registerOutParameter(2, Types.INTEGER);
			
			return cstmt.execute();
		} catch (Exception e) {
			System.err.println("DatabaseBean callExecuteProcedure error"
					+ e.getMessage());
			e.printStackTrace();
			throw new SQLException("-CallExecuteProcedure error ");
		}
    }

    public void executeBatch(ArrayList<String> sqlBatch) throws SQLException {
        try {
            if (!isConnected())
                this.connection();
            conn.setAutoCommit(false);
            if (this.stmt == null)
                stmt = conn.createStatement();
            for (int i = 0; i < sqlBatch.size(); i++) {
                stmt.addBatch((String) sqlBatch.get(i));
            }
            stmt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            System.err.println("DatabaseBean executeBatch error"
                    + e.getMessage());
            e.printStackTrace();
            throw new SQLException("-ExecuteBatch error ");
        }
    }

    public PreparedStatement createprepareStatement(String paramSQL)
            throws SQLException {
        try {
            if (!isConnected())
                this.connection();
            return conn.prepareStatement(paramSQL);

        } catch (Exception e) {
            System.err.println("DatabaseBean PreparedStatement error");
            e.printStackTrace();
            throw new SQLException("-DatabaseBean PreparedStatement error ");
        }
    }

    public void transactionStart() throws SQLException {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("DatabaseBean transactionStart");
            e.printStackTrace();
            throw new SQLException("-Transaction start error ");
        }
    }

    public void transactionEnd() throws SQLException {
        try {
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("DatabaseBean transactionEnd error");
            e.printStackTrace();
            throw new SQLException("-Transaction end error:");
        }
    }

    public void transactionRollback() throws SQLException {
        try {
            conn.rollback();
        } catch (SQLException e) {
            System.err.println("DatabaseBean transactionRollback error");
            e.printStackTrace();
            throw new SQLException("-Transaction rollback error ");
        }
    }

}