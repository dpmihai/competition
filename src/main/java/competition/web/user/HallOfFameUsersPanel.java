package competition.web.user;

import java.util.Date;
import java.util.List;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.User;
import competition.domain.entity.UserChampion;
import competition.service.GeneralService;
import competition.web.common.behavior.SimpleTooltipBehavior;
import competition.web.util.WicketUtil;

public class HallOfFameUsersPanel extends Panel {

	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private GeneralService generalService;

	public HallOfFameUsersPanel(String id) {
		super(id);
		
		Label label = new Label("title", "Hall of Fame");	
		add(label);

		LoadableDetachableModel<List<UserChampion>> championsProvider = new LoadableDetachableModel<List<UserChampion>>() {
			@Override
			protected List<UserChampion> load() {
				return generalService.search(new Search(UserChampion.class).addSort(new Sort("enddate", true)));
			}
		};

		ListView<UserChampion> lstFameUsers = new ListView<UserChampion>("row", championsProvider) {
			@Override
			protected void populateItem(ListItem<UserChampion> item) {
				IModel<UserChampion> itemModel = item.getModel();							
				item.add(new Label("index", String.valueOf(item.getIndex() + 1)));		
				item.add(new ContextImage("avatar", "img/" + itemModel.getObject().getAvatarFile()));
				item.add(new Label("team", itemModel.getObject().getTeam()));
				item.add(new Label("user", itemModel.getObject().getUsername()));
				item.add(new Label("competition", String.valueOf(itemModel.getObject().getCompetition())));							
				item.add(WicketUtil.getDateLabel("enddate", new Model<Date>(itemModel.getObject().getEnddate()), "dd/MM/yyyy"));
			}
		};
		add(lstFameUsers);
		
		LoadableDetachableModel<List<User>> avatarsProvider = new LoadableDetachableModel<List<User>>() {
			@Override
			protected List<User> load() {
				return generalService.search(new Search(User.class).addSort(new Sort("team")));
			}
		};
		
		ListView<User> lstUsers = new ListView<User>("avatars", avatarsProvider) {
			@Override
			protected void populateItem(ListItem<User> item) {
				IModel<User> itemModel = item.getModel();		
				ContextImage ci = new ContextImage("avatar", "img/" + itemModel.getObject().getAvatarFile());
				ci.add(new SimpleTooltipBehavior(item.getModelObject().getTeam() + " (" + item.getModelObject().getUsername() + ")"));
				item.add(ci);								
			}
		};
		add(lstUsers);
	}

}
