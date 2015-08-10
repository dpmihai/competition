package competition.web.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import competition.domain.entity.User;
import competition.service.BusinessService;

public class UsersRegisteredDataProvider extends SortableDataProvider<User, String> {

	private static final long serialVersionUID = 1L;

	private int competitionId;
	
	@SpringBean
	private BusinessService service;	

	public UsersRegisteredDataProvider(int competitionId) {
		this.competitionId = competitionId;
		Injector.get().inject(this);		
	}

	@Override
	public Iterator<? extends User> iterator(long first, long count) {
		return getUsers().subList((int)first, (int)(first + Math.min(count, size()))).iterator();
	}

	@Override
	public IModel<User> model(User user) {
		return new Model<User>(user);
	}
	

	@Override
	public long size() {
		return getUsersCount();
	}

	private List<User> getUsers() {
		List<User> users = new ArrayList<User>();		
		try {
			users = service.getRegisteredUsers(competitionId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return users;
	}
	
	public ArrayList<String> getUserNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (User user : getUsers()) {
			names.add(user.getUsername());
		}
		return names;
	}

	private int getUsersCount() {		
		return getUsers().size();
	}
	
	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

}
