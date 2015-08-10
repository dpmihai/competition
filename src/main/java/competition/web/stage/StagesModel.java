package competition.web.stage;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.trg.search.Filter;
import com.trg.search.Search;
import com.trg.search.Sort;

import competition.domain.entity.Stage;
import competition.service.GeneralService;

public class StagesModel extends LoadableDetachableModel<List<Stage>> {
	
	private static final long serialVersionUID = 1L;
	private int competitionId;
	
	@SpringBean
	private GeneralService generalService;
	
	public StagesModel(int competitionId) {
		super();
		this.competitionId = competitionId;
		Injector.get().inject(this);
	}
	
	@Override
	protected List<Stage> load() {
		try {
			if (competitionId == -1) {
				return new ArrayList<Stage>();
			} else {
				return generalService.search(
					new Search(Stage.class).
					addFilter(Filter.equal("competitionId", competitionId)).
					addSort(new Sort("id", false)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Stage>();
		}
	}

}
