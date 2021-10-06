package detect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Global {
	public static ArrayList<Long> blockSeries = new ArrayList<>();

	public static HashMap<String, Flow> allFlowMp = new HashMap<>();

	// per block
	public static HashSet<String> blockFlows = new HashSet<>();
	public static HashMap<String, ArrayList<Double>> blockTable = new HashMap<>();

	// global
	public static ArrayList<HashMap<String, ArrayList<Double>>> globalTableList = new ArrayList<HashMap<String, ArrayList<Double>>>();
	public static int[] timeScope = Config.timeScope;
	public static int[] globalBlockIndexInitArray;
	public static ArrayList<Long> globalTableStartTimeList = new ArrayList<Long>();

	public Global() {
		for (int i = 0; i < timeScope.length; i++) {
			HashMap<String, ArrayList<Double>> tmp = new HashMap<String, ArrayList<Double>>();
			globalTableList.add(tmp);
			globalTableStartTimeList.add((long) 0);
		}
		globalBlockIndexInitArray = new int[timeScope.length];
		for (int i = 0; i < globalBlockIndexInitArray.length; i++) {
			globalBlockIndexInitArray[i] = 0;
		}
	}
}
