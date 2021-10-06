package detect;

import pcapreader.PacketInfo;
import pcapreader.PcapReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class DetectCore {
	public static int blockSeriesCurpos = 0;

	public static void readBlockSeries(String blockFilePath) throws IOException, ParseException {
		String tempString;
		BufferedReader readerPattern = new BufferedReader(new FileReader(blockFilePath));
		long curBlockTime = 0;
		long thres = Config.blockIntervalThres;
		while ((tempString = readerPattern.readLine()) != null) {
			long nextBlockTime = Long.valueOf(tempString.split(",")[0]);
			if ((nextBlockTime - curBlockTime) >= thres) {
				Global.blockSeries.add(nextBlockTime);
			}
			curBlockTime = nextBlockTime;
		}
		readerPattern.close();
	}

	public static int getBlockSeriesPos(long timestamp) {
		int begin = 0;
		int end = Global.blockSeries.size();
		while (true) {
			if (begin == end || (begin + 1) == end) {
				return begin;
			}
			int mid = (begin + end) / 2;
			if (timestamp == Global.blockSeries.get(mid)) {
				return mid;
			}
			if (timestamp < Global.blockSeries.get(mid)) {
				end = mid;
			} else {
				begin = mid;
			}
		}
	}

	public static boolean lenFilter(PacketInfo packetInfo) {
		int thres = Config.packetLenThres;
		if (packetInfo.length <= thres) {
			return true;
		} else {
			return false;
		}
	}

	public static void detect(String outputPath) throws IOException {
		PacketInfo packetInfo = null;
		// for each pcap, init bloclSeriesCurPos
		blockSeriesCurpos = 0;
		while ((packetInfo = PcapReader.getNextPacket()) != null) {
			if (lenFilter(packetInfo) == true) {
				continue;
			}
			// initialize the block_series_curpos
			if (blockSeriesCurpos == 0) {
				blockSeriesCurpos = getBlockSeriesPos(packetInfo.timestamp);
			}

			if (packetInfo.timestamp >= Global.blockSeries.get(blockSeriesCurpos + 1)) {
				// interval update
				intervalAlert(blockSeriesCurpos + 1, outputPath, packetInfo.timestamp);
				blockSeriesCurpos += 1;

			}
			// during the interval
			processPacket(packetInfo);

		}
	}

	public static void processPacket(PacketInfo packetInfo) {
		String sipdip = packetInfo.sip + "_" + packetInfo.dip;

		// add into all_flow_mp
		Flow flow = Global.allFlowMp.get(sipdip);
		if (flow == null) {
			flow = new Flow(packetInfo.sip, packetInfo.dip);
			flow.add(packetInfo.timestamp);
			Global.allFlowMp.put(sipdip, flow);
		} else {
			flow.add(packetInfo.timestamp);
		}

		// add into block_flows
		Global.blockFlows.add(sipdip);

	}

	public static void intervalAlert(int curPos, String outputPath, long timestamp) throws IOException {
		// init block_table
		Global.blockTable.clear();

		Similarity.initializeBlock();

		// generate block table
		for (String sipdip : Global.blockFlows) {
			ArrayList<Double> similarityList = Similarity.computeSimilarityByCdfPro(Global.allFlowMp.get(sipdip),
					blockSeriesCurpos);
			Global.blockTable.put(sipdip, similarityList);
		}

		// update global table
		updateGlobalTable(curPos);

		try {
			for (int i = 0; i < Global.timeScope.length; i++) {
				if (Global.globalTableStartTimeList.get(i) == ((long) 0)) {
					Global.globalBlockIndexInitArray[i] = curPos;
					Global.globalTableStartTimeList.set(i, timestamp);
					continue;
				}
				long globalTableStartTime = Global.globalTableStartTimeList.get(i);
				if (timestamp - globalTableStartTime > (Global.timeScope[i] * 60)) {
					int blockCount = curPos - Global.globalBlockIndexInitArray[i];
					String outTmpPath = outputPath.replace(".csv", "_" + Global.timeScope[i] + ".csv");
					outputTable2File(Global.globalTableList.get(i), Config.globalTableSizeThres, outTmpPath, curPos,
							blockCount);
					Global.globalTableList.get(i).clear();
					Global.globalTableStartTimeList.set(i, timestamp);
					Global.globalBlockIndexInitArray[i] = curPos;
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("error");
		}
		// flush block statistics
		Global.blockFlows.clear();
		Global.allFlowMp.clear();
	}

	public static void outputTable2File(HashMap<String, ArrayList<Double>> table, int size, String filePath, int pos,
			int blockCount) throws IOException {
		List<Map.Entry<String, ArrayList<Double>>> list = new ArrayList<>(table.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, ArrayList<Double>>>() {
			@Override
			public int compare(Map.Entry<String, ArrayList<Double>> o1, Map.Entry<String, ArrayList<Double>> o2) {
				return o2.getValue().get(0).compareTo(o1.getValue().get(0));
			}
		});

		File outFile = new File(filePath);
		if (!outFile.exists()) {
			outFile.createNewFile();
			FileWriter outTmp = new FileWriter(outFile, true);
			outTmp.write("pos,rank,flowName,sim\r\n");
			outTmp.flush();
			outTmp.close();
		}
		FileWriter out = new FileWriter(outFile, true);

		int cnt = 0;
		for (Map.Entry<String, ArrayList<Double>> e : list) {
			cnt++;
			out.write(pos + "," + cnt + "," + e.getKey() + ",");
			for (int i = 0; i < e.getValue().size(); i++) {
				if (i != e.getValue().size() - 1) {
					out.write(e.getValue().get(i) / blockCount + ",");
				} else {
					out.write(e.getValue().get(i) / blockCount + "\r\n");
				}
			}
			if (cnt >= size) {
				break;
			}
		}
		out.flush();
		out.close();
	}

	public static void updateGlobalTable(int curPos) {
		for (int i = 0; i < Global.timeScope.length; i++) {
			HashMap<String, ArrayList<Double>> globalTable = Global.globalTableList.get(i);
			LinkedList<SimilarityNode> list = new LinkedList<>();
			for (Map.Entry<String, ArrayList<Double>> entry : globalTable.entrySet()) {
				String sipdip = entry.getKey();

				ArrayList<Double> globalSimilarityList = entry.getValue();

				ArrayList<Double> blockSimilarityLsit = Global.blockTable.get(sipdip);

				if (blockSimilarityLsit != null) {
					// belong to block table and global table
					ArrayList<Double> tmp = new ArrayList<Double>();
					tmp.add(globalSimilarityList.get(0) + blockSimilarityLsit.get(0));
					list.add(new SimilarityNode(sipdip, tmp));
				} else {
					// only belong to global table
					ArrayList<Double> tmp2 = new ArrayList<Double>();
					tmp2.add(globalSimilarityList.get(0) - 1);
					list.add(new SimilarityNode(sipdip, tmp2));
				}
			}

			// only belong to block table
			for (Map.Entry<String, ArrayList<Double>> entry : Global.blockTable.entrySet()) {
				String sipdip = entry.getKey();
				ArrayList<Double> blockSimilarity = entry.getValue();
				if (!globalTable.containsKey(sipdip)) {
					list.add(new SimilarityNode(sipdip, blockSimilarity));
				}
			}

			// generate new global table, size
			Collections.sort(list);
			globalTable.clear();

			int cnt = 0;
			for (SimilarityNode node : list) {
				globalTable.put(node.sipdip, node.similarity);
				cnt++;
				if (cnt > Config.globalTableSizeThres) {
					break;
				}
			}
		}
	}
}
