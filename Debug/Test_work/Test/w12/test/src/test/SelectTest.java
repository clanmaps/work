package test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

public class SelectTest implements Runnable
{
	public static final int threadNum = 200;//线程数
	public static int loopNum = 2000;//循环次数
	public static int totalTime = 0; // 每个线程运行时间总和
	public static String tableName = "tt01";//表名
	private static Object lock = new Object();
	private static int num = 0;
	private String url;
	private int keyId;
	private final CountDownLatch doneSignal;

	public SelectTest(CountDownLatch doneSignal, String url, int keyId)
	{
		this.doneSignal = doneSignal;
		this.url = url;
		this.keyId = keyId;
	}

	@Override
	public void run()
	{
		byte[] b = new byte[50];  // 原始为50
		for (int i = 0; i < 50; i++)
		{
			b[i] = 'g';
		}
		String str = new String(b);

		// 驱动程序名
		String driver = "com.mysql.jdbc.Driver";

		// URL指向要访问的数据库名
		// String url = "jdbc:mysql://localhost:3306/vast";

		// MySQL配置时的用户名
		String user = "admin";

		// MySQL配置时的密码
		String password = "admin";
		
		// SELECT返回的结果集
		ResultSet rs = null;

		try
		{
			// 加载驱动程序
			Class.forName(driver);

			// 连续数据库
			Connection conn = DriverManager.getConnection(url, user, password);

			// statement用来执行SQL语句			
			Statement statement = conn.createStatement();

			// 要执行的SQL语句
			String sql = "replace into tt01 values(" + keyId + ", '" + str
					+ "')";
			String queryStr = "select * from " + tableName + " where id = " + keyId;
			String querysql = "create table test(c1 int primary key, c2 int)";
			// 采用PreparedStatement
			// PreparedStatement statement = conn
			// .prepareStatement("replace into tt01 values " + "(?, ?)");
			// statement.setInt(1, keyId);
			// statement.setString(2, str);
			PreparedStatement pstmt = conn.prepareStatement(sql);
//			pstmt.executeUpdate(querysql);
			long startTime = System.currentTimeMillis();

			for (int i = 0; i < loopNum; i++)
			{
				// statement.executeUpdate();
			    try
                {
			        statement.executeUpdate(sql);
                }
                catch (Exception e)
                {
                    while(conn==null)
                    {
                        conn = DriverManager.getConnection("jdbc:mysql://10.11.1.193:15520//oceanbase", user, password);
                    }
                }
				 
				// queryStr = "select * from " + tableName + " where id = " + (keyId + i);
				//rs = pstmt.executeQuery();
				// System.out.println("i = " + i);
			}

			// System.out.println(System.currentTimeMillis() - startTime);
			long singalTotalTime = System.currentTimeMillis() - startTime;
			synchronized (lock)
			{
				SelectTest.totalTime += singalTotalTime;
				SelectTest.num += 1;
				// System.out.println("Thread number is " + SelectTest.num +
				// ". TotalTime:" + SelectTest.totalTime);
			}

			statement.close();

			conn.close();

			doneSignal.countDown();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		// java -jar test.jar IP:PORT TABLE_NAME REPLACE_KEY THREAD_NUM LOOP_NUM
		//                    args[0]  args[1]   args[2]     args[3]    args[4]
		// CountDownLatch startSignal = new CountDownLatch(1);
		// CountDownLatch doneSignal = new
		// CountDownLatch(SelectTest.threadNum);
		int threadNum = 40;  //at least 40 threads per MS
		tableName = "test_table";
//		if (null != args[4])
//		{
//			loopNum = 5000;
//		}
		SelectTest.loopNum = 5000;
		CountDownLatch doneSignal = new CountDownLatch(threadNum);

		Thread[] tN = new Thread[threadNum];

		for (int i = 0; i < threadNum; i++)
		{
//			tN[i] = new Thread(new SelectTest(doneSignal, "jdbc:mysql://"
//					+ args[0] + "//oceanbase", Integer.parseInt(args[2]) + i * loopNum
//					+ 1));
			new Thread(new SelectTest(doneSignal, "jdbc:mysql://10.11.1.195:15520//oceanbase", Integer.parseInt("1") + i * loopNum
					+ 1)).start();
		}


		// startSignal.countDown();
		doneSignal.await();

		System.out.println("Thread number is " + SelectTest.num
				+ ". TotalTime:" + SelectTest.totalTime);
		System.out.println("Average thread runtime is "
				+ (SelectTest.totalTime / threadNum));
		System.out.println("Average latency is " 
				+ ((double)SelectTest.totalTime / threadNum / loopNum));
	}
}



