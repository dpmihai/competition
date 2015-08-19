package competition.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import competition.domain.entity.BestStagePerformer;
import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.JsonGame;
import competition.domain.entity.Rankings;
import competition.domain.entity.ScorePoints;
import competition.domain.entity.Team;
import competition.domain.entity.TeamRankings;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.competition.AverageStatisticsPanel;
import competition.web.util.DateUtil;

public class UserTotalScoreJob {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GeneralService generalService;

	@Autowired
	private BusinessService businessService;

	public void run() {
		long start = System.currentTimeMillis();
		log.info("*** UserTotalScore job started at " +  new Date() + " ...");
		
		autoEditPremierLeagueGames();		 
		computeTotalScoresAndPerformers();
		
		long end = System.currentTimeMillis();
		log.info("*** UserTotalScore job finished ... " + (end-start)/1000 + "  sec");
	}
	
	private void autoEditPremierLeagueGames() {
		log.info("  #### edit games");	
		Competition competition = (Competition)generalService.searchUnique(new Search(Competition.class).addFilterILike("name", "Premier League%").addFilterEqual("active", true));
		if (competition != null) {
			log.info("    > found competition : " + competition.getName());					
			autoEdit(competition);		
		}		
		log.info("  #### end edit");		
	}
	
	private void autoEdit(Competition competition) {
		Date start = businessService.getFirstStage(competition.getId()).getFixtureDate();		
		Date currentDate = new Date();		
		Date startDate =  DateUtil.floor(currentDate);
		Date endDate =  DateUtil.ceil(currentDate);
		/// take only the games that were not completed and were played (fixtureDate <= endDate)
		Search search = new Search(Game.class);	
		search.addFilterGreaterOrEqual("fixtureDate", start);		
		search.addFilterLessOrEqual("fixtureDate",endDate);		
		search.addFilterNull("hostsScore");
		search.addSortAsc("fixtureDate");
		List<Game> games = generalService.search(search);								
		log.info("    > " + games.size() + " games need to be completed till " + endDate);
		if (games.size() == 0) {
			return;
		}		
			
		int hour = DateUtil.getHour(currentDate);
//		if ((hour > 0) && (hour < 12)) {
//			log.info("    > stage will start after 12");
//		} else {
			log.info("    > stage in progress");
			Date from = games.get(0).getFixtureDate();
			List<JsonGame> jsonGames = businessService.readOnlineGames(from, endDate);
			log.info("    > read online games = " + jsonGames.size() + " between " + from + " - " + endDate);
			for (JsonGame jg : jsonGames) {
				log.info("      > " + jg.toString());
				if ("Finished".equals(jg.getStatus())) {
					// find the game in database and save the score
					boolean found = findAndSaveGame(jg, games, competition);
					if (!found) {
						log.info("    > game " + jg.toPrettyString() + " already edited.");
					}
				}
			}
//		}
		
	}		
	
	private boolean findAndSaveGame(JsonGame jg, List<Game> games, Competition competition) {		
		for (Game game : games) {
			Team hosts = generalService.find(Team.class, game.getHostsId());
			Team guests = generalService.find(Team.class, game.getGuestsId());	
			System.out.println("  -- " + hosts.getAbbreviation() + "  " + jg.getHosts()  + "   " + jg.getGuests());
			if ((hosts != null) && (guests != null) && (hosts.getAbbreviation() != null) && (guests.getAbbreviation() != null)) {
				if (hosts.getAbbreviation().equalsIgnoreCase(jg.getHosts()) && guests.getAbbreviation().equalsIgnoreCase(jg.getGuests())) {					
					game.setHostsScore(jg.getHostsScore());
					game.setGuestsScore(jg.getGuestsScore());
					generalService.merge(game);
					
					// something modified
					businessService.computeAndSetUserScore(game);
		            // reset average statistics to be computed again
		            AverageStatisticsPanel.resetAverageStatistics(competition.getName());
		            
					log.info("    > SAVED GAME : " + jg.toPrettyString());
					return true;
				} 
			} 
		}		
		return false;
	}		
	
	private void computeTotalScoresAndPerformers() {
		List<Competition> competitions = generalService.search(new Search(Competition.class));
		for (Competition competition : competitions) {
			if (competition.isActive()) {
				long start = System.currentTimeMillis();
				
				//***************************
				// if we change algorithm
				//***************************
				//businessService.computeAndSetUsersScore(competition);
				//AverageStatisticsPanel.resetAverageStatistics(competition.getName());
				
				List<ScorePoints> scores = businessService.computeUsersScore(competition);
				businessService.setUsersScore(scores);
				List<Rankings> rankings = businessService.computeUsersRanking(competition);
				businessService.setUsersRanking(rankings);
				Collection<TeamRankings> teamRankings = businessService.computeTeamRankings(competition);
				businessService.setTeamRankings(teamRankings);
								
				List<BestStagePerformer> performers = businessService.computeBestPerformer(scores);
				Search search = new Search(BestStagePerformer.class);
				search.addFilter(Filter.equal("competitionId", competition.getId()));	
				List<BestStagePerformer> oldPerformers = generalService.search(search);		
				
				List<BestStagePerformer> mergePerformers = new ArrayList<BestStagePerformer>(); 
				
				// must keep same record id for (user, competition)
				for (BestStagePerformer bp : oldPerformers) {
					for (BestStagePerformer nbp : performers) {
						if (bp.getUsername().equals(nbp.getUsername()) && bp.getCompetitionId().equals(nbp.getCompetitionId())) {
							nbp.setId(bp.getId());
							mergePerformers.add(bp);
						}
					}		
				}
				
				oldPerformers.removeAll(mergePerformers);
				generalService.remove(oldPerformers);				
				
				generalService.merge(performers);
				
				// lucky teams
				businessService.computeAndSetLuckyTeams(competition);
				
				// playoff
				playoff(competition);
				
				long end = System.currentTimeMillis();
				log.info("  * competition  : " + competition.getName() + " compute total scores took " +  (end-start)/1000 + "  sec");
			}	
		}
	}
	
	private void playoff(Competition competition) {
		businessService.initPlayoff(competition);
		businessService.savePlayoff(competition);
	}

	// for manual run
	
	public void setGeneralService(GeneralService generalService) {
		this.generalService = generalService;
	}

	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}
	
	

}
