package test;

import java.sql.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class statis {
	
	public static  Connection getConn() {  //ms1����
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.195:15530/database_test";
	    String username = "root";
	    String password = "root";
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
	    sql = "select count(1) from test";
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println("===================");
	    System.out.println("ID\t����");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t");
	    }
	    System.out.println("===================");
	    return 0;
	}
	
	public static void main(String args[]) throws Exception {
		Connection conn = statis.getConn(); //ms1д

        conn.close();
		}
		//System.out.println("The result of Insert:");
		//selectAll(getConn());

	    
	}


}
