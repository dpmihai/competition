package competition.web.game;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;
import competition.domain.entity.User;
import competition.domain.entity.UserScore;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.security.SecurityUtil;
import competition.web.team.TeamRankingPanel;

public class AddEditUserScoresPanel extends Panel implements IAjaxIndicatorAware {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditUserScoresPanel.class);
	private FeedbackPanel feedbackPanel;
	private List<UserScore> scores = null;
	private String username;

	@SpringBean
	private GeneralService generalService;
	
	@SpringBean
	private BusinessService businessService;

	public AddEditUserScoresPanel(String id, Stage stage) {
		super(id);
		this.scores = businessService.getUserScores(stage, SecurityUtil.getLoggedUser());
		this.username = SecurityUtil.getLoggedUser().getUsername();		
		ScoresForm form = new ScoresForm("form", stage);
		feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);
		add(form);		
	}

	public void onAddScores(AjaxRequestTarget target, Form form, List<UserScore> scores) {
		// override
	}

	public void onCancel(AjaxRequestTarget target) {
		// override
	}

	class ScoresForm extends Form<List<UserScore>> {

		private static final long serialVersionUID = 1L;
		private WebMarkupContainer scoresContainer;

		public ScoresForm(String id, final Stage stage) {

			super(id);
			
			scoresContainer = new WebMarkupContainer("scoresContainer");
			scoresContainer.setOutputMarkupId(true);
			
			final ListView<UserScore> listView = new ListView<UserScore>("scores", scores) {

				private static final long serialVersionUID = 1L;

				@Override
	            protected void populateItem(ListItem<UserScore> item) {
	                createItem(item);
	            }

	        };	        
	        scoresContainer.add(listView);
	        add(scoresContainer);
	        
			add(new TeamRankingPanel("teamRanking", stage.getCompetitionId(), false));

			ArrayList<String> names = new ArrayList<String>();
			try {
				List<User> users = businessService.getRegisteredUsers(stage.getCompetitionId());
				for (User user : users) {
					names.add(user.getUsername());
				}
			} catch (Exception e) {

			}
		    			    			        
	        DropDownChoice<String> userChoice = new DropDownChoice<String>("users", 
					new PropertyModel<String>(this, "username"), names) {
				@Override
				public boolean isVisible() {
					User user = SecurityUtil.getLoggedUser();
					return (user != null) && user.isAdmin();					
				}
			};
			userChoice.setOutputMarkupPlaceholderTag(true);
			add(userChoice);		
			userChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {		
					scores = businessService.getUserScores(stage, generalService.find(User.class, username));
					final ListView<UserScore> listView = new ListView<UserScore>("scores", scores) {

						private static final long serialVersionUID = 1L;

						@Override
			            protected void populateItem(ListItem<UserScore> item) {
			                createItem(item);
			            }

			        };			        
			        scoresContainer.replace(listView);		
			        target.add(scoresContainer);
				}
			});    

			AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {					
					onAddScores(target, form, scores);
				}

				protected void onError(AjaxRequestTarget target, Form<?> form) {
					target.add(feedbackPanel);
				}

			};
			add(addLink);

			add(new AjaxLink("cancel") {

				@Override
				public void onClick(AjaxRequestTarget target) {
					onCancel(target);
				}

			});
			
			add(new AjaxLink("random") {

				@Override
				public void onClick(AjaxRequestTarget target) {					
					businessService.updateRandomScores(scores, stage.getCompetitionId());
					target.add(scoresContainer);
				}

			});
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String localUsername) {
			username = localUsername;
		}

	}
	
	private void createItem(ListItem<UserScore> item) {
		
		Game game = generalService.find(Game.class, item.getModelObject().getGameId());
		Team hostsTeam = generalService.find(Team.class, game.getHostsId());
    	Team guestsTeam = generalService.find(Team.class, game.getGuestsId());
		
		item.add(new Label("opponents", new Model<String>(hostsTeam.getName() + " - " + guestsTeam.getName())));		
		
		final TextField<Integer> hostsScoreField = new TextField<Integer>("hostsScore", new PropertyModel<Integer>(item.getModelObject(), "hostsScore"));
		item.add(hostsScoreField);
		
		item.add(new Label("sep", " : "));

		final TextField<Integer> guestsScoreField = new TextField<Integer>("guestsScore", new PropertyModel<Integer>(item.getModelObject(), "guestsScore"));
		item.add(guestsScoreField);			
	}
	
	// class implements IAjaxIndicatorAware
	public String getAjaxIndicatorMarkupId() {
        return "veil";
    }
	
}
