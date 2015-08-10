package competition.web.game;

import java.util.Date;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Game;
import competition.domain.entity.Team;
import competition.service.GeneralService;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class ViewGamePanel  extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	public ViewGamePanel(String id, IModel<Game> game, Date previousDate) {
		super(id, game);	
				
		if (DateUtil.sameDay(previousDate, game.getObject().getFixtureDate())) {
			add(new Label("fixtureDate", ""));
		} else {
			add(DateLabel.forDatePattern("fixtureDate", new Model<Date>(game.getObject().getFixtureDate()), "dd/MM E"));
		}
				
		Team hostTeam = (Team)service.find(Team.class, game.getObject().getHostsId());
		Team guestTeam = (Team)service.find(Team.class, game.getObject().getGuestsId());
		if (hostTeam == null) {
			System.out.println(">>>>> game=" + game);
		}
		String hostsName = hostTeam.getName();
		
		String guestsName = guestTeam.getName();
		
		add(new ContextImage("hostAvatar", "img/" + hostTeam.getAvatarFile()));
		add(new Label("hostName", hostsName));		
		add(new ContextImage("guestAvatar", "img/" + guestTeam.getAvatarFile()));
		add(new Label("guestName", guestsName));		
		
		TextField<Integer> hostsText = new TextField<Integer>("hostsScore", new Model<Integer>(game.getObject().getHostsScore()));		
		hostsText.setEnabled(false);
		add(hostsText);
		
		add(new Label("sep", " : "));
		
		TextField<Integer> guestsText = new TextField<Integer>("guestsScore", new Model<Integer>(game.getObject().getGuestsScore()));
		guestsText.setEnabled(false);
		add(guestsText);	
		
		String username = SecurityUtil.getLoggedUsername();
		
		if (username == null) {
			add(new EmptyPanel("score"));
		} else {
			add(new UserGamePanel("score", username, game));
		}
	}

}
