package competition.web.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Question;
import competition.domain.entity.UserResponse;
import competition.service.GeneralService;

public class EditUserResponsePanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(EditUserResponsePanel.class);
	
	@SpringBean
	private GeneralService generalService;
	
	private FeedbackPanel feedbackPanel;	
	private UserResponse response = null;		
	
	public EditUserResponsePanel(String id, UserResponse response) {
        super(id);
        this.response = response;
        QuestionForm form = new QuestionForm("form", response);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onEditUserResponse(AjaxRequestTarget target, Form form, UserResponse response) {
        // override
    }  
		
    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class QuestionForm extends Form<UserResponse> {
        
		private static final long serialVersionUID = 1L;

		public QuestionForm(String id, final UserResponse response) {

            super(id, new CompoundPropertyModel<UserResponse>(response));   
            
            final Question question = generalService.find(Question.class, response.getQuestionId());
            Label label = new Label("question", question.getQuestion());
            add(label);
            
            final TextArea<String> responseArea = new TextArea<String>("response");                                               
            add(responseArea);    
            
            AjaxSubmitLink editLink = new AjaxSubmitLink("edit") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    UserResponse response = QuestionForm.this.getModelObject();                    
                    onEditUserResponse(target, form, response);
                }

                 protected void onError(AjaxRequestTarget target, Form<?> form) {                    
                    target.add(feedbackPanel);
                }

            };                        
            
            add(editLink);


            add(new AjaxLink("cancel") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    onCancel(target);
                }

            });
        }               

    }


}
