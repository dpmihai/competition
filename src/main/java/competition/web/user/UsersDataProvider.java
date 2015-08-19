package competition.web.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.User;
import competition.service.GeneralService;

public class UsersDataProvider extends SortableDataProvider<User, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;	

	public UsersDataProvider() {
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
			Search search = new Search(User.class);			
			search.addSort(new Sort("username"));
			users = service.search(search);
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
		try {
			Search search = new Search(User.class);			
			int count = service.count(search);			
			return count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
