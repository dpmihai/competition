package competition.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GeneralDAO;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.BestStagePerformer;
import competition.domain.entity.BonusPoints;
import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.GamePlayoff;
import competition.domain.entity.JsonGame;
import competition.domain.entity.LuckyTeam;
import competition.domain.entity.Question;
import competition.domain.entity.Rankings;
import competition.domain.entity.ScorePoints;
import competition.domain.entity.SearchCompetition;
import competition.domain.entity.Stage;
import competition.domain.entity.StagePlayoff;
import competition.domain.entity.Team;
import competition.domain.entity.TeamRankings;
import competition.domain.entity.TitleValue;
import competition.domain.entity.User;
import competition.domain.entity.UserPlayoff;
import competition.domain.entity.UserRegistration;
import competition.domain.entity.UserResponse;
import competition.domain.entity.UserScore;
import competition.playoff.StagePlayoffHelper;
import competition.service.BusinessService;
import competition.service.util.AlgorithmFactory;
import competition.web.CompetitionConfiguration;
import competition.web.util.DateUtil;
import competition.web.util.MathUtil;

@Service
public class BusinessServiceImpl implements BusinessService {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GeneralDAO generalDao;
	
	@Transactional(readOnly = true)
	public List<Competition> getCompetitions() {
		List<Competition> competitions = new ArrayList<Competition>();
		try {
			Search search = new Search(Competition.class);
			search.addSort(new Sort("name", true));			
			competitions = generalDao.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return competitions;
	}
	
	@Transactional(readOnly = true)
	public List<Competition> findCompetitions(SearchCompetition searchComp) {
		List<Competition> competitions = new ArrayList<Competition>();
		try {
			Search search = new Search(Competition.class);
			search.addFilter(Filter.ilike("name", "%" + searchComp.getName() + "%"));
//			search.addSort(new Sort("active", true));
//			search.addSort(new Sort("finished"));
//			search.addSort(new Sort("name"));						
			competitions = generalDao.search(search);
			sortCompetitions(competitions);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return competitions;
	}
	
	@Transactional(readOnly = true)
	public Competition getFirstCompetition() {
		Search search = new Search(Competition.class);
		search.addSortAsc("name");
		List<Competition> competitions = generalDao.search(search);
		if (competitions.size() == 0) {
			return null;
		}
		return competitions.get(0);
	}
	
	@Transactional(readOnly = true)
	public Stage getFirstStage(int competitionId) {
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));		
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		if (stages.isEmpty()) {
			// competition finished, show last stage
			search = new Search(Stage.class);
			search.addFilter(Filter.equal("competitionId", competitionId));			
			search.addSortDesc("fixtureDate");
			stages = generalDao.search(search);
			if (stages.isEmpty()) {
				// stages were not created yet
				return null;
			} else {
				return stages.get(0);
			}
			
		} else {
			return stages.get(0);
		}		
	}	
	
	@Transactional(readOnly = true)
	public Stage getCurrentStage(int competitionId) {
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));
		search.addFilter(Filter.greaterOrEqual("fixtureDate", DateUtil.floor(new Date())));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		if (stages.isEmpty()) {
			// competition finished, show last stage
			search = new Search(Stage.class);
			search.addFilter(Filter.equal("competitionId", competitionId));			
			search.addSortDesc("fixtureDate");
			stages = generalDao.search(search);
			if (stages.isEmpty()) {
				// stages were not created yet
				return null;
			} else {
				return stages.get(0);
			}
			
		} else {
			return stages.get(0);
		}		
	}	
	
	@Transactional(readOnly = true)
	public Stage getPreviousStage(Stage stage) {
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", stage.getCompetitionId()));
		search.addFilter(Filter.lessThan("fixtureDate", stage.getFixtureDate()));
		search.addSortDesc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		if (stages.isEmpty()) {
			return null;
		} else {
			return stages.get(0);
		}
	}
	
	@Transactional(readOnly = true)
	public Stage getNextStage(Stage stage) {
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", stage.getCompetitionId()));
		search.addFilter(Filter.greaterThan("fixtureDate", stage.getFixtureDate()));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		if (stages.isEmpty()) {
			return null;
		} else {
			return stages.get(0);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Game> getGames(List<Integer> ids) {
		List<Game> result = new ArrayList<Game>();
		for (Integer id : ids) {
			Game game = generalDao.find(Game.class, id);
			result.add(game);
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Game> getGames(Integer stageId) {
		List<Game> games = new ArrayList<Game>();
		if (stageId == -1) {
			return games;
		}
		try {
			Search search = new Search(Game.class);			
			search.addFilterEqual("stageId", stageId);
			search.addSort(new Sort("fixtureDate"));
			search.addSort(new Sort("id"));
			games = generalDao.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return games;
	}
	
	// get all existing and not existing scores in database for a stage and a user
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<UserScore> getUserScores(Stage stage, User user) {
		List<UserScore> scores = new ArrayList<UserScore>();
		List<Game> games = generalDao.search(
				new Search(Game.class).
				addFilter(Filter.equal("stageId", stage.getId())).
				addSort(new Sort("fixtureDate")).
			    addSort(new Sort("id")));		
		for (Game game : games) {			
			scores.add(findUserScore(game, user.getUsername()));
		}
		return scores;		
	}
		
	@SuppressWarnings("unchecked")
	private List<UserScore> getUserScores(Stage stage, List<String> userNames) {
		List<UserScore> scores = new ArrayList<UserScore>();
		List<Game> games = generalDao.search(
				new Search(Game.class).
				addFilter(Filter.equal("stageId", stage.getId())));				
		for (Game game : games) {			
			for (String userName : userNames) {
				scores.add(findUserScore(game, userName));
			}	
		}
		return scores;		
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public BonusPoints getBonusPoints(Stage stage, User user) {		
		BonusPoints result = (BonusPoints)generalDao.searchUnique(
				new Search(BonusPoints.class).
				addFilter(Filter.equal("stageId", stage.getId())).
				addFilter(Filter.equal("username", user.getUsername())));
		return result;
	}	
	
	@SuppressWarnings("unchecked")
	private List<BonusPoints> getBonusPoints(Stage stage, List<String> userNames) {
		List<BonusPoints> bonuses = new ArrayList<BonusPoints>();
		bonuses = generalDao.search(
				new Search(BonusPoints.class).
				addFilter(Filter.equal("stageId", stage.getId())).
				addFilter(Filter.in("username", userNames)));
		return bonuses;		
	}
	
	@Transactional
	public void saveUserScores(List<UserScore> scores) {
		for (UserScore score : scores) {
			generalDao.merge(score);
		}
	}
	
	@Transactional(readOnly = true)
	public int computeUserScore(UserScore score) {
		if (score == null) {
			return 0;
		}
		Game game = generalDao.find(Game.class, score.getGameId());
		Integer hs = game.getHostsScore();
		Integer gs = game.getGuestsScore();
		Integer uhs = score.getHostsScore();
		Integer ugs = score.getGuestsScore();
		return computeUserScore(hs, gs, uhs, ugs);
	}
		
	private int computeAndSetUserScore(Game game, User user) {
		UserScore score = findUserScore(game, user.getUsername());
		Integer hs = game.getHostsScore();
		Integer gs = game.getGuestsScore();
		Integer uhs = score.getHostsScore();
		Integer ugs = score.getGuestsScore();
		int points = computeUserScore(hs, gs, uhs, ugs);
		score.setPoints(points);
		generalDao.merge(score);				
		return points;
	}
	
	@Transactional
	public void computeAndSetUserScore(Game game) {		
		List<User> users = generalDao.search(new Search(User.class));		
		for (User user : users) {
			computeAndSetUserScore(game, user);
		}	
		if (isStageFinished(game.getStageId())) {
			Stage stage = generalDao.find(Stage.class, game.getStageId());
			computeAndSetBonusPoints(stage);
		}
	}
	
	@Transactional
	public void computeAndSetUserScore(List<Integer> gameIds) {
		for (Integer gameId : gameIds) {
			Game game = generalDao.find(Game.class, gameId);
			computeAndSetUserScore(game);
		}
	}
	
	@Transactional
	public List<BestStagePerformer> computeBestPerformer(int competitionId) {
		List<BestStagePerformer> result = new ArrayList<BestStagePerformer>();
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));
		search.addFilter(Filter.lessOrEqual("fixtureDate", new Date()));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		List<User> users = getRegisteredUsers(competitionId);
		for (Stage stage : stages) {
			for (User user : users) {
				ScorePoints sp = computeUserScore(stage, user);
				if (result.isEmpty()) {
					result.add(new BestStagePerformer(user.getUsername(), user.getTeam(), sp.getPoints(), stage.getName(), competitionId));
				} else {
					BestStagePerformer old = result.get(0);
					if (old.getPoints() < sp.getPoints()) {
						result.clear();
						result.add(new BestStagePerformer(user.getUsername(), user.getTeam(), sp.getPoints(), stage.getName(), competitionId));
					} else if (old.getPoints().intValue() == sp.getPoints().intValue()) {
						// look for user
						boolean foundUser = false;
						for (BestStagePerformer bp : result) {
							if (bp.getUsername().equals(user.getUsername())) {
								foundUser = true;
							}
						}
						if (!foundUser) {
							result.add(new BestStagePerformer(user.getUsername(), user.getTeam(), sp.getPoints(), stage.getName(), competitionId));
						}
					}
				}
			}
		}
		return result;
	}	
	
	@Transactional
	public List<BestStagePerformer> computeBestPerformer(List<ScorePoints> list) {
		List<BestStagePerformer> result = new ArrayList<BestStagePerformer>();
		for (ScorePoints sp : list) {
			if (sp.getBestStageScore() == 0) {
				return result;
			}
			if (result.isEmpty()) {
				result.add(new BestStagePerformer(sp.getUsername(), sp.getTeam(), sp.getBestStageScore(), sp.getBestStageName(), sp.getCompetitionId()));
			} else {
				BestStagePerformer old = result.get(0);
				if (old.getPoints().intValue() < sp.getBestStageScore().intValue()) {
					result.clear();
					result.add(new BestStagePerformer(sp.getUsername(), sp.getTeam(), sp.getBestStageScore(), sp.getBestStageName(), sp.getCompetitionId()));
				} else if (old.getPoints().intValue() == sp.getBestStageScore().intValue()) {					
					result.add(new BestStagePerformer(sp.getUsername(), sp.getTeam(), sp.getBestStageScore(), sp.getBestStageName(), sp.getCompetitionId()));					
				}
			}
		}
		return result;
	}
	
	@Transactional
	public int computeUserStageScore(Stage stage, User user) {
		ScorePoints sp = computeUserScore(stage, user);
		return sp.getPoints();
	}
	
	private ScorePoints computeUserScore(Stage stage, User user) {
		int points = 0;
		int exact = 0;
		int results1 = 0;
		int resultsX = 0;
		int results2 = 0;
		BonusPoints bonus = getBonusPoints(stage, user);
		int bonusPoints = (bonus == null) ? 0 : bonus.getPoints();
		List<UserScore> scores = getUserScores(stage, user);
		int total = scores.size();
		for (UserScore score : scores) {
			int val = score.getPoints().intValue();
			points += val;
			if (val >= 3) {
				exact++;
			}
			if ((score.getHostsScore() != null) && (score.getGuestsScore() != null)) {
				if (val > 0) {
					if (score.getHostsScore() > score.getGuestsScore()) {
						results1++;
					} else if (score.getHostsScore() < score.getGuestsScore()) {
						results2++;
					} else {
						resultsX++;
					}
				}
			}
		}		
		
		ScorePoints sp = new ScorePoints();
		sp.setCompetitionId(stage.getCompetitionId());
		sp.setUsername(user.getUsername());
		sp.setTeam(user.getTeam());
		sp.setAvatarFile(user.getAvatarFile());
		sp.setPoints(points);
		sp.setExactresults(exact);
		sp.setTotalresults(total);
		sp.setResults1(results1);
		sp.setResultsX(resultsX);
		sp.setResults2(results2);
		sp.setResults1X2(results1+resultsX+results2);
		sp.setBonusPoints(bonusPoints);
		return sp;
	}
	
	
	// take all stages till current date!
	private ScorePoints computeUserScore(Competition competition, User user) {
		if (competition == null) { 
			return null;
		}
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competition.getId()));				
		search.addFilter(Filter.lessOrEqual("fixtureDate", new Date()));
		List<Stage> stages = generalDao.search(search);
		int points = 0;
		int exact = 0;
		int total = 0;		
		int results1 = 0;
		int resultsX = 0;
		int results2 = 0;		
		int bonusPoints = 0;
		ScorePoints fullsp = new ScorePoints();
		fullsp.setBestStageScore(0);
		fullsp.setBestStageName("");		
		for (Stage stage : stages) {
			ScorePoints sp = computeUserScore(stage, user);
			points += sp.getPoints();
			exact += sp.getExactresults();
			total += sp.getTotalresults();	
			results1 += sp.getResults1();
			resultsX += sp.getResultsX();
			results2 += sp.getResults2();
			if (sp.getPoints() > fullsp.getBestStageScore()) {					
				fullsp.setBestStageScore(sp.getPoints());
				fullsp.setBestStageName(stage.getName());
			} 
			BonusPoints bp = getBonusPoints(stage, user);
			if (bp != null) {
				points += bp.getPoints();				
				bonusPoints += bp.getPoints();
			}
		}				
		points += getQuizPoints(competition.getId(), user.getUsername());
		String playoffWinner = getPlayoffWinner(competition);
		if (user.getUsername().equals(playoffWinner)) {
			points += competition.getPlayoffPoints();
		}			
		fullsp.setCompetitionId(competition.getId());
		fullsp.setUsername(user.getUsername());
		fullsp.setTeam(user.getTeam());
		fullsp.setAvatarFile(user.getAvatarFile());
		fullsp.setPoints(points);
		fullsp.setExactresults(exact);
		fullsp.setTotalresults(total);
		fullsp.setResults1(results1);
		fullsp.setResultsX(resultsX);
		fullsp.setResults2(results2);
		fullsp.setResults1X2(results1+resultsX+results2);
		fullsp.setBonusPoints(bonusPoints);
		return fullsp;
	}
	
	private int getQuizPoints(int competitionId, String username) {
		
		Search searchQ = new Search(Question.class);
		searchQ.addFilterEqual("competitionId", competitionId);		
		List<Question> questions = generalDao.search(searchQ);
		
		Search search = new Search(UserResponse.class);
		search.addFilterEqual("competitionId", competitionId);
		search.addFilterEqual("username", username);
		List<UserResponse> responses = generalDao.search(search);
		int quizPoints = 0;
		for (UserResponse response : responses) {
			for (Question q : questions) {
				if (q.getId().equals(response.getQuestionId())) {
					if ( (response.getResponse() != null) && (q.getResponse() != null) && 
						 (response.getResponse().equalsIgnoreCase(q.getResponse())) ) {
						quizPoints += 3;
					}
				}
			}
		}
		
		return quizPoints;
	}
	
	@Transactional(readOnly = true)
	public int getUserQuizPoints(int competitionId, String username) {
		return getQuizPoints(competitionId, username);
	}
	
	@Transactional(readOnly = true)
	public boolean isUserRegistered(int competitionId, String userName) {
		UserRegistration reg = (UserRegistration)generalDao.searchUnique(
				new Search(UserRegistration.class).
				addFilter(Filter.equal("username", userName)).
				addFilter(Filter.equal("competitionId", competitionId)));
		return reg != null;
	}
	
	@Transactional(readOnly = true)
	public List<ScorePoints> computeUsersScore(Competition competition) {	
		List<ScorePoints> result = new ArrayList<ScorePoints>();
		List<User> users = generalDao.search(new Search(User.class));
		for (User user : users) {
			if (isUserRegistered(competition.getId(), user.getUsername())) {
				ScorePoints sp = computeUserScore(competition, user);
				ScorePoints foundSp = (ScorePoints) generalDao.searchUnique(new Search(ScorePoints.class).addFilter(
						Filter.equal("username", sp.getUsername())).addFilter(
						Filter.equal("competitionId", sp.getCompetitionId())));
				if (foundSp == null) {
					foundSp = sp;
				} else {
					foundSp.setPoints(sp.getPoints());
					foundSp.setExactresults(sp.getExactresults());
					foundSp.setTotalresults(sp.getTotalresults());
					foundSp.setResults1(sp.getResults1());
					foundSp.setResultsX(sp.getResultsX());
					foundSp.setResults2(sp.getResults2());
					foundSp.setResults1X2(sp.getResults1X2());
					foundSp.setBestStageScore(sp.getBestStageScore());
					foundSp.setBestStageName(sp.getBestStageName());
					foundSp.setBonusPoints(sp.getBonusPoints());
				}
				result.add(foundSp);
			}
		}	
		return result;		
	}
	
	@Transactional
	public void computeAndSetUsersScore(Competition competition) {	
		List<Stage> stages = generalDao.search(new Search(Stage.class).addFilter(Filter.equal("competitionId", competition.getId())));
		for (Stage stage : stages) {
			List<Game> games = generalDao.search(new Search(Game.class).addFilter(Filter.equal("stageId", stage.getId())));
			for (Game game : games) {	
				computeAndSetUserScore(game);
			}
		}	
	}
	
	@Transactional
	public void setUsersScore(List<ScorePoints> scores) {		
		generalDao.merge(scores.toArray());
	}	
	
	@Transactional(readOnly = true)
	public List<ScorePoints> getUsersScore(Competition competition, int first) {					
		Search search = new Search(ScorePoints.class).				
				addFilter(Filter.equal("competitionId", competition.getId())).
				addSort(new Sort("points", true)).
				addSort(new Sort("exactresults", true)).
				addSort(new Sort("team"));
		if (first != -1) {
			search.setMaxResults(first);
		} 
		return generalDao.search(search);	
	}
	
	private List<ScorePoints> getUsersScore(int competitionId, List<String> userNames) {					
		Search search = new Search(ScorePoints.class).				
				addFilter(Filter.equal("competitionId", competitionId)).
				addFilter(Filter.in("username", userNames));		 
		return generalDao.search(search);	
	}
	
	@Transactional(readOnly = true)
	public List<ScorePoints> getUsersScoreWithRankings(Competition competition, int first) {					
		Search search = new Search(ScorePoints.class).				
				addFilter(Filter.equal("competitionId", competition.getId())).
				addSort(new Sort("points", true)).
				addSort(new Sort("exactresults", true)).
				addSort(new Sort("results1X2", true)).
				addSort(new Sort("results2", true)).
				addSort(new Sort("resultsX", true)).
				addSort(new Sort("results1", true)).
				addSort(new Sort("team"));
		if (first != -1) {
			search.setMaxResults(first);
		} 
		List<ScorePoints> scores = generalDao.search(search);
		List<Rankings> rankings = getRankings(competition);
		int pos = 1;
		for (ScorePoints score : scores) {
			Rankings ranking = getRanking(score.getUsername(), rankings);
			if (ranking != null) {
				//score.setCurrentRanking(ranking.getCurrentRanking());
				score.setCurrentRanking(pos);
				score.setPreviousRanking(ranking.getPreviousRanking());
			} else {
				score.setCurrentRanking(pos);
				score.setPreviousRanking(pos);
			}
			pos++;
		}
		return scores;
	}	
	
	private UserScore findUserScore(Game game, String userName) {
		UserScore score = (UserScore)generalDao.searchUnique(
				new Search(UserScore.class).
				addFilter(Filter.equal("username", userName)).
				addFilter(Filter.equal("gameId", game.getId())));
		if (score == null) {
			score = new UserScore();
			score.setGameId(game.getId());
			score.setUsername(userName);
			score.setPoints(0);
		}
		return score;
	}
	
	private int computeUserScore(Integer hs, Integer gs, Integer uhs, Integer ugs) {		
		return AlgorithmFactory.getAlgorithm().computeUserScore(hs, gs, uhs, ugs);
	}
	
	@Transactional
	public void deleteCompetition(int competitionId) {		
		List<ScorePoints> points = generalDao.search(new Search(ScorePoints.class).addFilter(Filter.equal("competitionId", competitionId)));
		generalDao.remove(points.toArray());
						
		List<Stage> stages = generalDao.search(new Search(Stage.class).addFilter(Filter.equal("competitionId", competitionId)));
		for (Stage stage : stages) {
			List<Game> games = generalDao.search(new Search(Game.class).addFilter(Filter.equal("stageId", stage.getId())));
			for (Game game : games) {
				List<UserScore> scores = generalDao.search(new Search(UserScore.class).addFilter(Filter.equal("gameId", game.getId())));
				generalDao.remove(scores.toArray());
			}
			generalDao.remove(games.toArray());
		}
		generalDao.remove(stages.toArray());
		
		List<Team> teams = generalDao.search(new Search(Team.class).addFilter(Filter.equal("competitionId", competitionId)));
		generalDao.remove(teams.toArray());
		
		List<UserRegistration> regs = generalDao.search(new Search(UserRegistration.class).addFilter(Filter.equal("competitionId", competitionId)));
		generalDao.remove(regs.toArray());
				
		List<LuckyTeam> luckyTeams = generalDao.search(new Search(LuckyTeam.class).addFilter(Filter.equal("competitionId", competitionId)));
		generalDao.remove(luckyTeams.toArray());
		
		List<StagePlayoff> playoffStages = generalDao.search(new Search(StagePlayoff.class).addFilter(
				Filter.equal("competitionId", competitionId)).addSort(new Sort("id", false)));
		if (!playoffStages.isEmpty()) {
			for (StagePlayoff sp : playoffStages) {				
				List<GamePlayoff> games = generalDao.search(new Search(GamePlayoff.class).addFilterEqual("stagePlayoffId", sp.getId()));
				generalDao.remove(games.toArray());
				
				List<UserPlayoff> users = generalDao.search(new Search(UserPlayoff.class).addFilterEqual("stagePlayoffId", sp.getId()));
				generalDao.remove(users.toArray());
			}
			generalDao.remove(playoffStages.toArray());
		}
		
		generalDao.removeById(Competition.class, competitionId);
	}
	
	@Transactional
	public void deleteUser(String userName) {
		List<ScorePoints> points = generalDao.search(new Search(ScorePoints.class).addFilter(Filter.equal("username", userName)));
		generalDao.remove(points.toArray());
		
		List<UserScore> scores = generalDao.search(new Search(UserScore.class).addFilter(Filter.equal("username", userName)));
		generalDao.remove(scores.toArray());
		
		List<UserRegistration> regs = generalDao.search(new Search(UserRegistration.class).addFilter(Filter.equal("username", userName)));
		generalDao.remove(regs.toArray());
		
		List<LuckyTeam> luckyTeams = generalDao.search(new Search(LuckyTeam.class).addFilter(Filter.equal("username", userName)));
		generalDao.remove(luckyTeams.toArray());
		
		generalDao.removeById(User.class, userName);
	}
	
	@Transactional
	public void registerUsers(int competitionId, List<String> userNames) {
		// first clear all registration for this competition
		 List<UserRegistration> regs = generalDao.search(new Search(UserRegistration.class).addFilter(Filter.equal("competitionId", competitionId)));
		 
		 // find unregistered users
		 List<String> unregistered = new ArrayList<String>();
		 for (UserRegistration reg : regs) {
			 boolean found = false;
			 for (String userName : userNames) {
				 if (userName.equals(reg.getUsername())) {
					 found = true;
					 break;
				 }
			 }
			 if (!found) {
				 unregistered.add(reg.getUsername());
			 }
		 }
		 
		 generalDao.remove(regs.toArray());
		 
		 // add all registrations
		 for (String name : userNames) {
			 UserRegistration reg = new UserRegistration();
			 reg.setCompetitionId(competitionId);
			 reg.setUsername(name);
			 generalDao.merge(reg);
		 }
		 
		 // delete ranking, total score and user scores for unregistered users
		 if (unregistered.size() > 0) {			 			 
			 List<Rankings> rankings = getRankings(competitionId, unregistered);
			 generalDao.remove(rankings.toArray());
			 List<ScorePoints> scorePoints = getUsersScore(competitionId, unregistered);
			 generalDao.remove(scorePoints.toArray());			
			 Search search = new Search(Stage.class);
			 search.addFilter(Filter.equal("competitionId", competitionId));				
			 List<Stage> stages = generalDao.search(search);
			 for (Stage stage : stages) {			 
				 List<UserScore> scores = getUserScores(stage, unregistered);
				 generalDao.remove(scores.toArray());
				 List<BonusPoints> bonuses = getBonusPoints(stage, userNames);
				 generalDao.remove(bonuses.toArray());
			 }			 
		 }
	}
	
	@Transactional(readOnly = true)
	public List<User> getRegisteredUsers(int competitionId) {
		List<User> users = generalDao.search(new Search(User.class).addSort(new Sort("username")));
		List<User> result = new ArrayList<User>();
		for (User user : users) {
			if (isUserRegistered(competitionId, user.getUsername())) {
				result.add(user);
			}
		}
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<TitleValue> getGamesTicker(int competitionId) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		List<TitleValue> result = new ArrayList<TitleValue>();
		Map<Date, LinkedList<String>> mapTitle = new HashMap<Date, LinkedList<String>>();
		Map<Date, LinkedList<String>> mapGame = new HashMap<Date, LinkedList<String>>();
		// last 7 days games
		Date start = DateUtil.addDays(new Date(), -7);
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));
		search.addFilter(Filter.lessOrEqual("fixtureDate", DateUtil.floor(new Date())));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		for (Stage stage : stages) {
			List<Game> games = generalDao.search(
					new Search(Game.class).
					addFilter(Filter.equal("stageId", stage.getId())).
					addSort(new Sort("fixtureDate")).
				    addSort(new Sort("id", true)));		
			for (Game game : games) {						
				if (DateUtil.after(game.getFixtureDate(), start)) {					
					if ((game.getHostsScore() != null) && (game.getGuestsScore() != null)) {
						String hostsName = ((Team)generalDao.find(Team.class, game.getHostsId())).getName();
						String guestsName = ((Team)generalDao.find(Team.class, game.getGuestsId())).getName();
						StringBuilder sb = new StringBuilder();						
						sb.append(sdf.format(game.getFixtureDate()));						
						sb.append(" (").append(stage.getName()).append(")");
						if (mapTitle.get(game.getFixtureDate()) == null) {
							mapTitle.put(game.getFixtureDate(), new LinkedList<String>());
						} 
						mapTitle.get(game.getFixtureDate()).push(sb.toString());						
												
						sb = new StringBuilder();
						sb.append(hostsName).append(" - ").append(guestsName);
						sb.append("  ").append(game.getHostsScore()).append(" : ").append(game.getGuestsScore());
						if (mapGame.get(game.getFixtureDate()) == null) {
							mapGame.put(game.getFixtureDate(), new LinkedList<String>());
						} 
						mapGame.get(game.getFixtureDate()).push(sb.toString());						
					}
				}
			}
		}
		List<Date> dates = new ArrayList<Date>(mapTitle.keySet());		
		Collections.sort(dates);
		for (Date date : dates) {
			LinkedList<String> titles = mapTitle.get(date);
			LinkedList<String> values = mapGame.get(date);					
			for (int i=0, size=titles.size(); i<size; i++) {
				result.add(new TitleValue(titles.pop(), values.pop()));
			}	
		}
		if (dates.size() == 0) {
			result.add(new TitleValue("Nu s-a disputat nici un meci in ultima saptamana.", ""));
		}
		return result;
	}
	
	private ScorePoints getLastWeekScore(int competitionId, User user) {			
		// last 7 days games
		Date start = DateUtil.addDays(new Date(), -7);
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));
		search.addFilter(Filter.lessOrEqual("fixtureDate", DateUtil.floor(new Date())));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		int points = 0;
		int exact = 0;
		int total = 0;		
		int results1 = 0;
		int resultsX = 0;
		int results2 = 0;
		int bonusPoints = 0;
		for (Stage stage : stages) {
			List<Game> games = generalDao.search(new Search(Game.class).addFilter(Filter.equal("stageId", stage.getId()))
					.addSort(new Sort("fixtureDate")).addSort(new Sort("id", true)));										
			for (Game game : games) {
				if (DateUtil.after(game.getFixtureDate(), start)) {
					if ((game.getHostsScore() != null) && (game.getGuestsScore() != null)) {												
						if (isUserRegistered(competitionId, user.getUsername())) {
							UserScore score = findUserScore(game, user.getUsername());
							int s = computeUserScore(score);
							points += s;
							if (s >= 3) {
								exact++;
							}
							if (score != null) {
								Integer hs = score.getHostsScore();
								Integer gs = score.getGuestsScore();
								if ((hs != null) && (gs != null)) {
									if (s > 0) {
										if (hs > gs) {
											results1++;
										} else if (hs < gs) {
											results2++;
										} else {
											resultsX++;
										}
									}
								}
							}
							BonusPoints bonus = getBonusPoints(stage, user);
							bonusPoints += (bonus == null) ? 0 : bonus.getPoints();
							total++;
						}
					}
				}
			}
			
		}
		ScorePoints sp = new ScorePoints();
		sp.setCompetitionId(competitionId);
		sp.setUsername(user.getUsername());
		sp.setTeam(user.getTeam());
		sp.setAvatarFile(user.getAvatarFile());
		sp.setPoints(points);
		sp.setExactresults(exact);
		sp.setTotalresults(total);
		sp.setResults1(results1);
		sp.setResultsX(resultsX);
		sp.setResults2(results2);
		sp.setResults1X2(results1+resultsX+results2);
		sp.setBonusPoints(bonusPoints);
		return sp;

	}
	
	@Transactional(readOnly = true)
	public TitleValue getLastWeekPerformer(int competitionId) {
		List<User> users = generalDao.search(new Search(User.class));
		int points = 0;
		int exactResults = 0;
		TitleValue performer = new TitleValue("", "");
		for (User user : users) {
			ScorePoints sp = getLastWeekScore(competitionId, user);
			if (points == 0) {
				performer.setTitle(user.getTeam() + " (" + user.getUsername() + ")");
				performer.setValue(sp.getPoints().toString());
				points = sp.getPoints();
				exactResults = sp.getExactresults();
			} else	if (sp.getPoints() > points) {
				performer.setTitle(user.getTeam() + " (" + user.getUsername() + ")");
				performer.setValue(sp.getPoints().toString());
				points = sp.getPoints();
				exactResults = sp.getExactresults();
			} else if (sp.getPoints() == points) {
				if (sp.getExactresults() > exactResults) {
					performer.setTitle(user.getTeam() + " (" + user.getUsername() + ")");
					performer.setValue(sp.getPoints().toString());
					points = sp.getPoints();
					exactResults = sp.getExactresults();
				}
			}
		}
		return performer;
	}
	
	// when a game is edited we update just current ranking
	@Transactional(readOnly = true)
    public List<Rankings> computeUsersRanking(Competition competition) {
		List<Rankings> rankings = getRankings(competition);
		List<ScorePoints> scores = getUsersScore(competition, -1);		
		int pos = 1;
		for (ScorePoints score : scores) {			
			Rankings ranking = getRanking(score.getUsername(), rankings);
			if (ranking == null) {
				ranking = new Rankings();
				ranking.setCompetitionId(competition.getId());
				ranking.setUsername(score.getUsername());
				ranking.setTeam(score.getTeam());
				ranking.setCurrentRanking(pos);
				ranking.setPreviousRanking(pos);
				rankings.add(pos-1, ranking);
			}			
			ranking.setCurrentRanking(pos);
			pos++;
		}		
		return rankings;
    }	
	
	@Transactional
	public void computeAndSetLuckyTeams(Competition competition) {
		Search search = new Search(LuckyTeam.class);
		search.addFilter(Filter.equal("competitionId", competition.getId()));
		List<LuckyTeam> luckyTeams = generalDao.search(search);
		generalDao.remove(luckyTeams.toArray());
		List<User> users = getRegisteredUsers(competition.getId());
		for (User user : users) {
			List<LuckyTeam> userLuckyTeams = computeLuckyTeams(competition, user);
			generalDao.merge(userLuckyTeams.toArray());
		}
	}
		
	private List<LuckyTeam> computeLuckyTeams(Competition competition, User user) {
		if (competition == null) { 
			return null;
		}
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competition.getId()));				
		search.addFilter(Filter.lessOrEqual("fixtureDate", new Date()));
		List<Stage> stages = generalDao.search(search);
		List<LuckyTeam> result = new ArrayList<LuckyTeam>();			
		for (int i=0; i<stages.size(); i++) {
			Stage stage = stages.get(i);
			List<LuckyTeam> stageLuckyTeams = computeLuckyTeams(stage, user);
			if (i == 0) {
				result = stageLuckyTeams;
			} else {
				for (LuckyTeam t : stageLuckyTeams) {
					LuckyTeam found = findLuckyTeam(t, result);
					found.setPoints(found.getPoints() + t.getPoints());
					found.setGoals(found.getGoals() + t.getGoals());
				}
			}
		}				
		return result;
	}
		
	private List<LuckyTeam> computeLuckyTeams(Stage stage, User user) {			
		List<UserScore> scores = getUserScores(stage, user);
		List<LuckyTeam> result = new ArrayList<LuckyTeam>();
		int total = scores.size();
		for (UserScore score : scores) {
			int val = score.getPoints().intValue();		
			Game game = generalDao.find(Game.class, score.getGameId());
			Team hosts = (Team)generalDao.find(Team.class, game.getHostsId());
			Team guests = (Team)generalDao.find(Team.class, game.getGuestsId());
			int points = 0;
			int hostGoals = 0;
			int guestGoals = 0;
			if (val >= 3) {								
				points = 1;
				hostGoals = score.getHostsScore();
				guestGoals = score.getGuestsScore();
			}		
			LuckyTeam luckyHost = new LuckyTeam();
			luckyHost.setCompetitionId(stage.getCompetitionId());
			luckyHost.setUsername(user.getUsername());
			luckyHost.setPoints(points);
			luckyHost.setGoals(hostGoals);
			luckyHost.setTeam(hosts.getName());
			result.add(luckyHost);
			
			LuckyTeam luckyGuest = new LuckyTeam();
			luckyGuest.setCompetitionId(stage.getCompetitionId());
			luckyGuest.setUsername(user.getUsername());
			luckyGuest.setPoints(points);
			luckyGuest.setGoals(guestGoals);
			luckyGuest.setTeam(guests.getName());
			result.add(luckyGuest);
		}
		return result;		
	}
	
	private LuckyTeam findLuckyTeam(LuckyTeam lucky, List<LuckyTeam> list) {
		for (LuckyTeam t : list) {
			if (lucky.getCompetitionId().equals(t.getCompetitionId()) && 
				lucky.getUsername().equals(t.getUsername()) &&
				lucky.getTeam().equals(t.getTeam())) {
				  return t;
			}
		}
		return null;
	}
				
	// after a week time inside UserRankingJob we update both current and previous ranking
	@Transactional(readOnly = true)
	public List<Rankings> resetUsersRanking(Competition competition) {
		List<Rankings> rankings = getRankings(competition);
		List<ScorePoints> scores = getUsersScore(competition, -1);		
		int pos = 1;
		for (ScorePoints score : scores) {			
			Rankings ranking = getRanking(score.getUsername(), rankings);
			if (ranking == null) {
				ranking = new Rankings();
				ranking.setCompetitionId(competition.getId());
				ranking.setUsername(score.getUsername());
				ranking.setTeam(score.getTeam());
				ranking.setCurrentRanking(pos);
				rankings.add(pos-1, ranking);
			}
			ranking.setPreviousRanking(ranking.getCurrentRanking().intValue());
			ranking.setCurrentRanking(pos);
			pos++;
		}		
		return rankings;
	}
	
	private Rankings getRanking(String userName, List<Rankings> rankings) {
		for (Rankings ranking :  rankings) {
			if (ranking.getUsername().equals(userName)) {
				return ranking;
			}
		}
		return null;
	}
	
    @Transactional
	public void setUsersRanking(List<Rankings> rankings) {
    	generalDao.merge(rankings.toArray());
	}
    
    @Transactional(readOnly = true)
	public List<Rankings> getRankings(Competition competition) {
		Search search = new Search(Rankings.class).
				addFilter(Filter.equal("competitionId", competition.getId())).
				addSort(new Sort("currentRanking")).
				addSort(new Sort("team"));		
		return generalDao.search(search);
	}
    
    private List<Rankings> getRankings(int competitionId, List<String> userNames) {
		Search search = new Search(Rankings.class).
				addFilter(Filter.equal("competitionId", competitionId)).
				addFilter(Filter.in("username", userNames));
		return generalDao.search(search);				
	}
    
    private List<LuckyTeam> getLuckyTeams(int competitionId, List<String> userNames) {
		Search search = new Search(LuckyTeam.class).
				addFilter(Filter.equal("competitionId", competitionId)).
				addFilter(Filter.in("username", userNames));
		return generalDao.search(search);				
	}
    
    @Transactional(readOnly = true)
    public Collection<TeamRankings> computeTeamRankings(Competition competition) {
    	Map<String, TeamRankings> map = new HashMap<String, TeamRankings>();
    	Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competition.getId()));
		search.addFilter(Filter.lessOrEqual("fixtureDate", DateUtil.floor(new Date())));
		search.addSortAsc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		List<Integer> stageIds = new ArrayList<Integer>();
		for (Stage stage : stages) {
			stageIds.add(stage.getId());
		}
		
		// take all games in date order (some games from a stage can be played in other day 
		// than the stage day if they are rescheduled)
		List<Game> games = generalDao.search(
				new Search(Game.class).
				addFilter(Filter.in("stageId", stageIds)).
				addSort(new Sort("fixtureDate")).
				addSort(new Sort("id", true)));
				
		for (Game game : games) {

			Team hostTeam = (Team) generalDao.find(Team.class, game.getHostsId());
			Team guestTeam = (Team) generalDao.find(Team.class, game.getGuestsId());
			String hostsName = hostTeam.getName();
			String guestsName = guestTeam.getName();

			TeamRankings hostRankings = map.get(hostsName);
			if (hostRankings == null) {
				hostRankings = initTeamRankings(hostTeam, competition.getId());
				map.put(hostsName, hostRankings);
			}
			if ((game.getHostsScore() != null) && (game.getGuestsScore() != null)) {
				updateTeamRankings(hostRankings, game, true);
			}

			TeamRankings guestRankings = map.get(guestsName);
			if (guestRankings == null) {
				guestRankings = initTeamRankings(guestTeam, competition.getId());
				map.put(guestsName, guestRankings);
			}
			if ((game.getHostsScore() != null) && (game.getGuestsScore() != null)) {
				updateTeamRankings(guestRankings, game, false);
			}

		}
		
		// initial top all teams have 0 points
		if (stages.isEmpty() || games.isEmpty()) {
			List<Team> teams = generalDao.search(new Search(Team.class).addFilter(Filter.equal("competitionId", competition.getId())));
			for (Team team : teams) {
				TeamRankings tr = initTeamRankings(team, competition.getId());
				map.put(team.getName(), tr);
			}
		}
		return map.values();
    }
	
    @Transactional
	public void setTeamRankings(Collection<TeamRankings> teamRankings) {      	
    	if (teamRankings.isEmpty()) {
    		return;
    	}
    	List<TeamRankings> oldRankings = getTeamRankings(teamRankings.iterator().next().getCompetitionId());
    	generalDao.remove(oldRankings.toArray());
		generalDao.merge(teamRankings.toArray());
	}
    
    private TeamRankings initTeamRankings(Team team, int competitionId) {
    	TeamRankings tr = new TeamRankings();
    	tr.setCompetitionId(competitionId);
    	tr.setTeam(team.getName());
    	tr.setAvatarFile(team.getAvatarFile());
    	tr.setEvolution("");
    	tr.setGamesPlayed(0);
    	tr.setWin(0);
    	tr.setDeuce(0);
    	tr.setLost(0);
    	tr.setGoalsFor(0);
    	tr.setGoalsAgainst(0);
    	tr.setPoints(0);
    	tr.setDifference(0);
    	return tr;
    }
	
	private void updateTeamRankings(TeamRankings tr, Game game, boolean host) {
		tr.setGamesPlayed(tr.getGamesPlayed()+1);	
		String s;
		if (host) {					
			tr.setGoalsFor(tr.getGoalsFor()+game.getHostsScore());
			tr.setGoalsAgainst(tr.getGoalsAgainst()+game.getGuestsScore());		
			tr.setDifference(tr.getDifference() + game.getHostsScore() - game.getGuestsScore());
			if ( game.getHostsScore() > game.getGuestsScore()) {				
				tr.setPoints(tr.getPoints() + 3);
				tr.setWin(tr.getWin()+1);
				s = "W";
			} else if ( game.getHostsScore().equals(game.getGuestsScore()) ) {
				tr.setPoints(tr.getPoints() + 1);
				tr.setDeuce(tr.getDeuce()+1);
				s = "D";
			} else {
				tr.setLost(tr.getLost()+1);
				s = "L";
			}			
		} else {
			tr.setGoalsFor(tr.getGoalsFor()+game.getGuestsScore());
			tr.setGoalsAgainst(tr.getGoalsAgainst()+game.getHostsScore());	
			tr.setDifference(tr.getDifference() + game.getGuestsScore() - game.getHostsScore());
			if ( game.getGuestsScore() > game.getHostsScore()) {				
				tr.setPoints(tr.getPoints() + 3);
				tr.setWin(tr.getWin()+1);
				s = "W";
			} else if ( game.getGuestsScore().equals(game.getHostsScore()) ) {
				tr.setPoints(tr.getPoints() + 1);
				tr.setDeuce(tr.getDeuce()+1);
				s = "D";
			} else {
				tr.setLost(tr.getLost()+1);
				s = "L";
			}
		}
		String evolution = tr.getEvolution();
		if (evolution.length() < 5) {
			evolution = evolution + s;
		} else {
			evolution = evolution.substring(1) + s;
		}
		tr.setEvolution(evolution);
	}
	
	@Transactional(readOnly = true)
	public List<TeamRankings> getTeamRankings(int competitionId) {
		return generalDao.search(
				new Search(TeamRankings.class).
				addFilter(Filter.equal("competitionId", competitionId)).
				addSort(new Sort("points", true)).
				addSort(new Sort("difference", true)).
			    addSort(new Sort("goalsFor", true)).
			    addSort(new Sort("team")));	
	}
	
	public List<JsonGame> readOnlineGames(Date start, Date end) {		
		String URL_NAME = "http://www.premierleague.com/en-gb.html";
		List<JsonGame> result = new ArrayList<JsonGame>();
		try {
			URL url = new URL(URL_NAME);
			if (url != null) {
				String proxyHost = CompetitionConfiguration.get().getProxyHost();
				Integer proxyPort = CompetitionConfiguration.get().getProxyPort();
				//System.out.println("**** proxyHost = " + proxyHost +  " port="+proxyPort);
				URLConnection connection ;
				if ((proxyHost == null) || proxyHost.trim().isEmpty()) {
					connection = url.openConnection();
				} else {					
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
					connection = url.openConnection(proxy);
				}
				if (connection != null) {
					InputStream stream = connection.getInputStream();
					if (stream != null) {
						BufferedReader br = new BufferedReader(new InputStreamReader(stream));
						String inputLine;
						StringBuilder sb = new StringBuilder();
						while ((inputLine = br.readLine()) != null) {
							sb.append(inputLine);
						}
						String html = sb.toString();
						createGamesFromHtml(result, html, start, end);						
					}
				}
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/*
	 * Example of html file to parse:
	 * 
	 * <li matchId="803439" class="megamenu-match POST_MATCH  ">
     *  
     *    <div class="megamenu-date">             
     *        <span field="date" widget="localeDate" timestamp="1456948800000" format="ddd d MMM HH:mm">Wed 2 Mar 20:00</span>                     
     *    </div>
     *    <a href="/en-gb/matchday/matches/2015-2016/epl.html/man-utd-vs-watford">
     *        <div class="megamenu-matchName">
     *            <span>MUN</span>
     *            <span class="megamenu-score">1-0</span>
     *            <span>WAT</span>
     *        </div>
     *    </a>
     *    <div class="megamenu-status">
     *        FT
     *    </div>
     *    ....
     *   </li> 
	 */
	private void createGamesFromHtml(List<JsonGame> result, String html, Date startDate, Date endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM HH:mm", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // hours:minutes are London time -> convert them to bucharest time
		
		Document doc = Jsoup.parse(html);							
		ListIterator<Element> gameNodes = doc.select("li.megamenu-match").listIterator();
		try {
			while (gameNodes.hasNext()) {							
				Element gameNode = gameNodes.next();
				String gameHtml = gameNode.html();			
												
				Element dateNode = gameNode.select("div.megamenu-date > span").first();	
				Date date;
				try {
					date = sdf.parse(dateNode.text());
				} catch (ParseException ex) {
					// dateNode.text() may be POSTPONED
					log.info("-- Date text is not in date format: " + dateNode.text());
					continue;
				}
				Date d = DateUtil.setCurrentYear(date);							
				
				if (DateUtil.insideInterval(d, startDate, endDate)) {		
					JsonGame jg = new JsonGame();
					jg.setDate(d);					
					
					Element statusNode = gameNode.select("div.megamenu-status").first();
					if ("FT".equals(statusNode.text())) {
						jg.setStatus("Finished");
					} else {
						jg.setStatus("Not Finished");
					}					
					
					ListIterator<Element> matchNodes = gameNode.select("div.megamenu-matchName > span").listIterator();						
					int i=0;
					while (matchNodes.hasNext()) {							
						Element matchNode = matchNodes.next();
						switch (i) {
							case 0: jg.setHosts(matchNode.text());									
									break;									
							case 1: String score = matchNode.text();
									if ("v".equals(score)) {
										jg.setHostsScore(0);
										jg.setGuestsScore(0);
									} else {
										String s[] = score.split("-");
										if (s.length == 2) {
											jg.setHostsScore(Integer.parseInt(s[0]));
											jg.setGuestsScore(Integer.parseInt(s[1]));
										} else {
											throw new Exception("Score format is wrong : " + score);
										}
									}									
									break;
							case 2: jg.setGuests(matchNode.text());								 	
									break;		
						}								
						i++;
					}
					result.add(jg);
				}														
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
//	public List<JsonGame> readOnlineGamesOld(Date start, Date end) {
//		
//		// https://statsfc.com/developers#api-results
//		//
//		// http://api.statsfc.com/[COMPETITION]/results.json?team=[TEAM_NAME]&from=[FROM_DATE]&to=[TO_DATE]&limit=[LIMIT]&key=[YOUR_API_KEY]
//		
//	    // team String, is the path of a team if you want only their results, e.g., manchester-city, liverpool
//	    // from Date, is the date to get results from, e.g., 2012-09-01
//	    // to Date, is the date to get results to, e.g., 2012-12-31
//	    // limit Integer, is the maximum number of results to return
//		// key=free (limited to 100 requests per day
//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String URL_NAME = "http://api.statsfc.com/premier-league/results.json?from=" + sdf.format(start) + "&to=" + sdf.format(end) + "&key=free";
//		String JSON_NOT_FOUND = "{\"error\":\"No results found\"}";		
//		
//		List<JsonGame> result = new ArrayList<JsonGame>();
//		
//		try {
//			URL url = new URL(URL_NAME);
//			if (url != null) {
////				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.16.7", 128));
////				URLConnection connection = url.openConnection(proxy);
//				URLConnection connection = url.openConnection();
//				if (connection != null) {
//					InputStream stream = connection.getInputStream();
//					if (stream != null) {
//						BufferedReader br = new BufferedReader(new InputStreamReader(stream));
//						String inputLine;
//						StringBuilder sb = new StringBuilder();
//						while ((inputLine = br.readLine()) != null) {
//							sb.append(inputLine);
//						}
//						String data = sb.toString();
//						if (JSON_NOT_FOUND.equals(data)) {
//							return result;
//						} else {
//							//System.out.println(data);							
//							//createGamesFromJsonWithRegex(result, data);
//							createGamesFromJson(result, data); 	
//						}						
//					}
//				}
//			}			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}
//	
//	// using regex is not ok if the json file is supposed to changes (like adding new properties)
//	private void createGamesFromJsonWithRegex(List<JsonGame> result, String data) {
//		String regex = "\"home\":\"([a-zA-Z ]+)\",\"homeshort\":\"[a-zA-Z ]+\",\"away_id\":[\\d]+,\"away\":\"([a-zA-Z ]+)\",\"awayshort\":\"[a-zA-Z ]+\",\"status\":\"([a-zA-Z]+)\",\"halftime\":\\[\\d,\\d\\],\"fulltime\":\\[(\\d),(\\d)\\],\"extratime\"";
//		Pattern patt = Pattern.compile(regex);
//		Matcher m = patt.matcher(data);
//		while(m.find()) {								
//			JsonGame game = new JsonGame();
//			game.setHosts(m.group(1));
//			game.setGuests(m.group(2));
//			game.setStatus(m.group(3)); // "Finished" means it ended
//			game.setHostsScore(Integer.parseInt(m.group(4)));
//			game.setGuestsScore(Integer.parseInt(m.group(5)));
//			result.add(game);
//		}
//	}
//	
//	private void createGamesFromJson(List<JsonGame> result, String data) throws ParseException {
//		JSONParser parser = new JSONParser();
//		JSONArray a = (JSONArray) parser.parse(data);
//		for (Object o : a) {
//			JsonGame game = new JsonGame();
//			JSONObject obj = (JSONObject) o;
//
//			String homeTeam = (String) obj.get("home");
//			game.setHosts(homeTeam);
//
//			String awayTeam = (String) obj.get("away");
//			game.setGuests(awayTeam);
//
//			String status = (String) obj.get("status");
//			game.setStatus(status);
//
//			JSONArray fulltime = (JSONArray) obj.get("fulltime");
//
//			for (int i=0, size=fulltime.size(); i<size; i++) {
//				if (i == 0) {
//					game.setHostsScore(((Long)fulltime.get(i)).intValue());
//				} else {
//					game.setGuestsScore(((Long)fulltime.get(i)).intValue());
//				}
//			}
//			result.add(game);
//		}
//	}
//	
//	private void readJson(String data) {
//		XStream xstream = new XStream(new JettisonMappedXmlDriver());
//		Map<String, Object> map2 = (Map<String, Object>) xstream.fromXML(data);
//
//		System.out.println("\nRead from json : ");
//		for (String key : map2.keySet()) {
//			System.out.print("key = " + key);
//			System.out.print("  value = ");
//			Object val = map2.get(key);
//			if (val instanceof Object[]) {
//				System.out.println(Arrays.asList((Object[]) val));
//			} else {
//				System.out.println(val);
//			}
//		}
//	}
	
	public void updateRandomScores(List<UserScore> scores, int competitionId) {
		List<TeamRankings> rankings = getTeamRankings(competitionId);				
		for (UserScore score : scores) {
			Game game = generalDao.find(Game.class, score.getGameId());
			Team hosts = (Team)generalDao.find(Team.class, game.getHostsId());
			Team guests = (Team)generalDao.find(Team.class, game.getGuestsId());
			
			int hr = getRanking(rankings, hosts.getName());
			int gr = getRanking(rankings, guests.getName());
			int[] rs = randomScore(hr, gr, 3);						
			
			score.setHostsScore(rs[0]);
			score.setGuestsScore(rs[1]);
		}
		
	}
	
	private int getRanking(List<TeamRankings> rankings, String teamName) {
		int pos = 0;
		for (TeamRankings tr : rankings) {
			pos++;
			if (tr.getTeam().equals(teamName)) {
				return pos;
			}
		}
		return 0;
	}
	
	private int[] randomScore(int hPos, int gPos, int maxGoals) {
		int[] result = new int[2];
		
		int d = hPos - gPos;
		
		if (d < -2) {
			result[0] = new Random().nextInt(maxGoals+1);
			if (result[0] <= 1) {
				result[1] = 0;
			} else {
				result[1] = new Random().nextInt(result[0]);
			}
		} else if (d > 2) {
			result[1] = new Random().nextInt(maxGoals+1);
			if (result[1] <= 1) {
				result[0] = 0;
			} else {
				result[0] = new Random().nextInt(result[1]);
			}			
		} else {
			result[0] = new Random().nextInt(maxGoals+1);
			result[1] = new Random().nextInt(maxGoals+1);
		}
		
		return result;		
	}
	
	public Date getCompetitionEndDate(int competitionId) {
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("competitionId", competitionId));			
		search.addSortDesc("fixtureDate");
		List<Stage> stages = generalDao.search(search);
		if (stages.isEmpty()) {
			// stages were not created yet
			return new Date();
		} else {
			return stages.get(0).getFixtureDate();
		}
	}
	
	@Transactional(readOnly = true)
	public List<Competition> sortCompetitions(List<Competition> competitions) {
		Collections.sort(competitions, new Comparator<Competition>() {

			@Override
			public int compare(Competition c1, Competition c2) {
				boolean test = false;
				if (c1.isActive()) {
					if (c2.isActive()) {
						test = true;
					} else {
						return -1;
					}
				}  else {
					if (c2.isActive()) {
						return 1;
					} else {
						test = true;
					}
				}
				if (test) {
					Date d1 = getCompetitionEndDate(c1.getId());
					Date d2 = getCompetitionEndDate(c2.getId());
					return -d1.compareTo(d2);
				}
				return -1;
			}								
		});
		return competitions;
	}
	
	@Transactional
	public void initPlayoff(Competition competition) {
		if (competition.getPlayoffFirstStageId() == null) {
			log.info("**** InitPlayoff Competition " + competition.getName() + " has not a playoff stage defined.");
			return;
		}
		
		Search searchP = new Search(StagePlayoff.class);
		searchP.addFilter(Filter.equal("stageId", competition.getPlayoffFirstStageId()));		
		StagePlayoff stageP = (StagePlayoff)generalDao.searchUnique(searchP);
		if (stageP != null) {
			log.info("**** InitPlayoff Competition " + competition.getName() + " already inited.");
			return;
		}
		
		Search search = new Search(Stage.class);
		search.addFilter(Filter.equal("id", competition.getPlayoffFirstStageId()));		
		Stage stage = (Stage)generalDao.searchUnique(search);
		
		// init playoff with 3 days before start
		Date currentDate = new Date();
		Date stageDate = stage.getFixtureDate();
		if (DateUtil.before(currentDate, stageDate)) {
			if (DateUtil.getNumberOfDays(currentDate, stageDate) > 4) {
				log.info("**** InitPlayoff Competition " + competition.getName() + " not inited yet. More than 4 days to it.");
				return;
			}
		}
		
		log.info("**** InitPlayoff Competition " + competition.getName() + " started ...");
		List<User> users = getRegisteredUsers(competition.getId());
		boolean preliminaryRound = !MathUtil.isPowerOf2(users.size());
		createPlayoffStage(competition, stage.getId(), users, preliminaryRound);
	}
	
	@Transactional(readOnly = true)
	public StagePlayoff getCurrentStagePlayoff(int competitionId) {
		Search search = new Search(StagePlayoff.class);
		search.addFilter(Filter.equal("competitionId", competitionId));	
		search.addSortDesc("id");
		List<StagePlayoff> list = (List<StagePlayoff>)generalDao.search(search);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	
	@Transactional(readOnly = true)
	public StagePlayoff getPreviousStagePlayoff(StagePlayoff stage) {
		Search search = new Search(StagePlayoff.class);
		search.addFilter(Filter.equal("competitionId", stage.getCompetitionId()));	
		search.addSortAsc("id");
		List<StagePlayoff> list = (List<StagePlayoff>)generalDao.search(search);
		for (int i=0, size=list.size(); i<size; i++) {
			if (list.get(i).getId().equals(stage.getId())) {
				if (i == 0) {
					return null;
				} else {
					return list.get(i-1);
				}
			}
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public StagePlayoff getNextStagePlayoff(StagePlayoff stage) {
		Search search = new Search(StagePlayoff.class);
		search.addFilter(Filter.equal("competitionId", stage.getCompetitionId()));	
		search.addSortAsc("id");
		List<StagePlayoff> list = (List<StagePlayoff>)generalDao.search(search);
		for (int i=0, size=list.size(); i<size; i++) {
			if (list.get(i).getId().equals(stage.getId())) {
				if (i == size-1) {
					return null;
				} else {
					return list.get(i+1);
				}
			}
		}
		return null;
	}
	
	@Transactional
	public void savePlayoff(Competition competition) {		
		StagePlayoff stagePlayoff = getCurrentStagePlayoff(competition.getId());
		if (stagePlayoff == null) {
			return;
		}
		log.info("**** Start Playoff Save.");
		Stage stage = generalDao.find(Stage.class, stagePlayoff.getStageId());
		
		// test if stage is completed
		List<Game> stageGames = getGames(stage.getId());
		boolean completed = true;
		// the stage associated to a playoff stage may not be populated with games
		// like in the case of a World Cup or EURO
		if (stageGames.size() == 0) {
			completed = false;
		}
		Date startStage = stage.getFixtureDate();
		for (Game g : stageGames) {
			if ((g.getHostsScore() == null) || (g.getGuestsScore() == null)) {
				if (competition.isPostponedGames()) {
					// if more than 3 days from stage start date (game was postponed)
					// we do not take that game into account for a stage playoff				
					if (DateUtil.getNumberOfDays(startStage, g.getFixtureDate()) <= 3) {
						completed = false;
						break;
					} 
				} else {
					completed = false;
				}
			}
		}
		if (!completed) {
			log.info("     Playoff did not finish yet.");
			return;
		}
		
		Search search = new Search(GamePlayoff.class);			
		search.addFilterEqual("stagePlayoffId", stagePlayoff.getId());			
		search.addSort(new Sort("id"));
		List<GamePlayoff> games = generalDao.search(search);
		if (games.get(0).getHostsScore() != null) {
			log.info("     Playoff already saved.");
			return;
		}
		List<User> qualifiedUsers = new ArrayList<User>();
		for (GamePlayoff gp : games) {
			User hostUser = generalDao.find(User.class, gp.getHostUser());
			int hostScore = computeUserStageScore(stage, hostUser);
			User guestUser = generalDao.find(User.class, gp.getGuestUser());
			int guestScore = computeUserStageScore(stage, guestUser);
			gp.setHostsScore(hostScore);
			gp.setGuestsScore(guestScore);
			if (hostScore > guestScore) {
				qualifiedUsers.add(hostUser);
			} else if (hostScore < guestScore) {
				qualifiedUsers.add(guestUser);
			} else if (hostScore == guestScore) {
				int draw = new Random().nextInt(2);
				User winner = (draw == 0) ? hostUser : guestUser;
				gp.setShootoutWinner(winner.getUsername());				
				qualifiedUsers.add(winner);
			}
			generalDao.save(gp);
		}	
		
		// in preliminary round we have some users automatically qualified
		if (stagePlayoff.getName().equals(StagePlayoffHelper.TUR_PRELIMINAR)) {
			Search searchU = new Search(UserPlayoff.class);			
			searchU.addFilterEqual("stagePlayoffId", stagePlayoff.getId());
			searchU.addFilterEqual("alreadyQualified", true);		
			searchU.addSort(new Sort("id"));
			List<UserPlayoff> users = generalDao.search(searchU);
			for (UserPlayoff up : users) {
				qualifiedUsers.add(generalDao.find(User.class, up.getUsername()));
			}
		}
		
		// create new playoff stage (preliminary round mat contain only one game)
		if (stagePlayoff.getName().equals(StagePlayoffHelper.TUR_PRELIMINAR) || (games.size() > 1)) {
			// see next stage which is used as following playoff stage
			Stage n = getNextStage(stage);
			n = getNextStage(n);									
			createPlayoffStage(competition, n.getId(), qualifiedUsers, false);
		}
		log.info("**** End Playoff Save. ");
		
	}
	
	private void createPlayoffStage(Competition competition, Integer stageId, List<User> users, boolean preliminaryRound) {
		log.info("     users = " + users);
		StagePlayoffHelper helper = new StagePlayoffHelper(users);
		
		StagePlayoff stagePlayoff = new StagePlayoff();
		stagePlayoff.setCompetitionId(competition.getId());
		stagePlayoff.setStageId(stageId);
		stagePlayoff.setName(helper.getStageName());		
		stagePlayoff = generalDao.save(stagePlayoff);
		
		helper.shuffleUsers(users);
		log.info("     shuffled users = " + users);
		int players = helper.getPreliminaryPlayers();	
		if (!preliminaryRound) {
			players = users.size();
		}
		for (int i=0, size=users.size(); i<size; i++) {
			UserPlayoff up = new UserPlayoff();
			up.setStagePlayoffId(stagePlayoff.getId());
			up.setUsername(users.get(i).getUsername());
			boolean qualified = (i < players) ? false : true;
			up.setAlreadyQualified(qualified);
			up = generalDao.save(up);
			log.info("       " + users.get(i).getUsername() + " -> qualified=" + qualified);
		}
		for (int i=0; i<players-1; i+=2) {
			GamePlayoff gp = new GamePlayoff();
			gp.setStagePlayoffId(stagePlayoff.getId());
			gp.setHostUser(users.get(i).getUsername());
			gp.setGuestUser(users.get(i+1).getUsername());
			log.info("     create game = " + gp.getHostUser() + " : " + gp.getGuestUser());
			gp = generalDao.save(gp);
		}
	}
	
	@Transactional(readOnly = true)
	public String getPlayoffWinner(Competition competition) {
		StagePlayoff stagePlayoff = getCurrentStagePlayoff(competition.getId());
		if (stagePlayoff == null) {
			return null;
		}
		Search search = new Search(GamePlayoff.class);			
		search.addFilterEqual("stagePlayoffId", stagePlayoff.getId());			
		search.addSort(new Sort("id"));
		List<GamePlayoff> games = generalDao.search(search);
		if (!stagePlayoff.getName().equals(StagePlayoffHelper.TUR_PRELIMINAR) && (games.size() == 1)) {
			GamePlayoff finalGame = games.get(0);
			return finalGame.getWinner();
		}
		return null;
	}
	
	private boolean isStageFinished(int stageId) {		
		List<Game> stageGames = getGames(stageId);
		boolean completed = true;
		for (Game g : stageGames) {
			if ((g.getHostsScore() == null) || (g.getGuestsScore() == null)) {
				completed = false;
				break;
			}
		}
		return completed;
	}
	
	private void computeAndSetBonusPoints(Stage stage) {	
		List<User> users = getRegisteredUsers(stage.getCompetitionId());
		Map<String, BonusPoints> bonuses = createBonusPoints(stage, users);				
		for (User user : users) {
			BonusPoints bp = getBonusPoints(stage, user);
			BonusPoints created = bonuses.get(user.getUsername());
			if (bp == null) {
				bp = created;
			} else {
				bp.setPoints(created.getPoints());
			}	
			generalDao.merge(bp);
		}		
	}
	
	private Map<String, BonusPoints> createBonusPoints(Stage stage, List<User> users) {
		
		Map<String, BonusPoints> result = new HashMap<String, BonusPoints>();
		Map<String, Integer> prognosticMap = new HashMap<String, Integer>();
		int max = 0;
		for (User user : users) {
			int total = 0;
			List<UserScore> scores = getUserScores(stage, user);
			for (UserScore score : scores) {
				if (score.getPoints() > 0) {
					total++;
				}
			}
			if (total > max) {
				max = total;
			}
			prognosticMap.put(user.getUsername(), total);
		}		
		for (User user : users) {
			BonusPoints bonus = new BonusPoints();
			bonus.setStageId(stage.getId());
			bonus.setUsername(user.getUsername());	
			bonus.setPoints(0);
			if (prognosticMap.get(user.getUsername()) == max) {
				// less than 3 prognostics, there are no bonus points
				if (max >= 3) {
					bonus.setPoints(3);
				}	
			}
			result.put(user.getUsername(), bonus);
		}
		return result;		
	}
	
}
