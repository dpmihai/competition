package competition.web.game;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Filter;
import com.trg.search.Search;

import competition.domain.entity.Game;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class UserGamePanel extends Panel {
		
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public UserGamePanel(String id, String username, IModel<Game> game) {
		super(id, game);	
		
		add(new Label("user", "-"));				
		
		UserScore score = (UserScore)service.searchUnique(
				new Search(UserScore.class).
				addFilter(Filter.equal("username", username)).
				addFilter(Filter.equal("gameId", game.getObject().getId())));
		
		String hostsScore = ((score == null) || (score.getHostsScore() == null)) ? "-" : String.valueOf(score.getHostsScore());		
		TextField<String> userHostsText = new TextField<String>("userHostsScore", new Model<String>(hostsScore));		
		userHostsText.setEnabled(false);				
		add(userHostsText);
				
		add(new Label("sep", " : "));
		
		String guestsScore = ((score == null) || (score.getGuestsScore() == null)) ? "-" : String.valueOf(score.getGuestsScore());	
		TextField<String> userGuestsText = new TextField<String>("userGuestsScore", new Model<String>(guestsScore));
		userGuestsText.setEnabled(false);		
		add(userGuestsText);		
						
		int points = 0;
		if (score != null) {
			points = score.getPoints();
		}	
		TextField<Integer> pointsText = new TextField<Integer>("points", new Model<Integer>(points));
		String cssClass = null;
		if (points == 1) {
			cssClass = "resultOne";
		} else if (points == 3) {
			cssClass = "resultThree";
		} else if (points == -1) {
			cssClass = "resultNegative";
		} else if (points >= 4) {
			cssClass = "resultFourUp";
		}
		if (cssClass != null) {
			pointsText.add(AttributeModifier.replace("class", cssClass));
		}
		pointsText.setEnabled(false);		
		add(pointsText);				
	}	
	
	
	

}
