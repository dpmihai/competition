package competition.web.team;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.domain.entity.TeamRankings;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class TeamRankingPanel extends Panel {
		
	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;

	public TeamRankingPanel(String id, final int competitionId, boolean page) {
		super(id);
											
		Competition competition = generalService.find(Competition.class, competitionId);
		String title = "";
		if (page) {
			title = title +  competition.getName() + " - ";
		}
		title = title + "Clasament";
		add(new Label("fullTopLabel", title));

		LoadableDetachableModel<List<TeamRankings>> provider = new LoadableDetachableModel<List<TeamRankings>>() {
			@Override
			protected List<TeamRankings> load() {
				return businessService.getTeamRankings(competitionId);
			}
		};

		ListView<TeamRankings> lstTopTeams = new ListView<TeamRankings>("row", provider) {
			@Override
			protected void populateItem(ListItem<TeamRankings> item) {
				IModel<TeamRankings> itemModel = item.getModel();
				item.add(new Label("index", String.valueOf(item.getIndex() + 1)));	
				item.add(new ContextImage("avatar", "img/" + itemModel.getObject().getAvatarFile()));
				item.add(new Label("team", itemModel.getObject().getTeam()));
				item.add(new Label("played", String.valueOf(itemModel.getObject().getGamesPlayed())));
				item.add(new Label("win", String.valueOf(itemModel.getObject().getWin())));
				item.add(new Label("deuce", String.valueOf(itemModel.getObject().getDeuce())));
				item.add(new Label("lost", String.valueOf(itemModel.getObject().getLost())));
				item.add(new Label("gf", String.valueOf(itemModel.getObject().getGoalsFor())));
				item.add(new Label("ga", String.valueOf(itemModel.getObject().getGoalsAgainst())));
				item.add(new Label("difference", String.valueOf(itemModel.getObject().getDifference())));
				item.add(new Label("evolution", String.valueOf(itemModel.getObject().getEvolution())));
				item.add(new Label("points", String.valueOf(itemModel.getObject().getPoints())));
				
			}

		};
		add(lstTopTeams);
	}

}
