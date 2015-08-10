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
@Table(name = "games_playoff")
public class GamePlayoff implements Serializable {
		
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
	
	@Column(name = "stage_playoff_id")
	private Integer stagePlayoffId;
	
	@Column(name = "host_user")
	private String hostUser;
	
	@Column(name = "guest_user")
	private String guestUser;
	
	@Column(name = "hosts_score")
	private Integer hostsScore;
	
	@Column(name = "guests_score")
	private Integer guestsScore;
	
	@Column(name = "shootout_winner")
	private String shootoutWinner;
			
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getStagePlayoffId() {
		return stagePlayoffId;
	}

	public void setStagePlayoffId(Integer stagePlayoffId) {
		this.stagePlayoffId = stagePlayoffId;
	}

	public String getHostUser() {
		return hostUser;
	}

	public void setHostUser(String hostUser) {
		this.hostUser = hostUser;
	}

	public String getGuestUser() {
		return guestUser;
	}

	public void setGuestUser(String guestUser) {
		this.guestUser = guestUser;
	}

	public String getShootoutWinner() {
		return shootoutWinner;
	}

	public void setShootoutWinner(String shootoutWinner) {
		this.shootoutWinner = shootoutWinner;
	}
	
	public String getWinner() {
		if ((hostsScore == null) || (guestsScore == null)) {
			return null;
		}
		if (hostsScore > guestsScore) {
			return hostUser;
		} else if (hostsScore < guestsScore) {
			return guestUser;
		} else {
			return shootoutWinner;
		}
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
