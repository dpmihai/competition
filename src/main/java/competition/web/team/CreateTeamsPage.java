package competition.web.team;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import competition.service.BusinessService;
import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateTeamsPage extends BaseTemplatePage  {

	@SpringBean
	private BusinessService businessService;
	
	public CreateTeamsPage() {
		super();
		setOutputMarkupId(true);

		TeamsPanel panel = new TeamsPanel("teams");
		panel.setOutputMarkupId(true);
		
		add(panel);
	}		

}
