package competition.test;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import competition.domain.entity.JsonGame;
import competition.service.util.OnlineReader;
import competition.service.util.PremierLeagueReader;
import competition.web.CompetitionConfiguration;
import competition.web.util.DateUtil;

public class TestReader {
	
	public static void main(String[] args) {
		
		OnlineReader reader = new PremierLeagueReader() {
			public String getProxyHost() {
				return null;
			}
			
			public Integer getProxyPort() {
				return null;
			}			

		};
		reader.setDebug(true);
		
		Date start = DateUtil.asDate(LocalDate.of(2016, 12, 17));
		Date end = DateUtil.asDate(LocalDate.of(2016, 12, 18));
		List<JsonGame> jsonGames = reader.readOnlineGames(start, end);
		
		System.out.println("-------------------------------");
		for(JsonGame jg : jsonGames) {
			System.out.println(jg);
		}
		
	}

}
