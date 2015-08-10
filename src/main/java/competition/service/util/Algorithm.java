package competition.service.util;

public interface Algorithm {
	
	/**
	 * Compute user score for a game
	 * 
	 * @param hs hosts score
	 * @param gs guests score
	 * @param uhs user hosts scor 
	 * @param ghs user guests score
	 * 
	 * @return user points for a game
	 */
	public int computeUserScore(Integer hs, Integer gs, Integer uhs, Integer ghs);

}
