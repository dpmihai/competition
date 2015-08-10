package competition.web;


import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Filter;
import com.trg.search.Search;

import competition.domain.entity.Competition;
import competition.domain.entity.Stage;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.competition.CompetitionsPanel;
import competition.web.competition.SearchCompetitionPanel;
import competition.web.user.HallOfFameUsersPage;

public class HomePage extends BaseTemplatePage implements IHeaderContributor {

	private static final long serialVersionUID = 1L;


	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;

	public HomePage(PageParameters pageParameters) {
		super(pageParameters);
		
		add(new Label("welcome", "Bine ati venit la cele mai tari competitii sportive. Ghiciti cat mai multe scoruri si intrati in "));
		Link<Void> fameLink = new BookmarkablePageLink<Void>("fameTop", HallOfFameUsersPage.class);
		fameLink.add(new Label("fameLabel", "\"Hall of Fame\"!"));
		add(fameLink);
				
		CompetitionsPanel compPanel = new CompetitionsPanel("competitions");
		add(compPanel);
		
		add(new SearchCompetitionPanel("search"));
		
		
//		Search search = new Search(Competition.class);
//		search.addFilter(Filter.equal("id", 4));		
//		Competition competition = (Competition)generalService.searchUnique(search);
//		businessService.initPlayoff(competition);
//		businessService.savePlayoff(competition);
		

	}

}
