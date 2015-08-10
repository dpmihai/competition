package competition.web.competition;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;

import competition.domain.entity.Competition;

public class SearchCompetitionEvent {
	
	private final AjaxRequestTarget target;
	private List<Competition> competitions;
    
    public SearchCompetitionEvent(AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }

	public List<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(List<Competition> competitions) {
		this.competitions = competitions;
	}
        

}
