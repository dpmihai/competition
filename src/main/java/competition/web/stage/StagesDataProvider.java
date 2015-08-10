package competition.web.stage;

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

import competition.domain.entity.Stage;
import competition.service.GeneralService;

public class StagesDataProvider extends SortableDataProvider<Stage, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int competitionId;

	public StagesDataProvider(int competitionId) {
		Injector.get().inject(this);
		this.competitionId = competitionId;
	}

	@Override
	public Iterator<? extends Stage> iterator(long first, long count) {
		return getStages().iterator();
	}

	@Override
	public IModel<Stage> model(Stage competition) {
		return new Model<Stage>(competition);
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

	@Override
	public long size() {
		return getStagesCount();
	}

	private List<Stage> getStages() {
		List<Stage> stages = new ArrayList<Stage>();
		if (competitionId == -1) {
			return stages;
		}
		try {
			Search search = new Search(Stage.class);
			// search.setFirstResult(first);
			// search.setMaxResults(count);
			search.addFilterEqual("competitionId", competitionId);
			search.addSort(new Sort("fixtureDate", false));
			stages = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stages;
	}

	private int getStagesCount() {
		if (competitionId == -1) {
			return 0;
		}
		try {
			Search search = new Search(Stage.class);
			search.addFilterEqual("competitionId", competitionId);
			int count = service.count(search);			
			return count;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
