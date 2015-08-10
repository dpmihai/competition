package competition.service.util;

/**
 * 3 points for exact score
 * 1 point for guessing the prognostic
 * 0 points for other
 * 
 * @author Mihai Dinca-Panaitescu
 *
 */
public class BasicAlgorithm implements Algorithm {

	@Override
	public int computeUserScore(Integer hs, Integer gs, Integer uhs, Integer ghs) {
		
		if ((hs == null) || (gs == null) || (uhs == null) || (ghs == null)) {
			return 0;
		}					
		
		// exact result
		if (hs.equals(uhs) && gs.equals(ghs)) {			
			return 3;			
		}
		// draw result
		if ((hs.intValue() == gs.intValue()) && (uhs.intValue() == ghs.intValue())) {
			return 1;
		}
		// host victory result
		if ( (hs.intValue() > gs.intValue()) && (uhs.intValue() > ghs.intValue())) {
			return 1;
		}
		// guest victory result
		if ( (hs.intValue() < gs.intValue()) && (uhs.intValue() < ghs.intValue())) {
			return 1;
		}							
		return 0;
	}

}
