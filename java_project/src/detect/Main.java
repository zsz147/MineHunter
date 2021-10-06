package detect;

import pcapreader.PcapReader;

public class Main {

	public static void main(String[] args) throws Exception {
		Config configInstance = new Config("./config");

		Global globalInstance = new Global();

		DetectCore.readBlockSeries(Config.blockFilePath);

		PcapReader.setPcapReader(Config.pcapInputPath);

		DetectCore.detect(Config.outputPath);
	}
}
