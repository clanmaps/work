package disktest;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class DiskTest {

	public static void main(String[] args) throws Exception {
		long start_time = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(".//test.txt");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
		FileDescriptor fd = fos.getFD();
		int count = 0;
		while(System.currentTimeMillis() - start_time < 20000) {
			bw.write("a\n");
			bw.flush();
			fd.sync();
			count++;
		}
		if(bw != null)	bw.close();
		System.out.println(count/20);
	}
}
