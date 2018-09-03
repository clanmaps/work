package test;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;


public class TestNew {
	public static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(1,true);  //阻塞队列 
	public static ArrayBlockingQueue<String> queue2 = new ArrayBlockingQueue<String>(1,true);
	public static  Connection getConn() {  //ms1连接
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.194:15520";
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
	
	public static Connection getConn2() {  //ms2连接
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.194:15522";
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
		write w1 = new write("Thread1");
		w1.start();
		read r1 = new read("Thread2");
		r1.start();
	}

}





class write implements Runnable {   //写线程
	   private Thread t;
	   private String threadName;
	   public int idx=0;
	   int count = 0;
	   TestNew t1 = new TestNew();
	   write(String name) {
	      threadName = name;
	      System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
		   while(TestNew.queue.isEmpty()) 
		   {      ++idx;
			      System.out.println("Running " +  threadName );
			      /*try {
					TestNew.selectAll(TestNew.getConn());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			      Connection conn = TestNew.getConn(); //ms1写
				    int i = 0;
				    String newname = "wang";
				    String sql = "update test set name='"+ newname +"'where id="+idx; //更新操作
				    PreparedStatement pstmt;
				    try {
				        pstmt = (PreparedStatement) conn.prepareStatement(sql);
				        i = pstmt.executeUpdate();
				        if(i == 1)
				        System.out.println("update: " + (++count));
				        pstmt.close();
				        conn.close();
				    } catch (SQLException e) {
				        e.printStackTrace();
				    }

				    try {
						TestNew.queue.put(newname);  //更新值放入队列
						TestNew.queue2.put(TestNew.queue.take()); //将queue中的值传给queue2
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 	  
			      System.out.println("Thread " +  threadName + " exiting.");   
			      if(idx>=10000) break;
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

class read implements Runnable {    //读线程
	   private Thread t;
	   private String threadName;
	   public String name1;
	   TestNew t1 = new TestNew();
	   CountDownLatch count1;
	   read(String name) {
	      threadName = name;
	      System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
		   for(int i = 0;i < 10000;++i) {

			      System.out.println("Running " +  threadName );
			      try {
					TestNew.queue2.take();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			      try {
						TestNew.selectAll(TestNew.getConn2());  //ms2读
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		    
			      System.out.println("Thread " +  threadName + " exiting.");
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

