package competition.web.user;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import competition.domain.entity.Competition;
import competition.domain.entity.User;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class UserQuestionPanel extends Panel {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
	
	private static String USER_STATS = "Per";
	private static String ALL_USER_STATS = "Gen";
	private String stats = USER_STATS;
	
	private Competition competition;			
	private String username;	
	
	private String quizPoints = "";
	
	public UserQuestionPanel(String id, IModel<Competition> competition) {
		super(id);
		this.competition = competition.getObject();
		this.username = SecurityUtil.getLoggedUsername();				
		init();
	}

	private void init() {	
										
		String title = competition.getName() + " - Intrebari  ( Data limita : " + sdf.format(competition.getQuizDate()) + " )";				
		add(new Label("fullTopLabel", title));		
		
		DropDownChoice<String> statsChoice = new DropDownChoice<String>("stats", 
				new PropertyModel<String>(this, "stats"), Arrays.asList(USER_STATS, ALL_USER_STATS)) {
			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				if (user == null) {
					return false;
				}
				Date quizDate = competition.getQuizDate();
				if (quizDate != null) {
					int compare = DateUtil.compare(new Date(), quizDate);
					if ((compare > 0) || user.isAdmin()) {
						return true;
					}
				}				
				return user.isAdmin();							
			}
		};
		statsChoice.setOutputMarkupPlaceholderTag(true);
		add(statsChoice);		
		statsChoice.setOutputMarkupPlaceholderTag(true);	
		
		final WebMarkupContainer container = new WebMarkupContainer("view");
		container.setOutputMarkupPlaceholderTag(true);
		add(container);
		
		statsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {							
				if (USER_STATS.equals(stats)) {
					container.replace(new ViewUserQuestionPanel("qPanel", competition, username));
				} else {
					container.replace(new ViewQuestionsPanel("qPanel", Model.of(competition)));
				}				
				target.add(container);							
			}
		});    
		
		container.add(new ViewUserQuestionPanel("qPanel", competition, username));
	}
	
	

}
