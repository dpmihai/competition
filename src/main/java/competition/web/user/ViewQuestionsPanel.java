package competition.web.user;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.domain.entity.Question;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.web.common.behavior.SimpleTooltipBehavior;

public class ViewQuestionsPanel  extends Panel {		
	
	@SpringBean
	private BusinessService businessService;
	
	public ViewQuestionsPanel(String id,  Model<Competition> model) {
		super(id);
		
		Competition competition = model.getObject();	
		
		final ListView<User> teamsView = new ListView<User>("teams", new TeamsModel(competition.getId())) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<User> item) {               
               Label label = new Label("team", item.getModelObject().getUserTeamInitials());
               label.add(new SimpleTooltipBehavior(item.getModelObject().getTeam() + " (" + item.getModelObject().getUsername() + ")"));
               item.add(label);
            }			
						
        };
        teamsView.setReuseItems(true);  
        teamsView.setOutputMarkupPlaceholderTag(true);      
        add(teamsView);
		
		add(createAllUsersQuestionsView(competition));
		
		final ListView<Integer> totalView = new ListView<Integer>("totals", new TotalScoresModel(competition.getId())) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<Integer> item) {
			   Label label = new Label("total", String.valueOf(item.getModelObject()));	
			   label.add(AttributeModifier.replace("class", Model.of("resultQuestion")));			   
               item.add(label);               
            }			
						
        };          
        totalView.setOutputMarkupPlaceholderTag(true);   
        add(totalView);
	}
	
	private DataView<Question> createAllUsersQuestionsView(final Competition competition) {		
		QuestionsDataProvider dataProvider = new QuestionsDataProvider(competition.getId());

		DataView<Question> dataView = new DataView<Question>("questions", dataProvider) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Question> item) {
				ViewAllUsersQuestionPanel panel = new ViewAllUsersQuestionPanel("question", item.getModel(),competition.getId());
				item.add(panel);
			}

		};

		return dataView;
	}
	
	class TeamsModel extends LoadableDetachableModel<List<User>> {

		private static final long serialVersionUID = 1L;
		
		private Integer competitionId;
		
		public TeamsModel(Integer competitionId) {
			super();
			this.competitionId = competitionId;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<User> load() {
			try {				
				List<User> users = businessService.getRegisteredUsers(competitionId);				
				Collections.sort(users, new Comparator<User>() {
					@Override
					public int compare(User u1, User u2) {
						return Collator.getInstance().compare(u1.getUserTeamInitials(), u2.getUserTeamInitials());						
					}					
				});
				return users;
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<User>();
			}
		}

	}
	
	class TotalScoresModel extends LoadableDetachableModel<List<Integer>> {

		private static final long serialVersionUID = 1L;
		private Integer competitionId;
		
		public TotalScoresModel(Integer competitionId) {
			super();
			this.competitionId = competitionId;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<Integer> load() {
			try {
				List<Integer> scores = new ArrayList<Integer>();
						
					List<User> users = businessService.getRegisteredUsers(competitionId);
					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return Collator.getInstance().compare(u1.getTeam(), u2.getTeam());						
						}
						
					});
					for (User user : users) {
						scores.add(businessService.getUserQuizPoints(competitionId, user.getUsername()));
					}					
				
				return scores;
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Integer>();
			}
		}

	}

}
