package competition.web.competition;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.service.GeneralService;
import competition.web.BaseTemplatePage;
import competition.web.rss.RssFeedPanel;

public class CompetitionNewsPage extends BaseTemplatePage {
	
	@SpringBean
	private GeneralService generalService;

	public CompetitionNewsPage() {
		super();

		PageParameters pp = getPageParameters();
		int competitionId = pp.get("competitionId").toInt();
		
		Competition competition = generalService.find(Competition.class, competitionId);
		
		add(new Label("competition", competition.getName() + " - Ultimele Stiri"));		

		// premier league
		//String url = "http://www.teamtalk.com/rss/1765";
		
		// euro
		//String url = "http://www.uefa.com/rssfeed/uefaeuro/rss.xml";
		
		// london
		//String url = "http://www.fifa.com/mensolympic/news/rss.xml";
		
		RssFeedPanel panel = new RssFeedPanel("rss", competition, 12);
		panel.setOutputMarkupId(true);

		add(panel);
	}

}
