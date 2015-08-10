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
@Table(name = "user_scores")
public class UserScore implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
	
	@Column
	private String username;		
	
	@Column(name = "game_id")
	private Integer gameId;
	
	@Column(name = "hosts_score")
	private Integer hostsScore;
	
	@Column(name = "guests_score")
	private Integer guestsScore;
	
	@Column
	private Integer points = 0;
			
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public Integer getHostsScore() {
		return hostsScore;
	}

	public void setHostsScore(Integer hostsScore) {
		this.hostsScore = hostsScore;
	}

	public Integer getGuestsScore() {
		return guestsScore;
	}

	public void setGuestsScore(Integer guestsScore) {
		this.guestsScore = guestsScore;
	}		

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
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
