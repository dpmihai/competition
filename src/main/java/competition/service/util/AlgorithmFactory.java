package competition.service.util;

import java.util.Arrays;
import java.util.List;

public class AlgorithmFactory {
	
	public static final int BASIC = 1;
	public static final int CLEVER = 2;
	public static final int VICTORY = 3;
	
	public static final int USED = VICTORY;
	
	private static Algorithm getAlgorithm(int type) {
		switch (type) {
			case 1 : return new BasicAlgorithm();
			case 2 : return new CleverAlgorithm();
			case 3 : return new VictoryAlgorithm();
			default: return new BasicAlgorithm();
		}
	}
	
	public static Algorithm getAlgorithm() {
		return getAlgorithm(USED);
	}
	
	public static List<Integer> getAlgorithms() {
		return Arrays.asList(new Integer[] {BASIC, CLEVER, VICTORY});
	}

}
