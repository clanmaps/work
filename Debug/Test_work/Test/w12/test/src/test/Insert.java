package test;

import java.sql.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Insert {
	
	public static  Connection getConn() {  //ms1连接
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.195:15530/database_test";
	    String username = "root";
	    String password = "root";
	    Connection conn = null;
	    try {
	        Class.forName(driver); //classLoader,加载对应驱动
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return conn;
	}
	
	public static  int selectAll(Connection conns) throws SQLException {  //select所有内容
		Connection conn = conns;
	    String sql;
		Statement stmt = conn.createStatement();
	    sql = "select * from test";
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println("===================");
	    System.out.println("ID\t姓名");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
	    }
	    System.out.println("===================");
	    return 0;
	}
	
	public static void main(String args[]) throws Exception {

		for(int idx = 1,idx2 = 11,idx3 = 21;idx <= 10;++idx,++idx2,++idx3) {
		Connection conn = Insert.getConn(); //ms1写
	    String newname = "zhang";
	    String sql = "insert into t1 (c1,c2,c3,c4,c5) values(?,?,?,?,?)"; //插入操作
	    PreparedStatement pstmt ;
	    pstmt = (PreparedStatement) conn.prepareStatement(sql);
        pstmt.setInt(1, idx);
        pstmt.setInt(2, idx2);
        pstmt.setInt(3, idx3);
        pstmt.setInt(4, idx);
        pstmt.setString(5, newname);
	    pstmt.executeUpdate();
        pstmt.close();
        conn.close();
		}
		//System.out.println("The result of Insert:");
		//selectAll(getConn());

	    
	}


}
