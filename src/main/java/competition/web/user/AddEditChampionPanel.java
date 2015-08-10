package competition.web.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.UserChampion;

public class AddEditChampionPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditChampionPanel.class);
	private FeedbackPanel feedbackPanel;	
	private UserChampion user = null;
	
	public AddEditChampionPanel(String id) {
		this(id, null);
	}
	
	public AddEditChampionPanel(String id, UserChampion user) {
        super(id);
        this.user = user;
        if (user == null) {
        	user = new UserChampion();
        }                
        UserForm form = new UserForm("form", user);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddUser(AjaxRequestTarget target, Form form, UserChampion user) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class UserForm extends Form<UserChampion> {
        
		private static final long serialVersionUID = 1L;				

		public UserForm(String id, final UserChampion user) {

            super(id, new CompoundPropertyModel<UserChampion>(user));                                 
                        
            TextField<String> username = new TextField<String>("username");
    		username.setRequired(true);
    		username.setLabel(Model.of("Utilizator"));
    		username.add(StringValidator.minimumLength(3));
    		username.add(StringValidator.maximumLength(15));
    		add(username);    		

    		TextField<String> competition = new TextField<String>("competition");
    		competition.setRequired(true);    		
    		competition.setLabel(Model.of("Competitie"));
    		add(competition);
    		
    		TextField<String> team = new TextField<String>("team");
    		team.setRequired(true);
    		team.setLabel(Model.of("Echipa"));
    		add(team);
    		
    		TextField<String> avatarFile = new TextField<String>("avatarFile");
    		avatarFile.setRequired(true);
    		avatarFile.setLabel(Model.of("Avatar"));
    		add(avatarFile);

    		DateField endTime = new DateField("enddate") {

                protected DateTextField newDateTextField(String s, PropertyModel propertyModel) {
                    DateTextField dateField = super.newDateTextField(s, propertyModel);
                    dateField.setLabel(new Model<String>("Data de start"));
                    return dateField;
                }
            };
            endTime.setRequired(true);
            add(endTime);
    		
            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                	UserChampion user = UserForm.this.getModelObject();                    
                    onAddUser(target, form, user);
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
