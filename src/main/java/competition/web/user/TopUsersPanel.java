package competition.web.user;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Filter;
import com.trg.search.Search;

import competition.domain.entity.Competition;
import competition.domain.entity.ScorePoints;
import competition.domain.entity.User;
import competition.domain.entity.UserChampion;
import competition.job.UserRankingJob;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.ClasspathFolderContentResourceReference;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.competition.FullCompetitionPage;

public class TopUsersPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;

	public TopUsersPanel(String id, final Competition competition, final int first) {
		super(id);
		
		PageParameters pp = new PageParameters();
		pp.add("competitionId", String.valueOf(competition.getId()));		
		pp.add("top", Boolean.TRUE);
		Link<Void> lnkTopUsersLink = new BookmarkablePageLink<Void>("fullTop", FullCompetitionPage.class, pp);	
		String title = competition.getName() + " - Top ";
		if (first == -1) {
			title += "Complet";
		} else {
			title += first;
		}
		lnkTopUsersLink.add(new Label("fullTopLabel", title));
		add(lnkTopUsersLink);

		LoadableDetachableModel<List<ScorePoints>> scoreProvider = new LoadableDetachableModel<List<ScorePoints>>() {
			@Override
			protected List<ScorePoints> load() {
				return businessService.getUsersScoreWithRankings(competition, first);
			}
		};

		ListView<ScorePoints> lstTopUsers = new ListView<ScorePoints>("row", scoreProvider) {
			@Override
			protected void populateItem(ListItem<ScorePoints> item) {
				IModel<ScorePoints> itemModel = item.getModel();
				item.add(new Label("index", String.valueOf(item.getIndex() + 1)));
				item.add(new ContextImage("rankImage", "img/" + getRankingImage(itemModel.getObject())));
				item.add(new Label("oldIndex", String.valueOf(itemModel.getObject().getPreviousRanking())));
				
				String user = itemModel.getObject().getUsername();
				String winner = businessService.getPlayoffWinner(competition);
				if (user.equals(winner)) {
					item.add(new ContextImage("winner", "img/cup.png").add(new SimpleTooltipBehavior("Castigator Playoff")));
				} else {
					item.add(new ContextImage("winner", "img/transparent.png"));
				}
				
				item.add(new ContextImage("avatar", "img/" + itemModel.getObject().getAvatarFile()));
				String team = itemModel.getObject().getTeam() + " (" + itemModel.getObject().getUsername() + ")";
				if (first == -1) {
					// for full top (on a separate page) we add also the champions stars
					int titles = generalService.count(new Search(UserChampion.class).addFilter(Filter.equal("username", itemModel.getObject().getUsername())));
	            	StringBuilder sb = new StringBuilder(); 	            		            		
	            	for (int i=0; i<titles; i++) {
	            		sb.append("*");
	            	}
	            	team += " " + sb.toString();
				} 
				item.add(new Label("team", team));				
				item.add(new Label("points", String.valueOf(itemModel.getObject().getPoints())).add(new SimpleTooltipBehavior("Total Puncte")));
				item.add(new Label("exact", String.valueOf(itemModel.getObject().getExactresults())).add(new SimpleTooltipBehavior("Rezultate Exacte")));
				
				Integer results1 = itemModel.getObject().getResults1();
				Integer resultsX = itemModel.getObject().getResultsX();
				Integer results2 = itemModel.getObject().getResults2();
				Integer results1X2 = itemModel.getObject().getResults1X2();
				
				int r1 = (results1 == null) ? 0 : results1;
				int rX = (resultsX == null) ? 0 : resultsX;
				int r2 = (results2 == null) ? 0 : results2;
				int r1X2 = (results1X2 == null) ? 0 : results1X2;
				
				if (first == -1) {
					item.add(new Label("results", String.valueOf(r1X2)).add(new SimpleTooltipBehavior("Rezultate Pronosticuri")));
					item.add(new Label("results1X2", String.valueOf(r1) + "/" + String.valueOf(rX) + "/" + String.valueOf(r2)).add(new SimpleTooltipBehavior("1/X/2")));
				} else {
					item.add(new Label("results", ""));
					item.add(new Label("results1X2", ""));
				}
			}

		};
		add(lstTopUsers);
		
		PageParameters pp2 = new PageParameters();		
		String topUser = scoreProvider.getObject().isEmpty() ? "" : scoreProvider.getObject().get(0).getUsername();		
		pp2.add("name", getMP3(topUser));
		// for eclipse run
		// pp2.add("name", "/" + getMP3(topUser));
		String url = RequestCycle.get().urlFor( new ClasspathFolderContentResourceReference(), pp2).toString();	
		
		String html5AudioStr = "<audio autoplay=\"autoplay\" controls=\"controls\"><source src=\"" + url + "\">Your browser does not support the audio element.</audio>";
		Label label = new Label("html5Audio", html5AudioStr);
		label.setEscapeModelStrings(false);
		add(label); 
		if ((first != -1) || (scoreProvider.getObject().isEmpty()) || (scoreProvider.getObject().get(0).getPoints() == 0)) {
			label.setVisible(false);
		}
	}
	
	private String getMP3(String userName) {
		if ("".equals(userName)) {
			return "winner.mp3";
		}
		User user = generalService.find(User.class, userName);
		String team = user.getTeam();
		return team + ".mp3";
	}
	
	private String getRankingImage(ScorePoints score) {
		if (score.getPreviousRanking() == null) {
			UserRankingJob job = new UserRankingJob();
			job.setGeneralService(generalService);
			job.setBusinessService(businessService);
			job.run();			
		}
		if (score.getPreviousRanking().equals(score.getCurrentRanking())) {
			return "stationary.png";
		} else if (score.getPreviousRanking() < score.getCurrentRanking()) {
			return "down.png";
		} else {
			return "up.png";
		}
	}

}
