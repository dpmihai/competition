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
@Table(name = "competitions")
public class Competition implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
	
	@Column
	private String name;
	
	@Column
	private String imageFile;
	
	@Column
	private boolean active;
	
	@Column
	private boolean finished;
	
	@Column
	private String rss;
	
	@Column
	private Boolean postponedGames;
	
	@Column
	private Integer playoffPoints;
	
	@Column(name="quiz_date")
	@Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.DAY)
	private Date quizDate;
	
	@Column(name="playoff_first_stage_id")
	private Integer playoffFirstStageId;
	
	@Column
	private Integer emailDays;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}		
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
		
	public String getRss() {
		return rss;
	}

	public void setRss(String rss) {
		this.rss = rss;
	}
		
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
		
	public Date getQuizDate() {
		return quizDate;
	}

	public void setQuizDate(Date quizDate) {
		this.quizDate = quizDate;
	}
	
	public Integer getPlayoffFirstStageId() {
		return playoffFirstStageId;
	}

	public void setPlayoffFirstStageId(Integer playoffFirstStageId) {
		this.playoffFirstStageId = playoffFirstStageId;
	}

	public boolean isPostponedGames() {
		if (postponedGames == null) {
			return true;
		} else {
			return postponedGames;
		}
	}

	public void setPostponedGames(boolean postponedGames) {
		this.postponedGames = postponedGames;
	}

	public Integer getPlayoffPoints() {
		if (playoffPoints == null) {
			return 10;
		}
		return playoffPoints;
	}

	public void setPlayoffPoints(Integer playoffPoints) {
		this.playoffPoints = playoffPoints;
	}
	
	public Integer getEmailDays() {
		if (emailDays == null) {
			return 4;
		}
		return emailDays;
	}

	public void setEmailDays(Integer emailDays) {
		this.emailDays = emailDays;
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
