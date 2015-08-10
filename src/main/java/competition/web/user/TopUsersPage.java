package competition.web.user;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.service.GeneralService;
import competition.web.BaseTemplatePage;

public class TopUsersPage  extends BaseTemplatePage {
	
	@SpringBean
	private GeneralService generalService;
	
	public TopUsersPage() {
		super();		

		PageParameters pp = getPageParameters();		
		int competitionId = pp.get("competitionId").toInt();
		Competition competition = generalService.find(Competition.class, competitionId);
		
		TopUsersPanel panel = new TopUsersPanel("top", competition, -1);
		panel.setOutputMarkupId(true);

		add(panel);
	}

}
