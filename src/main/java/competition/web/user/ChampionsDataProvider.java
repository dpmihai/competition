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

import competition.domain.entity.UserChampion;
import competition.service.GeneralService;

public class ChampionsDataProvider extends SortableDataProvider<UserChampion, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;	

	public ChampionsDataProvider() {
		Injector.get().inject(this);		
	}

	@Override
	public Iterator<? extends UserChampion> iterator(long first, long count) {
		return getChampions().subList((int)first, (int)(first + Math.min(count, size()))).iterator();
	}

	@Override
	public IModel<UserChampion> model(UserChampion user) {
		return new Model<UserChampion>(user);
	}
	

	@Override
	public long size() {
		return getChampionsCount();
	}

	private List<UserChampion> getChampions() {
		List<UserChampion> users = new ArrayList<UserChampion>();		
		try {
			Search search = new Search(UserChampion.class);			
			search.addSort(new Sort("enddate", true));
			users = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return users;
	}

	private int getChampionsCount() {		
		try {
			Search search = new Search(UserChampion.class);			
			int count = service.count(search);			
			return count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

}
