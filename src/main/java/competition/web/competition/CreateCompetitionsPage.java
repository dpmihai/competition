package competition.web.competition;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateCompetitionsPage extends BaseTemplatePage  {	
	
	public CreateCompetitionsPage() {
		super();
		setOutputMarkupId(true);

		ManageCompetitionsPanel panel = new ManageCompetitionsPanel("competitions");
		panel.setOutputMarkupId(true);
		
		add(panel);
	}		

}

