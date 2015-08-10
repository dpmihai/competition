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
@Table(name = "user_rankings")
public class Rankings implements Serializable {
		
	private static final long serialVersionUID = -2284523100846342692L;

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
	
	@Column(name = "current_ranking")
	private Integer currentRanking;
	
	@Column(name = "previous_ranking")
	private Integer previousRanking;		
	
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
