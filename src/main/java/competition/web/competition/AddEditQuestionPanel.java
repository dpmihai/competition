package competition.web.competition;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import competition.domain.entity.Competition;
import competition.domain.entity.Question;

public class AddEditQuestionPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditQuestionPanel.class);
	private FeedbackPanel feedbackPanel;	
	private Question question = null;
	
	public AddEditQuestionPanel(String id, Competition competition) {
		this(id, competition, null);
	}
	
	public AddEditQuestionPanel(String id, Competition competition, Question question) {
        super(id);
        this.question = question;
        if (question == null) {
        	question = new Question();
        }        
        question.setCompetitionId(competition.getId());
        QuestionForm form = new QuestionForm("form", question);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddQuestion(AjaxRequestTarget target, Form form, Question question) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class QuestionForm extends Form<Question> {
        
		private static final long serialVersionUID = 1L;

		public QuestionForm(String id, final Question question) {

            super(id, new CompoundPropertyModel<Question>(question));                                 
                        
            final TextArea<String> questionArea = new TextArea<String>("question");
            questionArea.setRequired(true);                                   
            add(questionArea);    
            
            final TextArea<String> responseArea = new TextArea<String>("response");                                               
            add(responseArea);    
            
            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Question question = QuestionForm.this.getModelObject();                    
                    onAddQuestion(target, form, question);
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
