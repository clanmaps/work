package dataimport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import performancetest.DBConnector;

/**
 * 
 * @author LiYuming
 * @time 20160403
 * 实现OB的简单数据表导入功能
 */
public class DataImport implements Runnable{

	private String ip = null;
	private String port = null;
	private int threadid;
	private int threadnum;
	private int size;
	private int startid;
	private String userName = null;
	private String passwd = null;

	public DataImport(String ip, String port, int threadid, int threadnum,
			int size, int startid, String username, String passwd) {
		super();
		this.ip = ip;
		this.port = port;
		this.threadid = threadid;
		this.threadnum = threadnum;
		this.size = size;
		this.startid = startid;
		this.userName = username;
		this.passwd = passwd;
	}

	public void run() {
		long starttime = System.currentTimeMillis();
		DBConnector dbConnector = new DBConnector(ip, port, userName, passwd);
		Connection conn = dbConnector.getJDBCConnection();
		PreparedStatement pstmt = null;
		//-------------------
		String sql = "replace into test values (?, ?, ?, ?)";
		//-------------------
		try {
			pstmt = conn.prepareStatement(sql);
			Random random = new Random();
			for(int i = 0;  i < size; i++) {
				if(i % threadnum == threadid) {
					//自动处理因换主而造成的连接中断
					try {
						//-------------------
						pstmt.setInt(1, startid + i);
						pstmt.setInt(2, random.nextInt(100000000));
						pstmt.setInt(3, random.nextInt(100000000));
						pstmt.setInt(4, random.nextInt(100000000));
						pstmt.executeUpdate();
						//-------------------
					} catch (Exception e) {
						i--;
						int count = 1;
						while(true) {
							Thread.sleep(3000 * count++);
							conn = dbConnector.getJDBCConnection();
							if(conn != null) {
								pstmt = conn.prepareStatement(sql);
								System.out.println(ip + " has connected!");
								break;
							}
							System.out.println(ip + " conn = null!");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)	pstmt.close();
				if(conn != null)	conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Thread.currentThread().getName() + ": " + 
				(System.currentTimeMillis() - starttime));
	}

	public static void main(String[] args) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "utf-8"));
			String inputLine = null;
			while((inputLine = br.readLine()) != null) {
				String[] arr = inputLine.split(" ");
				
				System.out.println(Arrays.toString(arr));
				
				//ms ip; ms port; 本组线程数; 本组共需插入的数据行数; 本组线程插入数据的起始id
				String ip = arr[0];
				String port = arr[1];
				int threadnum = Integer.parseInt(arr[2]);
				int size = Integer.parseInt(arr[3]);
				int startid = Integer.parseInt(arr[4]);
				String username = "admin";
				String passwd = "admin";
				for(int threadid = 0; threadid < threadnum; threadid++) {
					new Thread(new DataImport(ip, port, threadid, threadnum, size, 
							startid, username, passwd)).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null)	br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
