package competition.web.team;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import competition.web.BaseTemplatePage;

public class TopTeamsPage extends BaseTemplatePage {
		
	public TopTeamsPage() {
		super();		

		PageParameters pp = getPageParameters();		
		int competitionId = pp.get("competitionId").toInt();		
		
		TeamRankingPanel panel = new TeamRankingPanel("top", competitionId, true);
		panel.setOutputMarkupId(true);

		add(panel);
	}

}
