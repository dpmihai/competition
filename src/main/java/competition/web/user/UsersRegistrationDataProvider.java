package competition.web.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.User;
import competition.domain.entity.UserRegistration;
import competition.service.GeneralService;

public class UsersRegistrationDataProvider extends SortableDataProvider<UserRegistration, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int competitionId;

	public UsersRegistrationDataProvider(int competitionId) {
		Injector.get().inject(this);
		this.competitionId = competitionId;
	}

	@Override
	public Iterator<? extends UserRegistration> iterator(long first, long count) {
		return getRegistrations().iterator();
	}

	@Override
	public IModel<UserRegistration> model(UserRegistration team) {
		return new Model<UserRegistration>(team);
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

	@Override
	public long size() {
		return getRegistrationsCount();
	}

	private List<UserRegistration> getRegistrations() {
		List<UserRegistration> registrations = new ArrayList<UserRegistration>();
		if (competitionId == -1) {
			return registrations;
		}
		try {
			Search search = new Search(UserRegistration.class);
			search.addFilterEqual("competitionId", competitionId);
			search.addSort(new Sort("username"));
			registrations = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return registrations;
	}
	
	public List<String> getRegisteredUsers() {
		List<String> users = new ArrayList<String>();
		for (UserRegistration reg : getRegistrations()) {
			users.add(reg.getUsername());
		}
		return users;
	}

	private int getRegistrationsCount() {
		if (competitionId == -1) {
			return 0;
		}
		try {
			Search search = new Search(UserRegistration.class);
			search.addFilterEqual("competitionId", competitionId);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
