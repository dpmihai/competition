package competition.web.competition;

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

import competition.domain.entity.Competition;
import competition.service.GeneralService;

public class CompetitionsDataProvider extends SortableDataProvider<Competition, String> {
		
	@SpringBean
	private GeneralService service;	

	public CompetitionsDataProvider() {
		Injector.get().inject(this);
	}

	@Override
	public Iterator<? extends Competition> iterator(long first, long count) {		
		return getCompetitions().subList((int)first, (int)(first + Math.min(count, size()))).iterator();
	}

	@Override
	public IModel<Competition> model(Competition competition) {		
		return new Model<Competition>(competition);
	}

	@Override
	public long size() {		
		return getCompetitionsCount();
	}

	private List<Competition> getCompetitions() {
		List<Competition> competitions = new ArrayList<Competition>();
		try {
			Search search = new Search(Competition.class);
			//search.setFirstResult(first);
			//search.setMaxResults(NO);
			// search.addFilterEqual("createdFor", username);
			search.addSort(new Sort("active", true));
			search.addSort(new Sort("finished"));
			search.addSort(new Sort("name"));			
			competitions = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return competitions;
	}

	private int getCompetitionsCount() {
		try {
			Search search = new Search(Competition.class);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
