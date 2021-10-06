package pcapreader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketParse {
	public static String bytesToIp(byte[] src) {
		return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
	}

	public static int bytesToInt(byte[] input, int offset, boolean littleEndian) {
		ByteBuffer buffer = ByteBuffer.wrap(input, offset, 4);
		if (littleEndian) {
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		return buffer.getInt();
	}

	public static long bytesToLong(byte[] input, int offset, boolean littleEndian) {
		ByteBuffer buffer = ByteBuffer.wrap(input, offset, 8);
		if (littleEndian) {
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		return buffer.getLong();
	}
}
