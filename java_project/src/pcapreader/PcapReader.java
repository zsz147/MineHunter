package pcapreader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class PcapReader {
	public static BufferedInputStream pcapBufferedReader;

	public static void setPcapReader(String pcapFile) throws IOException {
		pcapBufferedReader = new BufferedInputStream(new FileInputStream(pcapFile), 65536);

		byte[] pcapHeader = new byte[24];
		pcapBufferedReader.read(pcapHeader);
	}

	public static PacketInfo getNextPacket() throws IOException {
		byte[] buff = new byte[16];
		int len = 0;
		PacketHeader packetHeader = new PacketHeader();

		boolean isheader = true;
		while ((len = pcapBufferedReader.read(buff)) != -1) {
			if (len < 16) {
				break;
			}
			if (isheader == true) {
				packetHeader.setHeader(buff);
				int caplen = PacketParse.bytesToInt(packetHeader.caplen, 0, true);
				buff = new byte[caplen];
				isheader = false;
			} else {
				long timestamp = PacketParse.bytesToInt(packetHeader.second, 0, true);
				int length = PacketParse.bytesToInt(packetHeader.len, 0, true);

				byte[] etherHeader = new byte[14];
				byte[] ipHeader = new byte[20];
				System.arraycopy(buff, 0, etherHeader, 0, 14);
				if (etherHeader[12] == 0x08 && etherHeader[13] == 0x00) {
					System.arraycopy(buff, 14, ipHeader, 0, 20);
					byte[] srcIpByte = new byte[4];
					byte[] dstIpByte = new byte[4];
					System.arraycopy(ipHeader, 12, srcIpByte, 0, 4);
					System.arraycopy(ipHeader, 16, dstIpByte, 0, 4);
					String sip = PacketParse.bytesToIp(srcIpByte);
					String dip = PacketParse.bytesToIp(dstIpByte);
					int protocol = ipHeader[9];

					if (protocol == 6) {
						return new PacketInfo(timestamp, sip, dip, length);
					}
				}

				isheader = true;
				buff = new byte[16];
			}
		}

		return null;
	}
}
