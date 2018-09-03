package datagenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author LiYuming
 * @time 20160402
 * 简单的可配置的数据生成器
 */

public class DataGenerator {
	
	//待生成数据表的大小
	private int tableSize;
	//多主键之间的比例值
	private int[] keyRatio = null;
	//各属性信息
	private class AttributeInfo {
		//数据类型
		//int: 0; real: 1; decimal: 2; date: 3; string: 4; char: 5; bool: 6
		byte type;
		//数据类型的描述信息
		Long info1;
		Long info2;
		public AttributeInfo(byte type, Long info1, Long info2) {
			super();
			this.type = type;
			this.info1 = info1;
			this.info2 = info2;
		}
	}
	//属性信息列表
	private List<AttributeInfo> attributes = null;
	private String splitStr = "\t"; 

	public DataGenerator() {
		super();
		attributes = new ArrayList<AttributeInfo>();
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		if(args.length != 3) {
			System.out.println("error parameters!");
			return;
		}
		//配置信息
		File inputFile = new File(args[0]);
		//输出文件名模板
		File outputFile = new File(args[1]);
		//输出文件个数
		int outputFileSize = Integer.parseInt(args[2]);
		DataGenerator dg = new DataGenerator();
		dg.parse(inputFile);
		dg.generate(outputFile, outputFileSize);
		System.out.println("sucess!\ntime: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
	}

	private void parse(File inputFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "utf-8"));
			String inputLine = null;
			while((inputLine = br.readLine()) != null) {
				if(inputLine.trim().startsWith("#") || inputLine.trim().equals(""))
					continue;
				if(inputLine.trim().toLowerCase().startsWith("size")) {
					tableSize = Integer.parseInt(inputLine.split(":")[1].trim());
					continue;
				}
				if(inputLine.trim().toLowerCase().startsWith("key")) {
					String[] arr = inputLine.split(":")[1].split(";");
					keyRatio = new int[arr.length - 1];
					for(int i = 1; i < arr.length; i++) {
						keyRatio[i - 1] = (int)(Integer.parseInt(arr[i].trim()) / 
								Integer.parseInt(arr[0].trim()));
					}
					continue;
				}
				String[] arr = inputLine.split(";", -1);
				AttributeInfo attribute = new AttributeInfo(Byte.parseByte(arr[0].trim()), 
						arr.length < 2 ? null : Long.parseLong(arr[1].trim()), 
								arr.length < 3 ? null : Long.parseLong(arr[2].trim()));
				attributes.add(attribute);
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

	private void generate(File outputFile, int outputFileSize) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getAbsolutePath() 
					+ "_0.txt"), "utf-8"));
			Random random = new Random();
			List<HashSet<Integer>> keyValues = new ArrayList<HashSet<Integer>>();
			for(int i = 0; i < keyRatio.length; i++)
				keyValues.add(new HashSet<Integer>());
			int sizePerFirstKey = 1;
			for(int i = 0; i < keyRatio.length; i++)
				sizePerFirstKey *= keyRatio[i];
			int key = 0, fileName = 1, fileSize = tableSize / outputFileSize;
			for(int i = 0; i < tableSize;) {
				for(int j = 0; j < sizePerFirstKey; j++) {
					bw.write(key + splitStr);
					for(int k = 0; k < keyRatio.length; k++) {
						int tmp = Math.abs(random.nextInt());
						while(keyValues.get(k).contains(tmp))
							tmp = random.nextInt();
						bw.write(tmp + splitStr);
						keyValues.get(k).add(tmp);
					}
					for(int k = 0; k < attributes.size(); k++) {
						switch(attributes.get(k).type) {
						case 0 :
							bw.write(RanDataGene.getInteger(attributes.get(k).info1, 
									attributes.get(k).info2) + "");
							break;
						case 1 :
							bw.write(RanDataGene.getDouble(attributes.get(k).info1.intValue(), 
									attributes.get(k).info2.intValue()) + "");
							break;
						case 2 :
							bw.write(RanDataGene.getDecimal(attributes.get(k).info1.intValue(), 
									attributes.get(k).info2.intValue()) + "");
							break;
						case 3 :
							bw.write(RanDataGene.getDate(attributes.get(k).info1, 
									attributes.get(k).info2).getTime() + "");
							break;
						case 4 :
							bw.write(RanDataGene.getString(attributes.get(k).info1.intValue()) + 
									"");
							break;
						case 5 :
							bw.write(RanDataGene.getChar() + "");
							break;
						case 6 :
							bw.write(RanDataGene.getBoolean() + "");
							break;
						default :
							System.out.println("error data type!");
							return;
						}
						if(k < attributes.size() - 1)
							bw.write(splitStr);
					}
					bw.write("\n");
				}
				key++;
				i += sizePerFirstKey;
				for(int j = 0; j < keyRatio.length; j++)
					keyValues.get(j).clear();
				if(i >= fileSize * fileName) {
					bw.flush();
					if(i < tableSize)
						bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getAbsolutePath() + 
								"_" + fileName + ".txt"), "utf-8"));
					fileName++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(bw != null) bw.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
