package test;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import test.Test.Student;

public class Test {
	static class Student {
        private int Id;
        private String Name;
        private String Sex;
        private int Age;

        Student(int Id, String Name, String Sex, int Age) {
            this.Id = Id; 
            this.Name = Name;
            this.Sex = Sex;
            this.Age = Age;
        }

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getSex() {
            return Sex;
        }

        public void setSex(String Sex) {
            this.Sex = Sex;
        }

        public int getAge() {
            return Age;
        }

        public void setage(int Age) {
            this.Age = Age;
        }

	}
	
	public static  Connection getConn() {  //ms1
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
	
	public static Connection getConn2() {  //ms2
	    String driver = "com.mysql.jdbc.Driver";
	    String url = "jdbc:mysql://10.11.1.195:15522";
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
	
	private  int insert(Student student) {
	    Connection conn = getConn();
	    int i = 0;
	    String sql = "insert into students (id,Name,Sex,Age) values(?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.setInt(1, student.getId());
	        pstmt.setString(2, student.getName());
	        pstmt.setString(3, student.getSex());
	        pstmt.setInt(4, student.getAge());
	        i = pstmt.executeUpdate();
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	
	
	public static  int update(Student student) {
	    Connection conn = getConn();
	    int i = 0;
	    String sql = "update students set age=" + student.getAge() + " where id='" + student.getId() + "'";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        i = pstmt.executeUpdate();
	        System.out.println("resutl: " + i);
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	
	public static  int selectAll(Connection conns) throws SQLException {  
		Connection conn = conns;
	    String sql;
		Statement stmt = conn.createStatement();
	    sql = "select * from students";
	    ResultSet rs = stmt.executeQuery(sql);
	    System.out.println("===================================");
	    System.out.println("ID\t姓名\t性别\t年龄");
	    while (rs.next()) {
	    	System.out.println(rs.getInt(1)+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getInt(4));
	    }
	    System.out.println("===================================");
	    return 0;
	}
	
	
	public  Integer getAll() {  //ms1
	    Connection conn = getConn();
	    String sql = "select * from students";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery();
	        int col = rs.getMetaData().getColumnCount();
	        System.out.println("===================================");
	        while (rs.next()) {
	            for (int i = 1; i <= col; i++) {
	                System.out.print(rs.getString(i) + "\t");
	                if ((i == 2) && (rs.getString(i).length() < 8)) {
	                    System.out.print("\t");
	                }
	             }
	            System.out.println("");
	        }
	            System.out.println("===================================");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	
	public  Integer getAll2() {  //ms2
	    Connection conn2 = getConn2();
	    String sql = "select * from students";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)conn2.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery();
	        int col = rs.getMetaData().getColumnCount();
	        System.out.println("===================================");
	        while (rs.next()) {
	            for (int i = 1; i <= col; i++) {
	                System.out.print(rs.getString(i) + "\t");
	                if ((i == 2) && (rs.getString(i).length() < 8)) {
	                    System.out.print("\t");
	                }
	             }
	            System.out.println("");
	        }
	            System.out.println("===================================");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private  int delete(String name) {
	    Connection conn = getConn();
	    int i = 0;
	    String sql = "delete from students where Name='" + name + "'";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        i = pstmt.executeUpdate();
	        System.out.println("resutl: " + i);
	        pstmt.close();
	        conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	

	
	public static void main(String args[]) throws Exception {

		CountDownLatch count1 = new CountDownLatch(1);
		RunnableDemo R1 = new RunnableDemo(count1,"Thread1");
		R1.start();
		count1.await();
		System.out.println("success");

	}
	
}


class RunnableDemo implements Runnable {
	   private Thread t;
	   private String threadName;
	   public String name1;
	   public static String agems1;
	   CountDownLatch count1;
	   ArrayBlockingQueue<String> abq = new ArrayBlockingQueue<String>(1,true);
	   RunnableDemo( CountDownLatch count1,String name) {
		  this.count1 = count1;
	      threadName = name;
	      System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
		
			      System.out.println("Running " +  threadName );
			      try {
					Test.selectAll(Test.getConn());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      Student student1 = new Student(2,"lisi0", "male", 12);
			      Test.update(student1);
			      System.out.println("Thread " +  threadName + " exiting.");
			      int age1 = student1.getAge();
			      agems1 = Integer.toString(age1);
			      if(abq.offer(agems1)) {
			    	  try {
						String sage2 = abq.take();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			  		RunnableDemo2 R2 = new RunnableDemo2("Thread2");
					R2.start();
			    	count1.countDown();
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


class RunnableDemo2 implements Runnable {
	   private Thread t;
	   private String threadName;
	   public static String agems2;
	   RunnableDemo2( String name) {
	      threadName = name;
	      System.out.println("Creating " +  threadName );
	   }
	   
	   public void run() {
	      System.out.println("Running " +  threadName );
	      try {
				Test.selectAll(Test.getConn2());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
		    Connection conn = Test.getConn2();
		    String sql = "select * from students where id = 2";
		    PreparedStatement pstmt;
		    try {
		        pstmt = (PreparedStatement)conn.prepareStatement(sql);
		        ResultSet rs = pstmt.executeQuery();
		        System.out.println("===================================");
		        while (rs.next()) {
			        agems2 = Integer.toString(rs.getInt(4)); 
			        System.out.println(agems2);

		            System.out.println("");
		        }
		            System.out.println("===================================");
		           
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	      System.out.println("Thread " +  threadName + " exiting.");
	   }
	   
	   public void start () {
	      System.out.println("Starting " +  threadName );
	      if (t == null) {
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
} 
	
	
	

	
	
	