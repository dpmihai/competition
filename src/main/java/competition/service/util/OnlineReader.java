package competition.service.util;

import java.util.Date;
import java.util.List;

import competition.domain.entity.JsonGame;

public interface OnlineReader {
	
	/**
	 * Read online games scores from a web url
	 * 
	 * @param start start date of games
	 * @param end end date of games
	 * 
	 * @return json array of games scores
	 */
	public List<JsonGame> readOnlineGames(Date start, Date end);
	
	/**
	 * Debug reader
	 * 
	 * @param debug true to debug reader
	 */
	public void setDebug(boolean debug);

}
