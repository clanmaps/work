package jsydljl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import performancetest.DBConnector;

public class LogSizeTester {

	public static void main(String[] args) throws SQLException, InterruptedException {
		DBConnector dbConnector = new 
		DBConnector("10.11.1.197", "41880", "admin", "admin");
		Connection conn = dbConnector.getJDBCConnection();
		conn.createStatement().executeUpdate("drop table if exists jsydljl;");
		conn.createStatement().executeUpdate("create table jsydljl (id1 int, id2 int, " +
				"id3 int, id4 int, id5 int, id6 int, id7 int, id8 int, value1 int, " +
				"value2 int, value3 int, value4 int, value5 int, value6 int, value7 " +
				"int, value8 varchar(32), primary key(id1, id2, id3, id4, id5, " +
		"id6, id7, id8));");

		PreparedStatement pstmt1 = conn.prepareStatement("insert into jsydljl " +
		"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		PreparedStatement pstmt2 = conn.prepareStatement("update jsydljl set value1 " +
				"= ?, value2 = ?, value3 = ?, value4 = ?, value5 = ?, value6 = ?, " +
				"value7 = ?, value8 = ? where id1 = ? and id2 = ? and id3 = ? and " +
		"id4 = ? and id5 = ? and id6 = ? and id7 = ? and id8 = ?");

		int size = 1000;

		conn.setAutoCommit(false);
		for(int i = 0; i < size; i++) {
			pstmt1.setInt(1, i);
			pstmt1.setInt(2, 0);
			pstmt1.setInt(3, 0);
			pstmt1.setInt(4, 0);
			pstmt1.setInt(5, 0);
			pstmt1.setInt(6, 0);
			pstmt1.setInt(7, 0);
			pstmt1.setInt(8, 0);
			pstmt1.setInt(9, 0);
			pstmt1.setInt(10, 0);
			pstmt1.setInt(11, 0);
			pstmt1.setInt(12, 0);
			pstmt1.setInt(13, 0);
			pstmt1.setInt(14, 0);
			pstmt1.setInt(15, 0);
			pstmt1.setString(16, "000000000000000000000000000000");
			pstmt1.executeUpdate();
		}
		conn.commit();

		for(int i = 0; i < size; i++) {
			pstmt2.setInt(1, 1);
			pstmt2.setInt(2, 1);
			pstmt2.setInt(3, 1);
			pstmt2.setInt(4, 1);
			pstmt2.setInt(5, 1);
			pstmt2.setInt(6, 1);
			pstmt2.setInt(7, 1);
			pstmt2.setString(8, "111111111111111111111111111111");
			pstmt2.setInt(9, i);
			pstmt2.setInt(10, 0);
			pstmt2.setInt(11, 0);
			pstmt2.setInt(12, 0);
			pstmt2.setInt(13, 0);
			pstmt2.setInt(14, 0);
			pstmt2.setInt(15, 0);
			pstmt2.setInt(16, 0);
			pstmt2.executeUpdate();
		}
		conn.commit();
		
		for(int i = 0; i < size; i++) {
			pstmt2.setInt(1, 1);
			pstmt2.setInt(2, 1);
			pstmt2.setInt(3, 1);
			pstmt2.setInt(4, 1);
			pstmt2.setInt(5, 1);
			pstmt2.setInt(6, 1);
			pstmt2.setInt(7, 1);
			pstmt2.setString(8, "111111111111111111111111111111");
			pstmt2.setInt(9, i);
			pstmt2.setInt(10, 0);
			pstmt2.setInt(11, 0);
			pstmt2.setInt(12, 0);
			pstmt2.setInt(13, 0);
			pstmt2.setInt(14, 0);
			pstmt2.setInt(15, 0);
			pstmt2.setInt(16, 0);
			pstmt2.executeUpdate();
		}
		conn.commit();
	}
}
