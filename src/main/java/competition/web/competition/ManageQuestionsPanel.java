package competition.web.competition;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.Question;
import competition.domain.entity.UserResponse;
import competition.service.GeneralService;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;

public class ManageQuestionsPanel extends Panel {
	
	private Competition competition;
	private DataTable<Question, String> table;
	private QuestionsDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;	
	
	@SpringBean
	private GeneralService generalService;		

	public ManageQuestionsPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Intrebari"));                   	
       	
       	List<Competition> competitions = generalService.search(new Search(Competition.class).addSort(new Sort("name", true)));		
		DropDownChoice<Competition> competitionChoice = new DropDownChoice<Competition>("competition", 
				new PropertyModel<Competition>(this, "competition"), competitions, new CompetitionChoiceRenderer());
		competitionChoice.setOutputMarkupPlaceholderTag(true);
		add(competitionChoice);
		competitionChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {				
				provider.setCompetitionId(getCompetitionId());
				target.add(table);		
				target.add(addLink);
			}
		});     
        
        dialog = new ModalWindow("dialog");
        add(dialog);

        add(addLink = new AjaxLink("addQuestion") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga intrebare noua", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {
        List<IColumn<Question, String>> columns = new ArrayList<IColumn<Question, String>>();
        
        columns.add(new AbstractColumn<Question, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Question>> item, String componentId,
                                     final IModel<Question> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });
        
        columns.add(new AbstractColumn<Question, String>(new Model<String>("Intrebare")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Question>> item, String componentId,
                                     final IModel<Question> rowModel) {
                final Question question = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(question.getQuestion())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Question, String>(new Model<String>("Raspuns")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Question>> item, String componentId,
                                     final IModel<Question> rowModel) {
                final Question question = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(question.getResponse())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new LinkPropertyColumn<Question>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Question question = (Question) model.getObject();																																
				addEdit("Editeaza intrebare", target, question);     
			}
		});
        
        columns.add(new LinkPropertyColumn<Question>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti intrebare?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Question question = (Question) model.getObject();																																
				generalService.remove(question);
				
				// when a question is deleted we must delete also all the user responses for that question
				Search search = new Search(UserResponse.class);
				search.addFilterEqual("competitionId", competition.getId());
				search.addFilterEqual("questionId", question.getId());
				List<UserResponse> responses = generalService.search(search);
				generalService.remove(responses);
				
                target.add(table);     
			}
		});
        
        provider =  new QuestionsDataProvider(getCompetitionId());
        table = new BaseTable<Question, String>("table", columns, provider, 300);
        table.setOutputMarkupId(true);
        add(table);
    }
	
	private int getCompetitionId() {
		int competitionId = -1;
		if (competition != null) {
			competitionId = competition.getId();
			addLink.setEnabled(true);
		} else {
			addLink.setEnabled(false);
		}
		return competitionId;
	}
	
	private void addEdit(String title, AjaxRequestTarget target, Question question) {
		dialog.setTitle(title);                
        dialog.setAutoSize(true);
        dialog.setContent(new AddEditQuestionPanel(dialog.getContentId(), competition, question) {

            @Override
            public void onAddQuestion(AjaxRequestTarget target, Form form, Question question) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(question);
                    target.add(table);
                } catch (Exception e) {                            
                    e.printStackTrace();
                    form.error(e.getMessage());
                }
            }

            @Override
            public void onCancel(AjaxRequestTarget target) {
                ModalWindow.closeCurrent(target);
            }

        });
        dialog.show(target);
	}

}
