package competition.web.stage;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Competition;
import competition.domain.entity.Stage;

public class AddEditStagePanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditStagePanel.class);
	private FeedbackPanel feedbackPanel;	
	private Stage stage = null;
	
	public AddEditStagePanel(String id, Competition competition) {
		this(id, competition, null);
	}
	
	public AddEditStagePanel(String id, Competition competition, Stage stage) {
        super(id);
        this.stage = stage;
        if (stage == null) {
        	stage = new Stage();
        }        
        stage.setCompetitionId(competition.getId());
        StageForm form = new StageForm("form", stage);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddStage(AjaxRequestTarget target, Form form, Stage stage) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class StageForm extends Form<Stage> {
        
		private static final long serialVersionUID = 1L;

		public StageForm(String id, final Stage stage) {

            super(id, new CompoundPropertyModel<Stage>(stage));                                 
                        
            final TextField<String> nameField = new TextField<String>("name");
            nameField.setRequired(true);                                   
            add(nameField);          
            
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
                    Stage stage = StageForm.this.getModelObject();                    
                    onAddStage(target, form, stage);
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
