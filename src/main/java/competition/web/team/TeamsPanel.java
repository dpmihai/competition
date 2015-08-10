package competition.web.team;

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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Competition;
import competition.domain.entity.Team;
import competition.service.GeneralService;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.competition.CompetitionChoiceRenderer;

public class TeamsPanel extends Panel {

	private Competition competition;
	private DataTable<Team, String> table;
	private TeamsDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;		
	
	@SpringBean
	private GeneralService generalService;		

	public TeamsPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Echipe"));                   	
       	
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

        add(addLink = new AjaxLink("addTeam") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga echipa noua", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {
        List<IColumn<Team, String>> columns = new ArrayList<IColumn<Team, String>>();
        
        columns.add(new AbstractColumn<Team, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Team>> item, String componentId,
                                     final IModel<Team> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });
        
        columns.add(new AbstractColumn<Team, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Team>> item, String componentId,
                                     final IModel<Team> rowModel) {
                final Team team = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(team.getName())));
                //item.add(new SimpleAttributeModifier("class", "name-col"));
            }
        });   
        
        columns.add(new LinkPropertyColumn<Team>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Team team = (Team) model.getObject();																																
				addEdit("Editeaza echipa", target, team);     
			}
		});
        
        columns.add(new LinkPropertyColumn<Team>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti echipa?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Team team = (Team) model.getObject();																																
				generalService.remove(team);
                target.add(table);     
			}
		});
        
        provider =  new TeamsDataProvider(getCompetitionId());
        table = new BaseTable<Team, String>("table", columns, provider, 300);
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
	
	private void addEdit(String title, AjaxRequestTarget target, Team team) {
		dialog.setTitle(title);                
		dialog.setAutoSize(true);
        dialog.setContent(new AddEditTeamPanel(dialog.getContentId(), competition, team) {

            @Override
            public void onAddTeam(AjaxRequestTarget target, Form form, Team team) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(team);
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
