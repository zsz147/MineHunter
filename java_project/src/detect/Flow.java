package detect;

import java.util.ArrayList;

public class Flow {
	public String sip;
	public String dip;
	public ArrayList<Long> times = new ArrayList<>(128);

	public Flow(String sip, String dip) {
		this.sip = sip;
		this.dip = dip;
	}

	public void add(long timestamp) {
		this.times.add(timestamp);
	}

}
