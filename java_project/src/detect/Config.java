package detect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public static String pcapInputPath;
	public static String blockFilePath;
	public static String outputPath;
	public static String blockOutputPath;
	public static int packetLenThres;
	public static int globalTableSizeThres;
	public static int blockIntervalThres;
	public static int[] timeScope;

	public Config(String path) throws IOException {
		Properties prop = new Properties();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
		prop.load(bufferedReader);
		pcapInputPath = prop.getProperty("pcapInputPath");
		blockFilePath = prop.getProperty("blockFilePath");
		outputPath = prop.getProperty("outPutPath");
		blockOutputPath = prop.getProperty("blockOutputPath");
		packetLenThres = Integer.valueOf(prop.getProperty("packetLenThres"));
		globalTableSizeThres = Integer.valueOf(prop.getProperty("globalTableSizeThres"));
		blockIntervalThres = Integer.valueOf(prop.getProperty("blockIntervalThres"));
		// timeScope
		String timeString = prop.getProperty("detectTimeScope");
		String[] items = timeString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		timeScope = new int[items.length];
		for (int i = 0; i < items.length; i++) {
			timeScope[i] = Integer.parseInt(items[i]);
		}
	}
}
