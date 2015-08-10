package competition.service.util;

/**
 *  4 points for exact score with at least 4 goals
 *  3 points for exact score with at most 3 goals
 *  1 point for guessing the prognostic
 * -1 point for guessing wrong the winner (except draw games) and ( |HS - GS| >= 3 or |UHS - UGS| >= 3 or (|HS - GS| >= 2 and |UHS - UGS| >= 2) )
 *    where HS = hosts score, GS = guests score, UHS = user hosts score, UGS = user guests score
 *  0 points for other
 * 
 * @author Mihai Dinca-Panaitescu
 *
 */
public class CleverAlgorithm implements Algorithm {

	@Override
	public int computeUserScore(Integer hs, Integer gs, Integer uhs, Integer ugs) {
		if ((hs == null) || (gs == null) || (uhs == null) || (ugs == null)) {
			return 0;
		}					
		
		// exact result
		if (hs.equals(uhs) && gs.equals(ugs)) {
			if (hs + gs >= 4) {
				return 4;
			} else {
				return 3;
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
			
		// host victory missed by far
		if ( (hs.intValue() > gs.intValue()) && (uhs.intValue() < ugs.intValue()) ) {
			int du = ugs.intValue() - uhs.intValue();
			int d = hs.intValue() - gs.intValue();
			if  ( (d >= 3) || (du >= 3) || ((du >= 2) && (d >= 2)) ) {
				 return -1;
			}
		}
		// guest victory missed by far
		if ( (hs.intValue() < gs.intValue()) && (uhs.intValue() > ugs.intValue()) ) {
			int du = uhs.intValue() - ugs.intValue();
			int d = gs.intValue() - hs.intValue();		
			if ( (d >= 3) || (du >= 3) || ((du >= 2) && (d >= 2)) ) {
				return -1;
			}
		}
		
		return 0;
	}

}
