package competition.web.competition;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateQuestionsPage extends BaseTemplatePage  {	
	
	public CreateQuestionsPage() {
		super();
		setOutputMarkupId(true);

		ManageQuestionsPanel panel = new ManageQuestionsPanel("questions");
		panel.setOutputMarkupId(true);
		
		add(panel);
	}		

}
