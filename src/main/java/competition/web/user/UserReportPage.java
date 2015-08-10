package competition.web.user;

import competition.web.BaseTemplatePage;

public class UserReportPage extends BaseTemplatePage  {
	
	public UserReportPage() {
		super();
		setOutputMarkupId(true);

		UserReportPanel panel = new UserReportPanel("reportPanel", null);		
		
		add(panel);
	}		

}
