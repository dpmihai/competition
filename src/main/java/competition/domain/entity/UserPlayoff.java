package competition.domain.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name = "users_playoff")
public class UserPlayoff implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private String username;

	@Column(name = "stage_playoff_id")
	private Integer stagePlayoffId;

	@Column(name = "already_qualified")
	private boolean alreadyQualified;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getStagePlayoffId() {
		return stagePlayoffId;
	}

	public void setStagePlayoffId(Integer stagePlayoffId) {
		this.stagePlayoffId = stagePlayoffId;
	}

	public boolean isAlreadyQualified() {
		return alreadyQualified;
	}

	public void setAlreadyQualified(boolean alreadyQualified) {
		this.alreadyQualified = alreadyQualified;
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
