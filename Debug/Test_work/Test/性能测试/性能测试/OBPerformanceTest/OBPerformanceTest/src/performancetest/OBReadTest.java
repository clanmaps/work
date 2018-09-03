package performancetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OBReadTest {

	//×Ô¶¯»¯²âÊÔ
	public static void control(File inputFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String inputLine = null;
			List<String[]> inputLines = new ArrayList<String[]>();
			int threadNum = 0;
			while((inputLine = br.readLine()) != null) {
				
				if(inputLine.contains("**")) {
					continue;
				}
				
				if(!inputLine.contains("#")) {
					String[] arr = inputLine.split("\t");
					System.out.println(Arrays.toString(arr));
					inputLines.add(arr);
					threadNum += Integer.parseInt(arr[0]);
					continue;
				}
				
				long startTime = System.currentTimeMillis();
				ReadTest.init(threadNum);
				for(int i = 0; i < inputLines.size(); i++) {
					String[] arr = inputLines.get(i);
					int threadNumPerMS = Integer.parseInt(arr[0]);
					for(int j = 0; j < threadNumPerMS; j++) {
						new Thread(new ReadTest(arr[1], arr[2], arr[3], arr[4], arr[5], 
								Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]),
								Byte.parseByte(arr[9]), Boolean.parseBoolean(arr[10]), arr[11],
								Byte.parseByte(arr[12]), Integer.parseInt(arr[13]))).start();
					}
				}
				System.out.println(ReadTest.getStatisticInfo());
				System.out.println((System.currentTimeMillis() - startTime) / 1000 + "s\n");
				
				inputLines.clear();
				threadNum = 0;
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
	
	public static void main(String[] args) {
		OBReadTest.control(new File(args[0]));
	}
}
