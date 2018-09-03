package test;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Update {
	static final CountDownLatch end =new CountDownLatch(60);
	public static  Connection getConn() {  //ms����
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
	    System.out.println("c1\tc2");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t"+rs.getString(2));
	    }
	    System.out.println("===================");
	    return 0;
	}
	
	public static void main(String args[]) throws Exception {
		oneUpdate u1 = new oneUpdate("Thread1");
		mulUpdate u2 = new mulUpdate("Thread2");
		u1.start();
		u2.start(); 
		end.await();
		System.out.println("The result of Insert:");
		selectAll(getConn());

	    
	}
	
}


class oneUpdate implements Runnable {   //�������
	   private Thread t;
	   private String threadName;
	   oneUpdate(String name) {
	      threadName = name;
	      System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
			for(int idx = 1;idx <= 60;++idx) {
				int ranval = (int)(Math.random()*100+1);
				Connection conn = insertn.getConn(); //ms1д
			    String sql = "update t1 set c2=2 where c1="+ranval; //�������
			    PreparedStatement pstmt = null ;
			    try {
					pstmt = (PreparedStatement) conn.prepareStatement(sql);
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
		        try {
					Thread.sleep(30);
					//System.out.println("����");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        Update.end.countDown();
			}
	   }
	   
   public void start () {
      System.out.println("Starting " +  threadName );
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
}
   
class mulUpdate implements Runnable {   //�������
   private Thread t;
   private String threadName;
   mulUpdate(String name) {
      threadName = name;
      System.out.println("Creating " +  threadName );
   }
   
   public void run() {
			Connection conn = insertn.getConn(); //ms1д
		    String sql = "update t1 set c2=3 where c1>20 and c1<40"; //�������
		    PreparedStatement pstmt = null ;
		    try {
			Thread.sleep(500);
			//System.out.println("����");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    try {
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
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
   }
   
   public void start () {
      System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }	   
   }
}
		
	   