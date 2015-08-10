package competition.web.stage;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

import competition.service.BusinessService;
import competition.web.BaseTemplatePage;

@AuthorizeInstantiation(Roles.ADMIN)
public class CreateStagesPage extends BaseTemplatePage  {

	@SpringBean
	private BusinessService businessService;
	
	public CreateStagesPage() {
		super();
		setOutputMarkupId(true);

		StagesPanel panel = new StagesPanel("stages");
		panel.setOutputMarkupId(true);
		
		add(panel);
	}		

}
