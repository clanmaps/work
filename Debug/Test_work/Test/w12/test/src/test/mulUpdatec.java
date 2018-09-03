package test;//多行更新并发测试

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Connection;

public class mulUpdatec {
	public static int cnt1=100000;
	public static int cnt2=10000;
	public static int update_value=35;
	private static int threadNum = 10;
	public static String tablename="t1";
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
	
	public static  int selectAll(Connection conns) throws SQLException {  //select所有内容
		Connection conn = conns;
	    String sql;
		Statement stmt = conn.createStatement();
	    sql = "select * from "+tablename;
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println("===================");
	    System.out.println("c1\tc2");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
	    }
	    System.out.println("===================");
	    return 0;
	}
	
	public static void main(String args[]) throws Exception {
		for(int i = 1; i <=threadNum; i++) {
			new Thread(new threadUpMul(i)).start();
		}
	}	
}

class threadUpMul implements Runnable {   
   private Thread t;
   private String threadName;
   private int threadID;
	
   public threadUpMul (int threadID) {
		this.threadID = threadID;
	}
   
   public void run() {
	   long startTime = System.currentTimeMillis();
	   int ran1 = (int)(Math.random()*mulUpdatec.cnt1);
	   int ran2 = (int)(Math.random()*mulUpdatec.cnt2);
	   	Connection conn = mulUpdatec.getConn(); //ms1写
	    String sql = "update " + mulUpdatec.tablename + " set c2="+mulUpdatec.update_value+"where c1>"+ran1+"and c1<"+(ran1+ran2); //更新操作
	    PreparedStatement pstmt = null ;
	    try {
		Thread.sleep(500);
			//System.out.println("休眠");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			pstmt = conn.prepareStatement(sql);
		} catch (SQLException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			pstmt.executeUpdate();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
        try {
			pstmt.close();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			conn.close();
		} catch (SQLException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //System.out.println("Exiting " +  threadName );
        long endTime = System.currentTimeMillis();
        System.out.println("Thread" + threadID+": L=" +  ran1+",R=" + (ran1 + ran2) + ",size=" + ran2 + ",runtime=" + (endTime - startTime) + "ms");
   }
   
   public void start () {
      //System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }	   
   }
}
