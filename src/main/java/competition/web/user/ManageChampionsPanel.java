package competition.web.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
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

import competition.domain.entity.UserChampion;
import competition.service.GeneralService;
import competition.web.BaseApplication;
import competition.web.common.table.BaseTable;
import competition.web.common.table.LinkPropertyColumn;
import competition.web.util.WicketUtil;

public class ManageChampionsPanel extends Panel {
	
	private DataTable<UserChampion, String> table;
	private ChampionsDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;	

	@SpringBean
	private GeneralService generalService;			

	public ManageChampionsPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {
		
        add(new Label("title", "Administrare Campioni"));                   	       	       
        
        dialog = new ModalWindow("dialog");
        add(dialog);

        add(addLink = new AjaxLink("addUser") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga campion nou", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {				
		
        List<IColumn<UserChampion, String>> columns = new ArrayList<IColumn<UserChampion, String>>();
        
        columns.add(new AbstractColumn<UserChampion, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserChampion>> item, String componentId,
                                     final IModel<UserChampion> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
            }
		});

		columns.add(new AbstractColumn<UserChampion, String>(new Model<String>("Data de final")) {

			@Override
			public String getCssClass() {
				return "name-col hideForPhone";
			}

			public void populateItem(Item<ICellPopulator<UserChampion>> item, String componentId,
					final IModel<UserChampion> rowModel) {
				final UserChampion user = rowModel.getObject();				
				item.add(WicketUtil.getDateLabel(componentId, new Model<Date>(user.getEnddate()), "dd-MMM-yyyy"));
			}
		});
		        
        columns.add(new AbstractColumn<UserChampion, String>(new Model<String>("Competitie")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserChampion>> item, String componentId,
                                     final IModel<UserChampion> rowModel) {
                final UserChampion user = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(user.getCompetition())));
            }
        });  
        
        columns.add(new AbstractColumn<UserChampion, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<UserChampion>> item, String componentId,
                                     final IModel<UserChampion> rowModel) {
                final UserChampion user = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(user.getUsername())));
            }
        });  
        		
		columns.add(new AbstractColumn<UserChampion, String>(new Model<String>("Echipa")) {

			@Override
			public String getCssClass() {
				return "name-col hideForPhone";
			}

			public void populateItem(Item<ICellPopulator<UserChampion>> item, String componentId,
					final IModel<UserChampion> rowModel) {
				final UserChampion user = rowModel.getObject();
				item.add(new Label(componentId, new Model<String>(user.getTeam())));
			}
		});
        
        columns.add(new LinkPropertyColumn<UserChampion>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				UserChampion user = (UserChampion) model.getObject();																																
				addEdit("Editeaza campion", target, user);     
			}
		});
        
        columns.add(new LinkPropertyColumn<UserChampion>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti campion?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				UserChampion user = (UserChampion) model.getObject();																																
				generalService.remove(user);
                target.add(table);     
			}
		});
        
        provider =  new ChampionsDataProvider();
        table = new BaseTable<UserChampion, String>("table", columns, provider, 10);
        table.setOutputMarkupId(true);
        add(table);
    }
		
	
	private void addEdit(String title, AjaxRequestTarget target, UserChampion user) {
		dialog.setTitle(title);                
		dialog.setAutoSize(true);
        dialog.setContent(new AddEditChampionPanel(dialog.getContentId(), user) {

            @Override
            public void onAddUser(AjaxRequestTarget target, Form form, UserChampion user) {
                try {
                    ModalWindow.closeCurrent(target);                          
                    generalService.merge(user);
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

