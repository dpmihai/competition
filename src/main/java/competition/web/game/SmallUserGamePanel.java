package competition.web.game;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import competition.domain.entity.Game;
import competition.domain.entity.User;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.service.GeneralService;

public class SmallUserGamePanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	@SpringBean
	private BusinessService businessService;	

	public SmallUserGamePanel(String id, User user, IModel<Game> game) {
		super(id, game);				
		
		UserScore score = (UserScore) service.searchUnique(new Search(UserScore.class).addFilter(
				Filter.equal("username", user.getUsername())).addFilter(Filter.equal("gameId", game.getObject().getId())));

		String hostsScore = ((score == null) || (score.getHostsScore() == null)) ? "-" : String.valueOf(score.getHostsScore());		
		String guestsScore = ((score == null) || (score.getGuestsScore() == null)) ? "-" : String.valueOf(score.getGuestsScore());	
		add(new Label("score", hostsScore+":"+guestsScore));

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
