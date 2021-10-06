package pcapreader;

public class PacketInfo {
	public long timestamp;
	public String sip;
	public String dip;
	public int length;

	public PacketInfo(long timestamp, String sip, String dip, int length) {
		this.timestamp = timestamp;
		this.sip = sip;
		this.dip = dip;
		this.length = length;
	}
}
