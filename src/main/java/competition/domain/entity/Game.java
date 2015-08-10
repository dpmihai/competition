package competition.domain.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Resolution;

@Entity
@Table(name = "games")
public class Game implements Serializable {
		
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
	
	@Column(name = "stage_id")
	private Integer stageId;
	
	@Column(name = "hosts_id")
	private Integer hostsId;
	
	@Column(name = "guests_id")
	private Integer guestsId;
	
	@Column(name = "hosts_score")
	private Integer hostsScore;
	
	@Column(name = "guests_score")
	private Integer guestsScore;
	
	@Column(name="fixture_date")
	@Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.DAY)
	private Date fixtureDate;
			
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStageId() {
		return stageId;
	}

	public void setStageId(Integer stageId) {
		this.stageId = stageId;
	}

	public Integer getHostsId() {
		return hostsId;
	}

	public void setHostsId(Integer hostsId) {
		this.hostsId = hostsId;
	}

	public Integer getGuestsId() {
		return guestsId;
	}

	public void setGuestsId(Integer guestsId) {
		this.guestsId = guestsId;
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

	public Date getFixtureDate() {
		return fixtureDate;
	}

	public void setFixtureDate(Date fixtureDate) {
		this.fixtureDate = fixtureDate;
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
