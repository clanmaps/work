package test;

import java.sql.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class insertn {
	
	public static  Connection getConn() {  //ms1����
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.195:15520";
	    String username = "admin";
	    String password = "admin";
	    Connection conn = null;
	    try {
	        Class.forName(driver); //classLoader,���ض�Ӧ����
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return conn;
	}
	
	public static  int selectAll(Connection conns) throws SQLException {  //select��������
		Connection conn = conns;
	    String sql;
		Statement stmt = conn.createStatement();
	    sql = "select * from t1";
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println("===================");
	    System.out.println("ID\t����");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
	    }
	    System.out.println("===================");
	    return 0;
	}
	
	public static void main(String args[]) throws Exception {
		long startTime = System.currentTimeMillis();
		for(int idx = 1;idx <= 100000;++idx) {
		Connection conn = insertn.getConn(); //ms1д
		//System.out.println("The result of Insert:");
	    String sql = "insert into t1 (c1,c2) values(?,?)"; //�������
	    PreparedStatement pstmt ;
	    pstmt = (PreparedStatement) conn.prepareStatement(sql);
        pstmt.setInt(1, idx);
        pstmt.setInt(2, 1);
	    pstmt.executeUpdate();
        pstmt.close();
        conn.close();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("��������ʱ�䣺" + (endTime - startTime) + "ms"); 
		//System.out.println("The result of Insert:");
		//selectAll(getConn());

	    
	}


}
