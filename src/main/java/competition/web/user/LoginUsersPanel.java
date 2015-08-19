package competition.web.user;

import java.util.Date;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.Login;
import competition.service.GeneralService;

public class LoginUsersPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService generalService;

	public LoginUsersPanel(String id) {
		super(id);
		
		Label label = new Label("title", "Last Login");	
		add(label);

		LoadableDetachableModel<List<Login>> loginProvider = new LoadableDetachableModel<List<Login>>() {
			@Override
			protected List<Login> load() {
				return generalService.search(new Search(Login.class).addSort(new Sort("loginDate", true)));
			}
		};

		ListView<Login> lstFameUsers = new ListView<Login>("row", loginProvider) {
			@Override
			protected void populateItem(ListItem<Login> item) {
				IModel<Login> itemModel = item.getModel();											
				item.add(new Label("user", itemModel.getObject().getUsername()));						
				item.add(DateLabel.forDatePattern("lastLogin", new Model<Date>(itemModel.getObject().getLoginDate()), "dd/MM/yyyy hh:mm aa"));				 
			}
		};
		add(lstFameUsers);
				
	}

}
