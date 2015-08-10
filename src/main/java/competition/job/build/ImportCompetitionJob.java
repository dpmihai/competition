package competition.job.build;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;
import competition.service.GeneralService;

public class ImportCompetitionJob {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GeneralService generalService;
	private InputStream is;
	
	public ImportCompetitionJob(InputStream is) {
		this.is = is;
	}
	
	public void run() {
		long start = System.currentTimeMillis();
		log.info("*** Import competition job started at " + new Date() + " ...");
		importData();
		long end = System.currentTimeMillis();
		log.info("*** Import competition job finished ... " + (end - start) / 1000 + "  sec");
	}
	
	private void importData() {		
		ImportData data = ImportFile.importCompetition(is);
		
		Competition competition = data.getCompetition();
		competition = generalService.merge(competition);
		
		List<Team> teams = data.getTeams();
		// oldId, newId
		Map<Integer, Integer> teamIds = new HashMap<Integer, Integer>();
		for (Team team : teams) {
			team.setCompetitionId(competition.getId());
			int oldId = team.getId(); // this is the fake id used for games
			team.setId(null);         // reset fake id to null before save!!
			team = generalService.merge(team);
			teamIds.put(oldId, team.getId()); // put correct id
		}
		
		Map<Stage, List<Game>> stages = data.getStages();
		for (Stage stage : stages.keySet()) {			
			List<Game> games = stages.get(stage);
			stage.setCompetitionId(competition.getId());
			stage = generalService.merge(stage);
			for (Game game : games) {
				game.setStageId(stage.getId());
				// set correct team ids
				game.setHostsId(teamIds.get(game.getHostsId()));
				game.setGuestsId(teamIds.get(game.getGuestsId()));
				game = generalService.merge(game);
			}
		}
	}
	
	public void setGeneralService(GeneralService generalService) {
		this.generalService = generalService;
	}

}
