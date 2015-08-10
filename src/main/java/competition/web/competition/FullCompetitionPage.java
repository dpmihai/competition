package competition.web.competition;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Competition;
import competition.service.GeneralService;
import competition.web.BaseTemplatePage;

public class FullCompetitionPage extends BaseTemplatePage {
	
	private static final Logger LOG = LoggerFactory.getLogger(FullCompetitionPage.class);	
	
	@SpringBean
	private GeneralService generalService;

	private WebMarkupContainer container;	
	private BaseCompetitionPanel panel;
	
	private int competitionId = -1;
	
	public FullCompetitionPage(PageParameters pageParameters) {
		super(pageParameters);
		setOutputMarkupId(true);			
		
		competitionId = pageParameters.get("competitionId").toInt();				
		Competition competition = generalService.find(Competition.class, competitionId);	
		
		boolean top = pageParameters.get("top").toBoolean();
		
		panel = new BaseCompetitionPanel("basePanel", new Model<Competition>(competition), top);		
		add(panel);
		
	}	
	
	public void refresh(AjaxRequestTarget target) {
		panel.refresh(target);
	}

}
