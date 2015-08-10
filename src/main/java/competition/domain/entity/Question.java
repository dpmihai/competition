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
@Table(name = "questions")
public class Question implements Serializable {
	
	public static String EMPTY = " - ";
	public static final int FRAGMENT_SIZE = 60;
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")	
	private Integer id;
	
	@Column(name = "competition_id")
	private Integer competitionId;
	
	@Column	
	private String question;

	@Column	
	private String response;		
	
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

	public String getQuestion() {
		return question;
	}
	
	public String getQuestionFragment() {
		if ((question != null) && (question.length() > FRAGMENT_SIZE)) {
			return question.substring(0, FRAGMENT_SIZE) + " ...";
		} else {
			return question;
		}
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getResponse() {
		if (response == null) {
			response = EMPTY;
		}
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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
