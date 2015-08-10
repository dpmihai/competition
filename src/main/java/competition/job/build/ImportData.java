package competition.job.build;

import java.util.List;
import java.util.Map;

import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;

public class ImportData {
	
	private Competition competition;
	private List<Team> teams;
	private Map<Stage, List<Game>> stages;
	
	public ImportData() {		
	}

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public Map<Stage, List<Game>> getStages() {
		return stages;
	}

	public void setStages(Map<Stage, List<Game>> stages) {
		this.stages = stages;
	}		

}
