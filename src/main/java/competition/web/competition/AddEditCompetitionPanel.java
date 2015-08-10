package competition.web.competition;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.Stage;
import competition.service.GeneralService;
import competition.web.stage.StageChoiceRenderer;

public class AddEditCompetitionPanel extends Panel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AddEditCompetitionPanel.class);
	private FeedbackPanel feedbackPanel;	
	private Competition competition = null;
	
	@SpringBean
	private GeneralService generalService;	
	
	public AddEditCompetitionPanel(String id) {
		this(id, null);
	}
	
	public AddEditCompetitionPanel(String id, Competition competition) {
        super(id);
        this.competition = competition;
        if (competition == null) {
        	competition = new Competition();
        }                
        CompetitionForm form = new CompetitionForm("form", competition);        
        feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        add(form);
    }
	
	public void onAddCompetition(AjaxRequestTarget target, Form form, Competition competition) {
        // override
    }  

    public void onCancel(AjaxRequestTarget target) {
        // override
    }
    
    class CompetitionForm extends Form<Competition> {
        
		private static final long serialVersionUID = 1L;
		
		private Stage playoffStage;

		public CompetitionForm(String id, final Competition competition) {

            super(id, new CompoundPropertyModel<Competition>(competition));                                 
                        
            TextField<String> name = new TextField<String>("name");            
    		name.setRequired(true);
    		//name.add(new DuplicatePropertyValidator(Competition.class, "name", "competition.errorNameExists"));
    		name.setLabel(Model.of("Nume"));
    		name.add(StringValidator.minimumLength(3));
    		name.add(StringValidator.maximumLength(50));
    		add(name);
    		
    		TextField<String> imageFile = new TextField<String>("imageFile");
    		imageFile.setRequired(true);		
    		imageFile.setLabel(Model.of("Imagine"));		
    		add(imageFile);
    		
    		TextField<String> rss= new TextField<String>("rss");
    		rss.setRequired(true);		
    		rss.setLabel(Model.of("Rss"));		
    		add(rss);
    		
    		CheckBox active = new CheckBox("active");
        	add(active);    
        	
        	CheckBox finished = new CheckBox("finished");
        	add(finished);     
        	
        	DateField quizTime = new DateField("quizDate") {

                protected DateTextField newDateTextField(String s, PropertyModel propertyModel) {
                    DateTextField dateField = super.newDateTextField(s, propertyModel);
                    dateField.setLabel(new Model<String>("Data chestionar"));
                    return dateField;
                }
            };            
            add(quizTime);
            
        	List<Stage> stages = generalService.search(new Search(Stage.class).
        			addFilter(Filter.equal("competitionId", competition.getId())).addSort(new Sort("fixtureDate", false)));		
    		DropDownChoice<Stage> playoffStageChoice = new DropDownChoice<Stage>("playoffStage", 
    				new PropertyModel<Stage>(this, "playoffStage"), stages, new StageChoiceRenderer());
    		playoffStageChoice.setNullValid(true);
    		playoffStageChoice.setOutputMarkupPlaceholderTag(true);
    		if (competition.getPlayoffFirstStageId() != null) {
    			playoffStage = findStageById(competition.getPlayoffFirstStageId(), stages);
    		}
    		playoffStageChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

    			private static final long serialVersionUID = 1L;

    			@Override
    			protected void onUpdate(AjaxRequestTarget target) {	
    				Integer id = null;
    				if (playoffStage != null) {
    					id = playoffStage.getId();
    				}
    				competition.setPlayoffFirstStageId(id);
    			}
    		}); 
    		// on add new competition we do not have yet the stages
    		if (competition.getId() == null) {
    			playoffStageChoice.setEnabled(false);
    		}
    		add(playoffStageChoice);

            AjaxSubmitLink addLink = new AjaxSubmitLink("add") {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                	Competition competition = CompetitionForm.this.getModelObject();                    
                    onAddCompetition(target, form, competition);
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
		
		private Stage findStageById(Integer id, List<Stage> stages) {
			for (Stage s : stages) {
				if (s.getId().equals(id)) {
					return s;
				}
			}
			return null;
		}

    }


}
