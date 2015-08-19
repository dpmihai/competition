package competition.web.user;

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
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.ScorePoints;
import competition.domain.entity.User;
import competition.service.BusinessService;
import competition.service.GeneralService;
import competition.web.BaseApplication;
import competition.web.common.table.BaseTable;
import competition.web.common.table.BooleanImagePropertyColumn;
import competition.web.common.table.LinkPropertyColumn;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Filter;

public class ManageUsersPanel extends Panel {
	
	private DataTable<User, String> table;
	private UsersDataProvider provider;
	private ModalWindow dialog;
	private AjaxLink addLink;	

	@SpringBean
	private GeneralService generalService;	
	
	@SpringBean
	private BusinessService businessService;	

	public ManageUsersPanel(String id) {
		super(id);		
		init();
	}
	
	private void init() {				
		
        add(new Label("title", "Administrare Utilizatori"));                   	       	       
        
        dialog = new ModalWindow("dialog");
        add(dialog);

        add(addLink = new AjaxLink("addUser") {

            @Override
            public void onClick(AjaxRequestTarget target) {
            	addEdit("Adauga utilizator nou", target, null);            	
            }

        });
        addLink.setOutputMarkupId(true);

        addTable();
	}
	
	private void addTable() {		
		
        List<IColumn<User, String>> columns = new ArrayList<IColumn<User, String>>();
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Index")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {                 
            	int col=item.getIndex();
            	Item<?> i = (Item<?>) item.getParent().getParent();
            	int row = i.getIndex()+1; 
                item.add(new Label(componentId, new Model<String>(String.valueOf(row))));                
            }
        });
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Nume")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {
                final User user = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(user.getUsername())));
            }
        });  
        
        columns.add(new AbstractColumn<User, String>(new Model<String>("Echipa")) {

            @Override
            public String getCssClass() {
                return "name-col";
            }

            public void populateItem(Item<ICellPopulator<User>> item, String componentId,
                                     final IModel<User> rowModel) {
                final User user = rowModel.getObject();                
                item.add(new Label(componentId, new Model<String>(user.getTeam())));
            }
		});

		System.out.println("---> add mail column");
		columns.add(new AbstractColumn<User, String>(new Model<String>("Mail")) {

			@Override
			public String getCssClass() {
				return "name-col hideForPhone";
			}

			public void populateItem(Item<ICellPopulator<User>> item, String componentId, final IModel<User> rowModel) {
				final User user = rowModel.getObject();
				item.add(new Label(componentId, new Model<String>(user.getEmail())));
			}
		});
		
		columns.add(new AbstractColumn<User, String>(new Model<String>("Telefon")) {

			@Override
			public String getCssClass() {
				return "name-col hideForPhone";
			}

			public void populateItem(Item<ICellPopulator<User>> item, String componentId, final IModel<User> rowModel) {
				final User user = rowModel.getObject();
				item.add(new Label(componentId, new Model<String>(user.getPhone())));
			}
		});
		
        columns.add(new BooleanImagePropertyColumn<User>(new Model<String>("Admin"), "admin"));        
        
        columns.add(new LinkPropertyColumn<User>(new Model<String>("Editeaza"), new Model<String>("Editeaza")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				User user = (User) model.getObject();				
				addEdit("Editeaza utilizator", target, user);     
			}
		});
        
        columns.add(new LinkPropertyColumn<User>(new Model<String>("Sterge"), new Model<String>("Sterge"),  new Model<String>("Stergeti utilizator?")) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(Item item, String componentId, IModel model, AjaxRequestTarget target) {										
				User user = (User) model.getObject();																																
				businessService.deleteUser(user.getUsername());
                target.add(table);     
			}
		});
        
        provider =  new UsersDataProvider();
        table = new BaseTable<User, String>("table", columns, provider, 10);
        table.setOutputMarkupId(true);
        add(table);
    }
		
	
	private void addEdit(String title, AjaxRequestTarget target, User user) {
		dialog.setTitle(title);                
		dialog.setAutoSize(true);
        dialog.setContent(new AddEditUserPanel(dialog.getContentId(), user) {

            @Override
            public void onAddUser(AjaxRequestTarget target, Form form, User user) {
                try {
                    ModalWindow.closeCurrent(target);    
                    // preserve password                    
    				if (user.getPassword() == null) {
    					User foundUser = generalService.find(User.class, user.getUsername());
    					user.setPassword(foundUser.getPassword());
    				}
                    generalService.merge(user);
                    
                    // modify team and avatar on ScorePoints
                    Search search = new Search(ScorePoints.class);
                    search.addFilter(Filter.equal("username", user.getUsername()));	
    				List<ScorePoints> points = generalService.search(search);		;
                    for (ScorePoints sp : points) {
                    	sp.setTeam(user.getTeam());
                    	sp.setAvatarFile(user.getAvatarFile());
                    }
                    generalService.merge(points);
                    
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

