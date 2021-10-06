package detect;

import java.util.*;

public class SimilarityNode implements Comparable {
	public String sipdip;
	public ArrayList<Double> similarity;

	public SimilarityNode(String sipdip, ArrayList<Double> similarity) {
		this.sipdip = sipdip;
		this.similarity = similarity;
	}

	public int compareTo(Object o) {
		SimilarityNode s = (SimilarityNode) o;
		if (this.similarity.get(0) < s.similarity.get(0)) {
			return 1;
		} else if (this.similarity.get(0) > s.similarity.get(0)) {
			return -1;
		} else
			return 0;
	}
}
