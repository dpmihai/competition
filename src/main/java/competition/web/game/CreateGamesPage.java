package competition.web.game;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;


import competition.service.BusinessService;
import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateGamesPage extends BaseTemplatePage  {

	@SpringBean
	private BusinessService businessService;
	
	public CreateGamesPage() {
		super();
		setOutputMarkupId(true);

		GamesPanel panel = new GamesPanel("games");
		panel.setOutputMarkupId(true);
		
		add(panel);
	}		

}
