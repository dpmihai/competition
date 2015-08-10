package competition.web.competition;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

import competition.domain.entity.Competition;
import competition.domain.entity.SearchCompetition;
import competition.service.BusinessService;
import competition.web.common.panel.InfoPanel;

public class SearchCompetitionPanel extends Panel {
	
	private SearchCompetition search;
	private FeedbackPanel feedbackPanel;
	private ModalWindow dialog;	
	
	@SpringBean
	private BusinessService businessService;	
	
	public SearchCompetitionPanel(String id) {
		super(id);		
		search = new SearchCompetition();
		SearchForm form = new SearchForm("form");        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
        dialog = new ModalWindow("dialog");
        add(dialog);
	}
	
	public void onSearchCompetition(AjaxRequestTarget target, Form form, SearchCompetition search) {        
		List<Competition> competitions = businessService.findCompetitions(search);		
		if (competitions.size() == 0) {
			dialog.setTitle("Info");			
			dialog.setAutoSize(true);
			dialog.setContent(new InfoPanel(dialog.getContentId(), "Nu exista nici o competitie care sa contina in nume: '" + search.getName() + "' ."));
			dialog.show(target);
		} else {
			SearchCompetitionEvent event = new SearchCompetitionEvent(target);
			event.setCompetitions(competitions);
			// event will be interpreted inside CompetitionsPanel  onEvent(IEvent<?> event)  
			send(getPage(), Broadcast.BREADTH, event);
		}
    }     
	
	class SearchForm extends Form<SearchCompetition> {
        
		private static final long serialVersionUID = 1L;

		public SearchForm(String id) {

            super(id, new CompoundPropertyModel<SearchCompetition>(search));                                 
                        
            TextField<String> name = new TextField<String>("name");            
    		name.setRequired(true);
    		name.setLabel(Model.of("Nume"));    		
    		add(name);
    		    		
            AjaxSubmitLink addLink = new AjaxSubmitLink("search") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                	SearchCompetition search = SearchForm.this.getModelObject();                    
                    onSearchCompetition(target, form, search);
                    target.add(feedbackPanel);
                    target.appendJavaScript("$('#searchPanel').slide()");
                }

                 protected void onError(AjaxRequestTarget target, Form<?> form) {                    
                    target.add(feedbackPanel);
                }                                 

            };
            add(addLink);           
        }               

    }

}
