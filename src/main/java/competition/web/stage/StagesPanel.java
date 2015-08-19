package competition.web.stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.Stage;
import competition.service.GeneralService;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.competition.CompetitionChoiceRenderer;

public class StagesPanel extends Panel {

	private Competition competition;
	private DataTable<Stage, String> table;
	private StagesDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;		
	
	@SpringBean
	private GeneralService generalService;		

	public StagesPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Etape"));                   	
       	
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

        add(addLink = new AjaxLink("addStage") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga etapa noua", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {
        List<IColumn<Stage, String>> columns = new ArrayList<IColumn<Stage, String>>();
        
        columns.add(new AbstractColumn<Stage, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Stage>> item, String componentId,
                                     final IModel<Stage> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });
        
        columns.add(new AbstractColumn<Stage, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Stage>> item, String componentId,
                                     final IModel<Stage> rowModel) {
                final Stage stage = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(stage.getName())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new AbstractColumn<Stage, String>(new Model<String>("Data")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Stage>> item, String componentId,
                                     final IModel<Stage> rowModel) {
                final Stage stage = rowModel.getObject();                                
                item.add(DateLabel.forDatePattern(componentId, new Model<Date>(stage.getFixtureDate()), "dd-MMM-yyyy EEEE"));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new LinkPropertyColumn<Stage>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Stage team = (Stage) model.getObject();																																
				addEdit("Editeaza etapa", target, team);     
			}
		});
        
        columns.add(new LinkPropertyColumn<Stage>(new Model<String>("Sterge"), new Model<String>("Sterge"), new Model<String>("Stergeti etapa?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Stage stage = (Stage) model.getObject();																																
				generalService.remove(stage);
                target.add(table);     
			}
		});
        
        provider =  new StagesDataProvider(getCompetitionId());
        table = new BaseTable<Stage, String>("table", columns, provider, 300);
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
	
	private void addEdit(String title, AjaxRequestTarget target, Stage stage) {
		dialog.setTitle(title);                
		dialog.setAutoSize(true);
        dialog.setContent(new AddEditStagePanel(dialog.getContentId(), competition, stage) {

            @Override
            public void onAddStage(AjaxRequestTarget target, Form form, Stage stage) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(stage);
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
