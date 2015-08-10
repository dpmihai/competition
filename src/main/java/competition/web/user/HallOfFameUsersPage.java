package competition.web.user;

import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.service.GeneralService;
import competition.web.BaseTemplatePage;

public class HallOfFameUsersPage extends BaseTemplatePage {

	@SpringBean
	private GeneralService generalService;

	public HallOfFameUsersPage() {
		super();		
		HallOfFameUsersPanel panel = new HallOfFameUsersPanel("top");
		panel.setOutputMarkupId(true);
		add(panel);
	}

}
