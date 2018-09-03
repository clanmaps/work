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

import datagenerator.RanDataGene;

/**
 * @author LiYuming
 * @time 20160402
 * ������ݿ��д����
 */
public class WriteTest implements Runnable{

	//�������ݿ���Ϣ
	private String ip = null;
	private String port = null;
	private String dbName = null;
	private String userName = null;
	private String passwd = null;
	//����ȡֵ��Χ(for update)
	private int minKey;
	private int maxKey;
	//������(for update)
	private int workload;
	//���ݿ�����(0:mysql; 1:pg; 2:ob)
	private byte dbType;
	//�Ƿ�Ԥ����ִ��
	private boolean prepared;
	//sql���
	private String sql;
	//��������(0:insert & replace; 1:delete; 2:update)
	private byte loadType;
	//����ԭ���ܴ󣩣�����ɾ������Χ��for insert & replace & delete
	private int keyBegin;
	private int keyEnd;
	//��ʾÿ���̵߳�����������for insert & replace & delete
	private int threadNum;
	private int threadID;

	//tps��Ϣ�ռ��б�
	private static List<Integer> tpsList = null;
	//trs��Ϣ�ռ��б�
	private static List<Integer> trsList = null;
	//ȫ�ֻ�����
	private static Boolean[] lock = null;
	//��ʾͳ����Ϣ�Ƿ����ռ���
	private static CountDownLatch cdl = null;

	public WriteTest(String ip, String port, String dbName, String userName,
			String passwd, int minKey, int maxKey, int workload, byte dbType,
			boolean prepared, String sql, byte loadType, int keyBegin,
			int keyEnd, int threadNum, int threadID) {
		super();
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.passwd = passwd;
		this.minKey = minKey;
		this.maxKey = maxKey;
		this.workload = workload;
		this.dbType = dbType;
		this.prepared = prepared;
		this.sql = sql;
		this.loadType = loadType;
		this.keyBegin = keyBegin;
		this.keyEnd = keyEnd;
		this.threadNum = threadNum;
		this.threadID = threadID;
	}

	public static void init(int threadNum) {
		cdl = new CountDownLatch(threadNum);
		tpsList = new ArrayList<Integer>();
		trsList = new ArrayList<Integer>();
		lock = new Boolean[0];
	}

	public void run() {
		//��ȡ���ݿ�����
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
		int localLoad = -1;
		try {
			if(dbType == 2)
				conn.createStatement().executeQuery("set @@session.ob_query_timeout=9000000000;");
			if(prepared)
				ps = conn.prepareStatement(sql);
			else
				st = conn.createStatement();
			Random random = new Random();
			//��д����
			if(loadType == 0) {//insert & replace
				localLoad = (keyEnd - keyBegin) / threadNum;
				if(prepared) {
					for(int i = 0; i < localLoad; i++) {
						//֧�ֻ�������
						try {
							ps.setInt(1, keyBegin + threadID + threadNum * i);
							ps.setInt(2, random.nextInt(1000000000));
							ps.setString(3, RanDataGene.getString(50));
							ps.executeUpdate();
						} catch (Exception e1) {
							//-----����
							while(conn == null) {
								try {
									Thread.sleep(100);
									conn = dbConnector.getJDBCConnection();// for OB JDBC
								} catch (Exception e2) {
								}
							}
							conn.createStatement().executeQuery("set @@session.ob_query_timeout=9000000000;");
							ps = conn.prepareStatement(sql);
							//-----����
							i--;
							System.out.println("reconnection!");
						}
						//֧�ֻ�������
					}
				} else {
					for(int i = 0; i < localLoad; i++)
						st.executeUpdate(sql + "(" + (keyBegin + threadID + threadNum * i) + ", " +
								random.nextInt(1000000000) + ", '" + RanDataGene.getString(20) + "')");
				}
			} else if(loadType == 1) {//delete
				localLoad = (keyEnd - keyBegin) / threadNum;
				if(prepared) {
					for(int i = 0; i < localLoad; i++) {
						ps.setInt(1, keyBegin + threadID + threadNum * i);
						ps.executeUpdate();
					}
				} else {
					for(int i = 0; i < localLoad; i++)
						st.executeUpdate(sql + " " + (keyBegin + threadID + threadNum * i));
				}
			} else if(loadType == 2) {//update
				for(int i = 0; i < workload; i++) {
					if(prepared) {
						//֧�ֻ�������
						try {
							ps.setInt(1, random.nextInt(1000000000));
							ps.setInt(2, keyBegin + threadID + threadNum * i);
							ps.executeUpdate();
						} catch (Exception e1) {
							//-----����
							while(conn == null) {
								try {
									Thread.sleep(100);
									conn = dbConnector.getJDBCConnection();// for OB JDBC
								} catch (Exception e2) {
								}
							}
							conn.createStatement().executeQuery("set @@session.ob_query_timeout=9000000000;");
							ps = conn.prepareStatement(sql);
							//-----����
							i--;
							System.out.println("reconnection!");
						}
						//֧�ֻ�������
					} else {
						st.executeUpdate(sql + " " + random.nextInt(1000000000) + " where id = " + 
								(random.nextInt(maxKey - minKey) + minKey));
					}
				}
				localLoad = workload;
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

		int tps = localLoad / (int)((System.nanoTime() - startTime) / 1000000000);
		int trs = (int)((System.nanoTime() - startTime) / 1000 / localLoad);
		synchronized (lock) {
			tpsList.add(tps);
			trsList.add(trs);
			cdl.countDown();
		}
	}

	public static String getStatisticInfo() {
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int tpsCount = 0, trsCount = 0;
		System.out.println(tpsList);
		System.out.println(trsList);
		for(int i = 0; i < tpsList.size(); i++) {
			tpsCount += tpsList.get(i);
			trsCount += trsList.get(i);
		}
		return "TPS: " + tpsCount + "\tTRS: " + ((float)trsCount / tpsList.size() / 1000) + "ms";
	}

	//�Զ�������
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
				WriteTest.init(threadNum);
				for(int i = 0; i < threadNum; i++) {
					new Thread(new WriteTest(arr[1], arr[2], arr[3], arr[4], arr[5], Integer.parseInt(arr[6]), 
							Integer.parseInt(arr[7]), Integer.parseInt(arr[8]), Byte.parseByte(arr[9]),
							Boolean.parseBoolean(arr[10]), arr[11], Byte.parseByte(arr[12]), 
							Integer.parseInt(arr[13]), Integer.parseInt(arr[14]), Integer.parseInt(arr[0]), i)).start();
				}
				System.out.println(WriteTest.getStatisticInfo());
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
		WriteTest.control(new File(args[0]));
	}
}
