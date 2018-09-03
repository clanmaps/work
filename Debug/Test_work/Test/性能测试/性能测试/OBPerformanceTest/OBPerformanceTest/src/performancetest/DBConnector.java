package performancetest;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {

	private String ip = null;
	private String port = null;
	private String dbName = null;
	private String userName = null;
	private String passwd = null;

	public DBConnector(String ip, String port, String userName, String passwd) {
		super();
		this.ip = ip;
		this.port = port;
		this.userName = userName;
		this.passwd = passwd;
	}

	public DBConnector(String ip, String port, String dbName, String userName,
			String passwd) {
		super();
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		this.userName = userName;
		this.passwd = passwd;
	}

	public Connection getMySQLConnection() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName;
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Connection getPostgreSQLConnection() {
		String driver = "org.postgresql.Driver";
		String url = "jdbc:postgresql://" + ip + ":" + port + "/" + dbName;
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public Connection getJDBCConnection() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + ip + ":" + port + 
		"/mysql?useServerPrepStmts=true";
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, userName, passwd);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return conn;
	}
}
