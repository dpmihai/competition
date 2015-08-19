package competition.web.game;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Game;
import competition.domain.entity.Team;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class ViewAllUsersGamePanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private int competitionId;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public ViewAllUsersGamePanel(String id, IModel<Game> game, int competitionId, Date previousDate) {
		super(id, game);	
		this.competitionId = competitionId;
		if (DateUtil.sameDay(previousDate, game.getObject().getFixtureDate())) {
			add(new Label("fixtureDate", ""));
		} else {
			add(DateLabel.forDatePattern("fixtureDate", new Model<Date>(game.getObject().getFixtureDate()), "dd/MM E"));
		}
								
		Team hostTeam = (Team)service.find(Team.class, game.getObject().getHostsId());
		Team guestTeam = (Team)service.find(Team.class, game.getObject().getGuestsId());
		String hostsName = hostTeam.getName();
		String guestsName = guestTeam.getName();
		
		add(new ContextImage("hostAvatar", "img/" + hostTeam.getAvatarFile()));
		add(new Label("hostName", hostsName));		
		add(new ContextImage("guestAvatar", "img/" + guestTeam.getAvatarFile()));
		add(new Label("guestName", guestsName));		
		
		Integer hostsScore = game.getObject().getHostsScore();
		String hs = (hostsScore == null) ? "-" : String.valueOf(hostsScore);
		Integer guestsScore = game.getObject().getGuestsScore();
		String gs = (guestsScore == null) ? "-" : String.valueOf(guestsScore);
		add(new Label("officialScore", hs + ":" + gs));
		
		
		ListView<SmallUserGamePanel> listView = new ListView<SmallUserGamePanel>("usersScore", new ScoresModel(game)) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<SmallUserGamePanel> item) {
               item.add(item.getModelObject());
            }

        };
        listView.setReuseItems(true);
        
        String username = SecurityUtil.getLoggedUsername();		
		listView.setVisible(username != null);
		add(listView);
		
	}
	
    class ScoresModel extends LoadableDetachableModel<List<SmallUserGamePanel>> {
    	
		private static final long serialVersionUID = 1L;
		private IModel<Game> game;
		
		public ScoresModel(IModel<Game> game) {
			super();
			this.game = game;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<SmallUserGamePanel> load() {			
			try {
				if (SecurityUtil.getLoggedUser() == null) {
					return new ArrayList<SmallUserGamePanel>();
				} else {
					List<SmallUserGamePanel> panels = new ArrayList<SmallUserGamePanel>();
					List<User> users = businessService.getRegisteredUsers(competitionId);
					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return Collator.getInstance().compare(u1.getTeam(), u2.getTeam());						
						}
						
					});
					for (User user : users) {
						panels.add(new SmallUserGamePanel("score", user, game));
					}	
					return panels;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<SmallUserGamePanel>();
			}
		}

    }
		

}
