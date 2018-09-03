package performancetest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Test {

	public static void main(String[] args) throws SQLException {
//		DBConnector dbConnector = new DBConnector("10.11.1.197", "11202", "admin", "admin");
//		Connection conn = dbConnector.getJDBCConnection();
//		PreparedStatement pstmt = conn.prepareStatement("select * from item where type = ?");
//		pstmt.setInt(1, 1001);
//		pstmt.executeQuery();
//		conn.createStatement().executeQuery("select itemkey from item where type = 1001");
		
		DBConnector dbConnector = new DBConnector("10.11.1.192", "31880", "admin", "admin");
		Connection conn = dbConnector.getJDBCConnection();
		PreparedStatement pstmt = conn.prepareStatement("update test set c2 = ? where c1 = ?");
		pstmt.setInt(1, 5555); 
		pstmt.setInt(2, 1); 
		pstmt.executeUpdate();
		
		conn.createStatement().executeUpdate("replace into test values (12,1)");
	}

}
