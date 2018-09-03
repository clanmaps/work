package performancetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class RemoteShell {

	//execute a shell command on the target machine
	public static String exec(String ip, String userName, String passwd, String cmd) {
		Connection conn = null;
		InputStream in = null;
		String result = null;
		try {
			conn = new Connection(ip);
			conn.connect();
			if (conn.authenticateWithPassword(userName, passwd)) {
				Session session = conn.openSession();
				session.execCommand(cmd);
				in = session.getStdout();
				result = processStdout(in, Charset.defaultCharset().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)	in.close();
				if(conn != null)	conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//convert byte stream into character stream
	private static String processStdout(InputStream in, String charset) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in, charset));
			String inputLine = br.readLine();
			while (inputLine != null) {
				sb.append(inputLine + "\n");
				inputLine = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	//test
	public static void main(String[] args) throws Exception{

		String sysInfo1 = RemoteShell.exec("10.11.1.190", "liyuming", "304624lym", 
		"/home/liyuming/commitlog/ecnu/oceanbase/bin/rs_admin -r 10.11.1.190 -p 54500 get_obi_role");
		String sysInfo2 = RemoteShell.exec("10.11.1.193", "liyuming", "304624lym", 
		"/home/liyuming/commitlog/ecnu/oceanbase/bin/rs_admin -r 10.11.1.193 -p 54500 get_obi_role");
		String sysInfo3 = RemoteShell.exec("10.11.1.196", "liyuming", "304624lym", 
		"/home/liyuming/commitlog/ecnu/oceanbase/bin/rs_admin -r 10.11.1.196 -p 54500 get_obi_role");

		System.out.println(sysInfo1);
		System.out.println(sysInfo2);
		System.out.println(sysInfo3);
	}
} 

