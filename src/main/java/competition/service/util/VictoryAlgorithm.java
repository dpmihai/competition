package competition.service.util;

/** 
 *  3 points for exact score with at most 3 goals
 *  n points for exact score with at least 4 goals, n=number of goals
 *  1 point for guessing the prognostic
 *  0 points for other
 * 
 * @author Mihai Dinca-Panaitescu
 *
 */
public class VictoryAlgorithm implements Algorithm {

	@Override
	public int computeUserScore(Integer hs, Integer gs, Integer uhs, Integer ugs) {
		if ((hs == null) || (gs == null) || (uhs == null) || (ugs == null)) {
			return 0;
		}					
		
		// exact result
		if (hs.equals(uhs) && gs.equals(ugs)) {
			if (hs + gs <= 3) {
				return 3;
			} else {
				return hs+gs;
			}
		}
		// draw result
		if ((hs.intValue() == gs.intValue()) && (uhs.intValue() == ugs.intValue())) {
			return 1;
		}
		// host victory result
		if ( (hs.intValue() > gs.intValue()) && (uhs.intValue() > ugs.intValue())) {
			return 1;
		}
		// guest victory result
		if ( (hs.intValue() < gs.intValue()) && (uhs.intValue() < ugs.intValue())) {
			return 1;
		}					
		
		return 0;
	}

}
