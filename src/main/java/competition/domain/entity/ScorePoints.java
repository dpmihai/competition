package competition.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_total_scores")
public class ScorePoints implements Serializable {
		
	private static final long serialVersionUID = -2463630922587329042L;
	
	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
		
	@Column(name = "competition_id")
	private Integer competitionId;
		
	@Column	
	private String username;
	
	@Column	
	private String team;
	
	@Column	
	private String avatarFile;
	
	@Column	
	private Integer points;
	
	@Column	
	private Integer exactresults;
	
	@Column	
	private Integer results1;
	
	@Column	
	private Integer resultsX;
	
	@Column	
	private Integer results2;
	
	@Column	
	private Integer results1X2;
	
	@Column	
	private Integer totalresults;
	
	private transient Integer currentRanking;
	private transient Integer previousRanking;
	private transient Integer bestStageScore;
	private transient String bestStageName;
				
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCompetitionId() {
		return competitionId;
	}

	public void setCompetitionId(Integer competitionId) {
		this.competitionId = competitionId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Integer getExactresults() {
		return exactresults;
	}

	public void setExactresults(Integer exactresults) {
		this.exactresults = exactresults;
	}
		
	public Integer getResults1() {
		return results1;
	}

	public void setResults1(Integer results1) {
		this.results1 = results1;
	}

	public Integer getResultsX() {
		return resultsX;
	}

	public void setResultsX(Integer resultsX) {
		this.resultsX = resultsX;
	}

	public Integer getResults2() {
		return results2;
	}

	public void setResults2(Integer results2) {
		this.results2 = results2;
	}
		
	public Integer getResults1X2() {
		return results1X2;
	}

	public void setResults1X2(Integer results1x2) {
		results1X2 = results1x2;
	}

	public Integer getTotalresults() {
		return totalresults;
	}

	public void setTotalresults(Integer totalresults) {
		this.totalresults = totalresults;
	}
	
	public String getAvatarFile() {
		return avatarFile;
	}

	public void setAvatarFile(String avatarFile) {
		this.avatarFile = avatarFile;
	}
	
	public Integer getCurrentRanking() {
		return currentRanking;
	}

	public void setCurrentRanking(Integer currentRanking) {
		this.currentRanking = currentRanking;
	}

	public Integer getPreviousRanking() {
		return previousRanking;
	}

	public void setPreviousRanking(Integer previousRanking) {
		this.previousRanking = previousRanking;
	}
			
	public Integer getBestStageScore() {
		return bestStageScore;
	}

	public void setBestStageScore(Integer bestStageScore) {
		this.bestStageScore = bestStageScore;
	}

	public String getBestStageName() {
		return bestStageName;
	}

	public void setBestStageName(String bestStageName) {
		this.bestStageName = bestStageName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object object) {
		return EqualsBuilder.reflectionEquals(this, object);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	


}
