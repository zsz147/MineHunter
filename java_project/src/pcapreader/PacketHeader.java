package pcapreader;

public class PacketHeader {
	public PacketHeader() {
	};

	public void setHeader(byte[] header) {
		System.arraycopy(header, 0, second, 0, 4);
		System.arraycopy(header, 4, microsecond, 0, 4);
		System.arraycopy(header, 8, caplen, 0, 4);
		System.arraycopy(header, 12, len, 0, 4);
	};

	public byte[] second = new byte[4];
	public byte[] microsecond = new byte[4];
	public byte[] caplen = new byte[4];
	public byte[] len = new byte[4];
}
