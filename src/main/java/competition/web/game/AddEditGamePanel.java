package competition.web.game;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Game;
import competition.domain.entity.Stage;
import competition.domain.entity.Team;
import competition.service.GeneralService;
import competition.web.team.TeamChoiceRenderer;

public class AddEditGamePanel  extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditGamePanel.class);
	private FeedbackPanel feedbackPanel;	
	private Game game = null;
	private Team hostsTeam = null;
	private Team guestsTeam = null;
	
	@SpringBean
	private GeneralService generalService;
	
	public AddEditGamePanel(String id, Stage stage) {
		this(id, stage, null);
	}
	
	public AddEditGamePanel(String id, Stage stage, Game game) {
        super(id);        
        if (game == null) {
        	game = new Game();
        } else {
        	hostsTeam = generalService.find(Team.class, game.getHostsId());
        	guestsTeam = generalService.find(Team.class, game.getGuestsId());
        }
        game.setStageId(stage.getId());
        this.game = game;
        GameForm form = new GameForm("form", stage, game);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddGame(AjaxRequestTarget target, Form form, Game game) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class GameForm extends Form<Game> {
        
		private static final long serialVersionUID = 1L;

		public GameForm(String id, final Stage stage, final Game game) {

            super(id, new CompoundPropertyModel<Game>(game));                                 
                        
            List<Team> teams = generalService.search(new Search(Team.class).addFilter(Filter.equal("competitionId", stage.getCompetitionId())).addSort(new Sort("name", false)));
            
            DropDownChoice<Team> hostsTeamChoice = new DropDownChoice<Team>("hostsTeam", 
    				new PropertyModel<Team>(this, "hostsTeam"), teams, new TeamChoiceRenderer());
            hostsTeamChoice.setOutputMarkupPlaceholderTag(true);
            hostsTeamChoice.setRequired(true);
    		add(hostsTeamChoice); 
    		
    		DropDownChoice<Team> guestsTeamChoice = new DropDownChoice<Team>("guestsTeam", 
    				new PropertyModel<Team>(this, "guestsTeam"), teams, new TeamChoiceRenderer());
    		guestsTeamChoice.setOutputMarkupPlaceholderTag(true);
    		guestsTeamChoice.setRequired(true);
    		add(guestsTeamChoice); 
    		
    		final TextField<Integer> hostsScoreField = new TextField<Integer>("hostsScore");    		                                  
            add(hostsScoreField);   
            
            final TextField<Integer> guestsScoreField = new TextField<Integer>("guestsScore");    		                                  
            add(guestsScoreField);    
            
            DateField startTime = new DateField("fixtureDate") {

                protected DateTextField newDateTextField(String s, PropertyModel propertyModel) {
                    DateTextField dateField = super.newDateTextField(s, propertyModel);
                    dateField.setLabel(new Model<String>("Data de start"));
                    return dateField;
                }
            };
            startTime.setRequired(true);
            add(startTime);

            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Game game = GameForm.this.getModelObject();                    
                    onAddGame(target, form, game);
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
        }  
		
		public Team getHostsTeam() {
			return hostsTeam;
		}
		
		public Team getGuestsTeam() {
			return guestsTeam;
		}
		
		public void setHostsTeam(Team _hostsTeam) {
			hostsTeam = _hostsTeam;
			game.setHostsId(hostsTeam.getId());
		}
		
		public void setGuestsTeam(Team _guestsTeam) {
			guestsTeam = _guestsTeam;
			game.setGuestsId(guestsTeam.getId());
		}

    }


}
