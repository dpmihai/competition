package competition.web.user;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateUsersPage extends BaseTemplatePage  {	
	
	public CreateUsersPage() {
		super();
		setOutputMarkupId(true);

		ManageUsersPanel usersPanel = new ManageUsersPanel("users");
		usersPanel.setOutputMarkupId(true);		
		add(usersPanel);
		
		ManageChampionsPanel championsPanel = new ManageChampionsPanel("champions");
		championsPanel.setOutputMarkupId(true);		
		add(championsPanel);
	}		

}

