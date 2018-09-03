package test;//多线程插入表格数据

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class insertC implements Runnable {
	
	private String sql;
	private int keyBegin = 1;
	private int keyEnd = 100001;
	private static int threadNum = 10;
	private static String tablename = "t1";
	private int threadID;
	
	public insertC (int threadID) {
		this.threadID = threadID;
	}
	public static  Connection getConn() {  //ms连接
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.195:15520";
	    String username = "admin";
	    String password = "admin";
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
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		System.out.println(threadID);
		Connection conn = getConn();
		PreparedStatement ps = null;
		sql = "insert into "+ tablename+ "(c1,c2) values(?,?)";
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int localLoad = (keyEnd - keyBegin) / threadNum;
		for(int i = 0; i < localLoad; i++) {
			try {
				ps.setInt(1, keyBegin + threadID + threadNum * i);
				ps.setInt(2, 1);
				ps.executeUpdate();
			} catch (Exception e1) {
				//-----重连
				while(conn == null) {
					try {
						Thread.sleep(100);
						conn = getConn();// for OB JDBC
					} catch (Exception e2) {
					}
				}
				try {
					ps = conn.prepareStatement(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//-----重连
				i--;
				System.out.println("reconnection!");		
			}
		} 
		try {
			if(ps != null)
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
        System.out.println("运行时间：" + (endTime - startTime) + "ms");
	}
	public static void main(String[] args) {
		for(int i = 0; i < threadNum; i++) {
			new Thread(new insertC(i)).start();
		}
		
	}
}
