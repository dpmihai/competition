package competition.domain.entity;

import java.io.Serializable;
import java.io.StringWriter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MailData implements Serializable {
	
	private static final long serialVersionUID = -2020202922932937787L;
	
	private String competitionName;
	private String stageName;
	private User user;
	private int daysToStage;
	
	public MailData() {		
	}
	
	public String getCompetitionName() {
		return competitionName;
	}
	
	public void setCompetitionName(String competitionName) {
		this.competitionName = competitionName;
	}
	
	public String getStageName() {
		return stageName;
	}
	
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}	
	
	public int getDaysToStage() {
		return daysToStage;
	}

	public void setDaysToStage(int daysToStage) {
		this.daysToStage = daysToStage;
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
	
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, this);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Error : " + ex.getMessage();
		}
	}		

}
