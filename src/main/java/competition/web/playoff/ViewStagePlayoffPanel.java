package competition.web.playoff;

import java.io.File;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.domain.entity.GamePlayoff;
import competition.domain.entity.Stage;
import competition.domain.entity.StagePlayoff;
import competition.domain.entity.User;
import competition.playoff.StagePlayoffHelper;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.ClasspathFolderContentResourceReference;

public class ViewStagePlayoffPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public ViewStagePlayoffPanel(String id, final IModel<StagePlayoff> stage) {
		super(id, stage);
				
		Competition competition = (Competition)service.find(Competition.class, stage.getObject().getCompetitionId());
		
		Stage s = service.find(Stage.class, stage.getObject().getStageId());
		
		add(new Label("title", competition.getName() + "  :  " + stage.getObject().getName()));
		add(new Label("titleStage", " ( " + s.getName() + " ) "));																
		
		final WebMarkupContainer container = new WebMarkupContainer("view");
		container.setOutputMarkupPlaceholderTag(true);		
		
		DataView<GamePlayoff> gamesView = createGamesView(stage.getObject());
		container.add(gamesView);
		
		add(container);
		
		if (!stage.getObject().getName().equals(StagePlayoffHelper.TUR_PRELIMINAR) && (gamesView.getDataProvider().size() == 1)) {
			String winner = businessService.getPlayoffWinner(competition);
			if (winner == null) {
				add(new EmptyPanel("trophyPanel"));
				add(new Label("html5Audio",""));
			} else {
				add (new WinnerPanel("trophyPanel", new Model<String>(winner)));				
				PageParameters pp = new PageParameters();				
				pp.add("name", getMP3(winner));
				// for eclipse run
				// pp2.add("name", "/" + getMP3(winner));
				String url = RequestCycle.get().urlFor( new ClasspathFolderContentResourceReference(), pp).toString();		      
				String html5AudioStr = "<audio autoplay=\"autoplay\" controls=\"controls\"><source src=\"" + url + "\">Your browser does not support the audio element.</audio>";
				add(new Label("html5Audio", html5AudioStr).setEscapeModelStrings(false)); 
			}
		} else {
			add(new EmptyPanel("trophyPanel"));
			add(new Label("html5Audio",""));
		}
		
		
	}	
	
	private String getMP3(String userName) {
		User user = service.find(User.class, userName);
		String team = user.getTeam();
		return team + ".mp3";
	}
	
	private DataView<GamePlayoff> createGamesView(StagePlayoff stage) {		
		GamesPlayoffDataProvider dataProvider = new GamesPlayoffDataProvider(stage.getId());

		DataView<GamePlayoff> dataView = new DataView<GamePlayoff>("games", dataProvider) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<GamePlayoff> item) {

				ViewGamePlayoffPanel panel = new ViewGamePlayoffPanel("game", item.getModel());
				item.add(panel);				
			}

		};

		return dataView;
	}
	
	
	
	
	

}
