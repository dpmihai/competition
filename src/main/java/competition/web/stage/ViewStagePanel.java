package competition.web.stage;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.BonusPoints;
import competition.domain.entity.Competition;
import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.game.ViewAllUsersGamePanel;
import competition.web.game.ViewGamePanel;
import competition.web.game.GamesDataProvider;
import competition.web.security.SecurityUtil;
import competition.web.util.DateUtil;

public class ViewStagePanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private Date previousDate = null;
	private Date previousAllDate = null;
		
	private static String USER_STATS = "Per";
	private static String ALL_USER_STATS = "Gen";
	private String stats = USER_STATS;
	
	@SpringBean
	private GeneralService service;
	
	@SpringBean
	private BusinessService businessService;
	
	public ViewStagePanel(String id, final IModel<Stage> stage) {
		super(id, stage);
				
		Competition competition = (Competition)service.find(Competition.class, stage.getObject().getCompetitionId());
		
		add(new Label("title", competition.getName() + "  :  " + stage.getObject().getName()));
		add(DateLabel.forDatePattern("titleDate",new Model<Date>( stage.getObject().getFixtureDate()), "dd MMMM yyyy"));
							
		DropDownChoice<String> statsChoice = new DropDownChoice<String>("stats", 
				new PropertyModel<String>(this, "stats"), Arrays.asList(USER_STATS, ALL_USER_STATS)) {
			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				Date currentDate = new Date();
				return (user != null) && ( user.isAdmin() || 
						 DateUtil.compare(stage.getObject().getFixtureDate(), DateUtil.ceil(currentDate)) < 0) ||
						 ( DateUtil.compare(stage.getObject().getFixtureDate(), DateUtil.ceil(currentDate)) == 0) && ( DateUtil.getHour(currentDate) >= 13 );					
			}
		};
		statsChoice.setOutputMarkupPlaceholderTag(true);
		add(statsChoice);										
		
		final ListView<User> teamsView = new ListView<User>("teams", new TeamsModel(stage.getObject())) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<User> item) {               
               Label label = new Label("team", item.getModelObject().getUserTeamInitials());
               label.add(new SimpleTooltipBehavior(item.getModelObject().getTeam() + " (" + item.getModelObject().getUsername() + ")"));
               item.add(label);
            }

			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				boolean show = (user != null) &&  ( user.isAdmin() || 
						DateUtil.compare(stage.getObject().getFixtureDate(), DateUtil.ceil(new Date())) <= 0);	
				return show && (ALL_USER_STATS.equals(stats));
			}
						
        };
        teamsView.setReuseItems(true);  
        teamsView.setOutputMarkupPlaceholderTag(true);            
		
		final WebMarkupContainer container = new WebMarkupContainer("view");
		container.setOutputMarkupPlaceholderTag(true);
		
		container.add(teamsView);
		
		statsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {							
				if (USER_STATS.equals(stats)) {
					container.replace(createGamesView(stage.getObject()));
				} else {
					container.replace(createAllUsersGamesView(stage.getObject()));
				}				
				target.add(container);							
			}
		});    
		
		container.add(createGamesView(stage.getObject()));
		
		final ListView<Integer> totalView = new ListView<Integer>("totals", new TotalScoresModel(stage.getObject())) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<Integer> item) {
			   Label label = new Label("total", String.valueOf(item.getModelObject()));	
			   label.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {

					@Override
					public String getObject() {
						if (USER_STATS.equals(stats)) {
							return "resultStage";
						} else {
							return "totals";
						}
					}

				})
			   );			   
               item.add(label);               
            }

			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				boolean show = (user != null) &&  ( user.isAdmin() || 
						DateUtil.compare(stage.getObject().getFixtureDate(), DateUtil.ceil(new Date())) <= 0);	
				return show; 
			}
						
        };          
        totalView.setOutputMarkupPlaceholderTag(true);   
        container.add(totalView);
        
        final ListView<Integer> bonusView = new ListView<Integer>("bonuses", new BonusModel(stage.getObject())) {

			private static final long serialVersionUID = 1L;

			@Override
            protected void populateItem(ListItem<Integer> item) {
			   Label label = new Label("bonus", String.valueOf(item.getModelObject()));	
			   label.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {

					@Override
					public String getObject() {
						if (USER_STATS.equals(stats)) {
							return "resultStage";
						} else {
							return "totals";
						}
					}

				})
			   );			   
               item.add(label);               
            }

			@Override
			public boolean isVisible() {
				User user = SecurityUtil.getLoggedUser();
				boolean show = (user != null) &&  ( user.isAdmin() || 
						DateUtil.compare(stage.getObject().getFixtureDate(), DateUtil.ceil(new Date())) <= 0);	
				return show; 
			}
						
        };          
        bonusView.setOutputMarkupPlaceholderTag(true);   
        container.add(bonusView);
		
		add(container);
	}
	
	private DataView<Game> createGamesView(Stage stage) {
		previousDate = null;
		GamesDataProvider dataProvider = new GamesDataProvider(stage.getId());

		DataView<Game> dataView = new DataView<Game>("games", dataProvider) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Game> item) {

				ViewGamePanel panel = new ViewGamePanel("game", item.getModel(), previousDate);
				item.add(panel);
				previousDate = item.getModelObject().getFixtureDate();
			}

		};

		return dataView;
	}
	
	private DataView<Game> createAllUsersGamesView(final Stage stage) {
		previousAllDate = null;
		GamesDataProvider dataProvider = new GamesDataProvider(stage.getId());

		DataView<Game> dataView = new DataView<Game>("games", dataProvider) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Game> item) {

				ViewAllUsersGamePanel panel = new ViewAllUsersGamePanel("game", item.getModel(), stage.getCompetitionId(), previousAllDate);
				item.add(panel);
				previousAllDate = item.getModelObject().getFixtureDate();
			}

		};

		return dataView;
	}
	
	class TeamsModel extends LoadableDetachableModel<List<User>> {

		private static final long serialVersionUID = 1L;
		
		private Stage stage;
		
		public TeamsModel(Stage stage) {
			super();
			this.stage = stage;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<User> load() {
			try {				
				List<User> users = businessService.getRegisteredUsers(stage.getCompetitionId());				
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
		private Stage stage;
		
		public TotalScoresModel(Stage stage) {
			super();
			this.stage = stage;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<Integer> load() {
			try {
				List<Integer> scores = new ArrayList<Integer>();
				if (USER_STATS.equals(stats)) {
					scores.add(businessService.computeUserStageScore(stage, SecurityUtil.getLoggedUser()));
				} else {				
					List<User> users = businessService.getRegisteredUsers(stage.getCompetitionId());
					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return Collator.getInstance().compare(u1.getTeam(), u2.getTeam());						
						}
						
					});
					for (User user : users) {
						scores.add(businessService.computeUserStageScore(stage, user));
					}					
				}
				return scores;
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Integer>();
			}
		}

	}
	
	class BonusModel extends LoadableDetachableModel<List<Integer>> {

		private static final long serialVersionUID = 1L;
		private Stage stage;
		
		public BonusModel(Stage stage) {
			super();
			this.stage = stage;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected List<Integer> load() {
			try {
				List<Integer> bonuses = new ArrayList<Integer>();
				if (USER_STATS.equals(stats)) {
					BonusPoints bp = businessService.getBonusPoints(stage, SecurityUtil.getLoggedUser());
					int p = (bp == null) ? 0 : bp.getPoints();
					bonuses.add(p);
				} else {				
					List<User> users = businessService.getRegisteredUsers(stage.getCompetitionId());
					Collections.sort(users, new Comparator<User>() {
						@Override
						public int compare(User u1, User u2) {
							return Collator.getInstance().compare(u1.getTeam(), u2.getTeam());						
						}
						
					});
					for (User user : users) {
						BonusPoints bp = businessService.getBonusPoints(stage, user);
						int p = (bp == null) ? 0 : bp.getPoints();
						bonuses.add(p);						
					}					
				}
				return bonuses;
			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Integer>();
			}
		}

	}



}
