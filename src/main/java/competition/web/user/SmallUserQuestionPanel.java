package competition.web.user;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import competition.domain.entity.Question;
import competition.domain.entity.User;
import competition.domain.entity.UserResponse;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.util.TeamUtil;

public class SmallUserQuestionPanel extends Panel {
		
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService service;
	
	public SmallUserQuestionPanel(String id, User user, IModel<Question> question) {
		super(id, question);
		
		Question q = question.getObject();
		UserResponse response =  (UserResponse)service.searchUnique(
				new Search(UserResponse.class).
				addFilter(Filter.equal("username", user.getUsername())).
				addFilter(Filter.equal("questionId", q.getId())));
		
		String r = "";
		if (response != null) {
			r = response.getResponse();
			if ((r != null) && !r.isEmpty()) {
				try { 
					Integer.parseInt(r);
				} catch (NumberFormatException ex) {
					// it is a team name
					r = TeamUtil.getAbbreviation(r);
				}
			}	
		}
		 		
		Label teamLabel = new Label("result", r);
		teamLabel.add(new SimpleTooltipBehavior(r));
		add(teamLabel);
		
		int quizPoints = 0;
		if ( (response != null) && (response.getResponse() != null) && (q.getResponse() != null) && 
		     (response.getResponse().equalsIgnoreCase(q.getResponse())) ) {
				quizPoints += 3;
		}
		TextField<Integer> pointsText = new TextField<Integer>("points", new Model<Integer>(quizPoints));
		String cssClass = null;
		if (quizPoints == 3) {
			cssClass = "resultThree";
		}
		if (cssClass != null) {
			pointsText.add(AttributeModifier.replace("class", cssClass));
		}
		pointsText.setEnabled(false);
		add(pointsText);
	}

}
