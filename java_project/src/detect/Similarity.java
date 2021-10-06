package detect;

import java.util.*;

public class Similarity {
	// <flowCnt: cdf>
	public static HashMap<Integer, Double> flowCntCdf = new HashMap<>();
	public static HashMap<Integer, Double> sipCntCdf = new HashMap<>();

	public static void initializeBlock() {
		// <flowCnt: number of occurrences>
		HashMap<Integer, Integer> flowCntMp = new HashMap<>();
		HashMap<Integer, Integer> sipCntCp = new HashMap<>();

		List<Map.Entry<Integer, Integer>> flowCntList = new ArrayList<>(flowCntMp.entrySet());
		List<Map.Entry<Integer, Integer>> sipCntList = new ArrayList<>(sipCntCp.entrySet());

		Collections.sort(flowCntList, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		Collections.sort(sipCntList, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		flowCntCdf.clear();
		sipCntCdf.clear();
	}

	public static double probabilityDistance(int m, int n, int d) {
		double res = Math.pow((n - d) / ((double) n), m) - Math.pow((n - d - 1) / ((double) n), m);
		return res;
	}

	public static double disProbabilityCombinePacketCnt(int blockLen, int packetCnt, int distance) {
		if (distance < (blockLen) / 2) {
			double proSum = 0;
			for (int i = 0; i <= distance; i++) {
				double proTmp = probabilityDistance(packetCnt, blockLen, i);
				proSum += proTmp;
			}
			return 1 - proSum;
		} else {
			double proSum = 0;
			for (int i = blockLen - 1; i > distance; i--) {
				double proTmp = probabilityDistance(packetCnt, blockLen, i);
				proSum += proTmp;
			}
			return proSum;
		}
	}

	public static ArrayList<Double> computeSimilarityByCdfPro(Flow flow, int blockSeriesPos) {
		int timeDiff = getTimeDifference(flow, blockSeriesPos);
		int blockLength = (int) (Global.blockSeries.get(blockSeriesPos + 1) - Global.blockSeries.get(blockSeriesPos));
		double diff = (double) timeDiff / blockLength;
		int packetCnt = flow.times.size();
		double confidence = disProbabilityCombinePacketCnt(blockLength, packetCnt, timeDiff);

		ArrayList<Double> res = new ArrayList<Double>();
		double sim = confidence * (1.0 - diff);
		res.add(sim);
		return res;
	}

	public static int getTimeDifference(Flow flow, int blockSeriesPos) {
		long blockStart = Global.blockSeries.get(blockSeriesPos);
		int i = flow.times.size() - 1;
		for (; i >= 0; i--) {
			if (flow.times.get(i) < blockStart) {
				break;
			}
		}
		return (int) (flow.times.get(i + 1) - blockStart);
	}
}
