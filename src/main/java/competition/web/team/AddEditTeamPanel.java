package competition.web.team;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Competition;
import competition.domain.entity.Team;

public class AddEditTeamPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditTeamPanel.class);
	private FeedbackPanel feedbackPanel;	
	private Team team = null;
	
	public AddEditTeamPanel(String id, Competition competition) {
		this(id, competition, null);
	}
	
	public AddEditTeamPanel(String id, Competition competition, Team team) {
        super(id);
        this.team = team;
        if (team == null) {
        	team = new Team();
        }        
        team.setCompetitionId(competition.getId());
        TeamForm form = new TeamForm("form", team);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddTeam(AjaxRequestTarget target, Form form, Team team) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class TeamForm extends Form<Team> {
        
		private static final long serialVersionUID = 1L;

		public TeamForm(String id, final Team team) {

            super(id, new CompoundPropertyModel<Team>(team));                                 
                        
            final TextField<String> nameField = new TextField<String>("name");
            nameField.setRequired(true);                                   
            add(nameField);       
            
            final TextField<String> abbreviationField = new TextField<String>("abbreviation");                                               
            add(abbreviationField);       
            
            final TextField<String> avatarField = new TextField<String>("avatarFile");                                              
            add(avatarField);       

            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Team team = TeamForm.this.getModelObject();                    
                    onAddTeam(target, form, team);
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

    }


}
