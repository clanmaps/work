package performancetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author LiYuming
 * @time 20160402
 * 针对数据库加读负载
 */
public class ReadTest implements Runnable{

	//连接数据库信息
	private String ip = null;
	private String port = null;
	private String dbName = null;
	private String userName = null;
	private String passwd = null;
	//属性取值范围
	private int minValue;
	private int maxValue;
	//负载量
	private int workload;
	//数据库类型(0:mysql; 1:pg; 2:OB)
	private byte dbType;
	//是否预编译执行
	private boolean prepared;
	//sql语句
	private String sql;
	//负载类型(0:主键或非主键上的随机读; 1:范围读)
	private byte loadType;
	//读范围大小
	private int range;

	//qps信息收集列表
	private static List<Integer> qpsList = null;
	//qrs信息收集列表
	private static List<Integer> qrsList = null;
	//全局互斥锁
	private static Boolean[] lock = null;
	//标示统计信息是否已收集完成
	private static CountDownLatch cdl = null;

	public ReadTest(String ip, String port, String dbName, String userName,
			String passwd, int minKey, int maxKey, int workload, byte dbType,
			boolean prepared, String sql, byte loadType, int range) {
		super();
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.passwd = passwd;
		this.minValue = minKey;
		this.maxValue = maxKey;
		this.workload = workload;
		this.dbType = dbType;
		this.prepared = prepared;
		this.sql = sql;
		this.loadType = loadType;
		this.range = range;
	}

	public static void init(int threadNum) {
		cdl = new CountDownLatch(threadNum);
		qpsList = new ArrayList<Integer>();
		qrsList = new ArrayList<Integer>();
		lock = new Boolean[0];
	}

	public void run() {
		//获取数据库连接
		DBConnector dbConnector = new DBConnector(ip, port, dbName, userName, passwd);
		Connection conn = null;
		while(conn == null) {
			try {
				Thread.sleep(100);
				if(dbType == 0)
					conn = dbConnector.getMySQLConnection();
				else if(dbType == 1)
					conn = dbConnector.getPostgreSQLConnection();
				else if(dbType == 2)
					conn = dbConnector.getJDBCConnection();// for OB JDBC
			} catch (Exception e) {
			}
		}
		long startTime = System.nanoTime();
		PreparedStatement ps = null;
		Statement st = null;
		try {
			if(dbType == 2)
				conn.createStatement().executeQuery("set @@session.ob_query_timeout=9000000000;");
			if(prepared)
				ps = conn.prepareStatement(sql);
			else
				st = conn.createStatement();
			Random random = new Random();
			//加读负载
			for(int i = 0; i < workload; i++) {
				int id = random.nextInt(maxValue - minValue) + minValue;
				if(prepared) {
					if(loadType == 0)
						ps.setInt(1, id);
					else if(loadType == 1) {
						ps.setInt(1, id);
						ps.setInt(2, id + random.nextInt(range));
					}
					ps.executeQuery();
				} else {
					if(loadType == 0)
						st.executeQuery(sql + " " + id);
					else if(loadType == 1) {
						st.executeQuery(sql + " " + id + " and " + (id + random.nextInt(range)));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(st != null)	st.close();
				if(ps != null)	ps.close();
				if(conn != null)	conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		int qps = workload / (int)((System.nanoTime() - startTime) / 1000000000);
		int qrs = (int)((System.nanoTime() - startTime) / 1000 / workload);
		synchronized (lock) {
			qpsList.add(qps);
			qrsList.add(qrs);
			cdl.countDown();
		}
	}

	public static String getStatisticInfo() {
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int qpsCount = 0, qrsCount = 0;
		System.out.println(qpsList);
		System.out.println(qrsList);
		for(int i = 0; i < qpsList.size(); i++) {
			qpsCount += qpsList.get(i);
			qrsCount += qrsList.get(i);
		}
		return "QPS: " + qpsCount + "\tQRS: " + ((float)qrsCount / qrsList.size() / 1000) + "ms";
	}

	//自动化测试
	public static void control(File inputFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String inputLine = null;
			while((inputLine = br.readLine()) != null) {
				long startTime = System.currentTimeMillis();
				String[] arr = inputLine.split("\t");
				System.out.println(Arrays.toString(arr));
				int threadNum = Integer.parseInt(arr[0]);
				ReadTest.init(threadNum);
				for(int i = 0; i < threadNum; i++) {
					new Thread(new ReadTest(arr[1], arr[2], arr[3], arr[4], arr[5], 
							Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]),
							Byte.parseByte(arr[9]), Boolean.parseBoolean(arr[10]), arr[11],
							Byte.parseByte(arr[12]), Integer.parseInt(arr[13]))).start();
				}
				System.out.println(ReadTest.getStatisticInfo());
				System.out.println((System.currentTimeMillis() - startTime) / 1000 + "s\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null)	br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	//test
	public static void main(String[] args) {
		ReadTest.control(new File(args[0]));
	}
}
