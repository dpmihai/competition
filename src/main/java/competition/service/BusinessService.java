package competition.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import competition.domain.entity.BestStagePerformer;
import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.JsonGame;
import competition.domain.entity.LuckyTeam;
import competition.domain.entity.ScorePoints;
import competition.domain.entity.SearchCompetition;
import competition.domain.entity.Stage;
import competition.domain.entity.StagePlayoff;
import competition.domain.entity.TeamRankings;
import competition.domain.entity.TitleValue;
import competition.domain.entity.User;
import competition.domain.entity.UserScore;
import competition.domain.entity.Rankings;

public interface BusinessService {
	
	public List<Competition> getCompetitions();
	
	public List<Competition> findCompetitions(SearchCompetition search);
	
	public Stage getFirstStage(int competitionId);
	
	public Stage getCurrentStage(int competitionId);	
	
	public Stage getPreviousStage(Stage stage);
	
	public Stage getNextStage(Stage stage);
	
	public List<Game> getGames(List<Integer> ids);
	
	public List<Game> getGames(Integer stageId);
	
	public List<UserScore> getUserScores(Stage stage, User user);
	
	public void saveUserScores(List<UserScore> scores);
	
	public int computeUserStageScore(Stage stage, User user);
	
	public int computeUserScore(UserScore score);	
	
	public void computeAndSetUserScore(Game game);	
	
	public void computeAndSetUserScore(List<Integer> gameIds);	
	
	public List<ScorePoints> computeUsersScore(Competition competition);
	
	public void computeAndSetUsersScore(Competition competition);
	
	public void setUsersScore(List<ScorePoints> scores);		
	
	public List<ScorePoints> getUsersScore(Competition competition, int first);
	
	public List<ScorePoints> getUsersScoreWithRankings(Competition competition, int first);
	
	public Competition getFirstCompetition();
	
	public void deleteCompetition(int competitionId);
	
	public void deleteUser(String userName);
	
	public void registerUsers(int competitionId, List<String> userNames);
	
	public List<User> getRegisteredUsers(int competitionId);
	
	public boolean isUserRegistered(int competitionId, String userName);
	
	public List<TitleValue> getGamesTicker(int competitionId);
	
    public TitleValue getLastWeekPerformer(int competitionId);
	
    public List<Rankings> computeUsersRanking(Competition competition);
    
    public void computeAndSetLuckyTeams(Competition competition);
    
    public List<Rankings> resetUsersRanking(Competition competition);
	
	public void setUsersRanking(List<Rankings> rankings);		
	
	public List<Rankings> getRankings(Competition competition);
	
    public Collection<TeamRankings> computeTeamRankings(Competition competition);
	
	public void setTeamRankings(Collection<TeamRankings> teamRankings);		
	
	public List<TeamRankings> getTeamRankings(int competitionId);
	
	public List<BestStagePerformer> computeBestPerformer(int competitionId);
	
	public List<BestStagePerformer> computeBestPerformer(List<ScorePoints> sp);
	
	public int getUserQuizPoints(int competitionId, String username);
	
	public List<JsonGame> readOnlineGames(Date start, Date end);
	
	public void updateRandomScores(List<UserScore> scores, int competitionId);
	
	public Date getCompetitionEndDate(int competitionId);
	
	public List<Competition> sortCompetitions(List<Competition> competitions);
	
	public void initPlayoff(Competition competition);
	
	public StagePlayoff getCurrentStagePlayoff(int competitionId);	
	
	public StagePlayoff getPreviousStagePlayoff(StagePlayoff stage);
	
	public StagePlayoff getNextStagePlayoff(StagePlayoff stage);
	
	public void savePlayoff(Competition competition);
	
	public String getPlayoffWinner(Competition competition);
	
}
