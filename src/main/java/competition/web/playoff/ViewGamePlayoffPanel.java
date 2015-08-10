package competition.web.playoff;

import java.util.Date;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.GamePlayoff;
import competition.domain.entity.User;
import competition.service.GeneralService;
import competition.web.common.panel.AbstractImageLabelPanel;

public class ViewGamePlayoffPanel  extends Panel {
	
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	public ViewGamePlayoffPanel(String id, final IModel<GamePlayoff> game) {
		super(id, game);	
		
		User homeUser = service.find(User.class, game.getObject().getHostUser());
		User guestUser = service.find(User.class, game.getObject().getGuestUser());
							
		Label homeLabel = new Label("hostName", homeUser.getTeam() + " (" + game.getObject().getHostUser() + ")");
		Label guestLabel = new Label("guestName", guestUser.getTeam() + " (" + game.getObject().getGuestUser() + ")");
		
		String classColorName = "green";
		if ((game.getObject().getHostsScore() != null) && (game.getObject().getGuestsScore() != null)) {
			if (game.getObject().getHostsScore() > game.getObject().getGuestsScore()) {
				homeLabel.add(AttributeAppender.append("class", classColorName));
			} else if (game.getObject().getHostsScore() < game.getObject().getGuestsScore()) {
				guestLabel.add(AttributeAppender.append("class", classColorName));
			} else {
				String winner = game.getObject().getShootoutWinner();
				if (winner != null) {
					if (winner.equals(game.getObject().getHostUser())) {
						homeLabel.add(AttributeAppender.append("class", classColorName));
					} else {
						guestLabel.add(AttributeAppender.append("class", classColorName));
					}
				}
			}
		}
		
		add(new ContextImage("hostAvatar", "img/" + homeUser.getAvatarFile()));
		add(homeLabel);		
		add(new ContextImage("guestAvatar", "img/" + guestUser.getAvatarFile()));
		add(guestLabel);		
		
		TextField<Integer> hostsText = new TextField<Integer>("hostsScore", new Model<Integer>(game.getObject().getHostsScore()));		
		hostsText.setEnabled(false);
		add(hostsText);
		
		add(new Label("sep", " : "));
		
		TextField<Integer> guestsText = new TextField<Integer>("guestsScore", new Model<Integer>(game.getObject().getGuestsScore()));
		guestsText.setEnabled(false);
		add(guestsText);	
		
		String winner = "";
		String shootoutImage = "img/transparent.png";
		if (game.getObject().getShootoutWinner() != null) {
			winner = "(11m)";
			shootoutImage = "img/referee_flag.png";
		}		
		add(new Label("winner",  winner));
		add(new ContextImage("shootoutImage", shootoutImage));
		
	}

}

