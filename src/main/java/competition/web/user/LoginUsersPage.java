package competition.web.user;

import competition.web.BaseTemplatePage;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

@AuthorizeInstantiation(Roles.ADMIN)
public class LoginUsersPage extends BaseTemplatePage {
	
	public LoginUsersPage() {
		super();		
		LoginUsersPanel panel = new LoginUsersPanel("top");
		panel.setOutputMarkupId(true);
		add(panel);
	}

}
