package competition.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.JsonGame;
import competition.web.util.DateUtil;

public class PremierLeagueReader extends AbstractOnlineReader {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected String getURL() {
		return "https://www.premierleague.com/";
	}

	@Override
	protected void createGamesFromHtml(List<JsonGame> result, String html, Date startDate, Date endDate) {
		Document doc = Jsoup.parse(html);
		
		// single game currently playing
		ListIterator<Element> singleNodes = doc.select("div.singleMatch > a").listIterator();
		Date today = DateUtil.resetTime(new Date());	
		if (DateUtil.insideInterval(today, startDate, endDate)) {	
			addGames(singleNodes, today, result);
		}
		
		// games currently playing
		ListIterator<Element> moreNodes = doc.select("div > a.matchAbridged").listIterator();
		if (DateUtil.insideInterval(today, startDate, endDate)) {	
			addGames(moreNodes, today, result);
		}

		// games already played or which will be played
		ListIterator<Element> dayNodes = doc.select("div.day").listIterator();
		try {
			while (dayNodes.hasNext()) {							
				Element dayNode = dayNodes.next();
				String gameHtml = dayNode.html();			
												
				Element dateNode = dayNode.select("div.day > time").first();
				
				// Friday <strong>19 August</strong>
				String dateHtml = dateNode.html();
				print("dateHtml="+dateHtml);
				if ("".equals(dateHtml)) {
					continue;
				} else {
					Date date = getDate(dateHtml);
					print("date=" + date);
					
					if (DateUtil.insideInterval(date, startDate, endDate)) {		
						
						ListIterator<Element> gameNodes = dayNode.select("div.day > li > span.overview > a").listIterator();
						addGames(gameNodes, date, result);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void addGames(ListIterator<Element> gameNodes, Date date, List<JsonGame> result) {
		while (gameNodes.hasNext()) {
			
			JsonGame jg = new JsonGame();
			jg.setDate(date);
								
			Element gameNode = gameNodes.next();
			print("gameNode=" + gameNode.html());
			
			ListIterator<Element> teamNodes = gameNode.select("span.teamName").listIterator();
			
			Element hostTeamNode = teamNodes.next();
			jg.setHosts(hostTeamNode.html());
			print("host=" +hostTeamNode.html());
			
			Element guestTeamNode = teamNodes.next();
			jg.setGuests(guestTeamNode.html());
			print("guest=" + guestTeamNode.html());
									
			Element statusNode = gameNode.select("strong.minutes").first();
			print("status="+statusNode.html());
			if ((statusNode != null) && ("FT".equals(statusNode.html()))) {
				jg.setStatus("Finished");
			} else {
				jg.setStatus("Not Finished");
			}	
			
			Element scoreNode = gameNode.select("span.matchScoreContainer > span.score").first();
			if (scoreNode != null) {
				print("score=" + scoreNode.html());
				Integer[] scores = getScore(scoreNode.html());
				jg.setHostsScore(scores[0]);
				jg.setGuestsScore(scores[1]);
			}
			
			
			result.add(jg);
		}
	}
	
	// Friday <strong>19 August</strong>
	private Date getDate(String dateHtml) throws ParseException {
		String strongStart = "<strong>";
		String strongEnd = "</strong>";
		String dateFormat = "dd MMM";
		
		int index = dateHtml.indexOf(strongStart);
		int index2 = dateHtml.indexOf(strongEnd);
		String day = dateHtml.substring(index+strongStart.length(), index2);		
		print("day=" + day);
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date;
		try {
			date = sdf.parse(day);
			date = DateUtil.setCurrentYear(date);
			return date;
		} catch (ParseException ex) {
			// dateNode.text() may be POSTPONED
			log.info("--- Date text is not in date format: " + day);
			throw ex;
		}
	}
	
	// "2"<span>-</span>"1"
	private Integer[] getScore(String scoreHtml) {
		Integer[] result = new Integer[2];
		String spanStart = "<span>";
		String spanEnd = "</span>";
		int index = scoreHtml.indexOf(spanStart);
		int index2 = scoreHtml.indexOf(spanEnd);
		result[0] = Integer.parseInt(scoreHtml.substring(0, index));
		result[1] = Integer.parseInt(scoreHtml.substring(index2 + spanEnd.length(), scoreHtml.length()));
		return result;
	}	

}
