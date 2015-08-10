package competition.web.competition;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.Competition;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.common.table.BaseTable;
import competition.web.common.table.BooleanImagePropertyColumn;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.user.UserRegistrationPanel;

public class ManageCompetitionsPanel extends Panel {
	
	private DataTable<Competition, String> table;
	private CompetitionsDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;	

	@SpringBean
	private GeneralService generalService;	
	
	@SpringBean
	private BusinessService businessService;	

	public ManageCompetitionsPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Competitii"));                   	       	       
        
        dialog = new ModalWindow("dialog");
        add(dialog);

        add(addLink = new AjaxLink("addCompetition") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga competitie noua", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
                
        final UserRegistrationPanel panel = new UserRegistrationPanel("registration");
        add(panel);
	}
	
	private void addTable() {
        List<IColumn<Competition, String>> columns = new ArrayList<IColumn<Competition, String>>();
        
        columns.add(new AbstractColumn<Competition, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Competition>> item, String componentId,
                                     final IModel<Competition> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
            }
        });
        
        columns.add(new AbstractColumn<Competition, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<Competition>> item, String componentId,
                                     final IModel<Competition> rowModel) {
                final Competition competition = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(competition.getName())));
            }
        });   
        
        columns.add(new BooleanImagePropertyColumn<Competition>(new Model<String>("Activa"), "active"));
        columns.add(new BooleanImagePropertyColumn<Competition>(new Model<String>("Terminata"), "finished"));      
        
        columns.add(new LinkPropertyColumn<Competition>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Competition competition = (Competition) model.getObject();																																
				addEdit("Editeaza competitie", target, competition);     
			}
		});
        
        columns.add(new LinkPropertyColumn<Competition>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti competitie?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				Competition competition = (Competition) model.getObject();																																
				businessService.deleteCompetition(competition.getId());
                target.add(table);     
			}
		});
        
        provider =  new CompetitionsDataProvider();
        table = new BaseTable<Competition, String>("table", columns, provider, 10);
        table.setOutputMarkupId(true);
        add(table);
    }
		
	
	private void addEdit(String title, AjaxRequestTarget target, Competition competition) {
		dialog.setTitle(title);                
        dialog.setAutoSize(true);
		dialog.setContent(new AddEditCompetitionPanel(dialog.getContentId(), competition) {

            @Override
            public void onAddCompetition(AjaxRequestTarget target, Form form, Competition competition) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(competition);
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

