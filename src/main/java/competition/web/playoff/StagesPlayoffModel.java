package competition.web.playoff;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;

import competition.domain.entity.StagePlayoff;
import competition.service.GeneralService;

public class StagesPlayoffModel extends LoadableDetachableModel<List<StagePlayoff>> {

	private static final long serialVersionUID = 1L;
	private int competitionId;

	@SpringBean
	private GeneralService generalService;

	public StagesPlayoffModel(int competitionId) {
		super();
		this.competitionId = competitionId;
		Injector.get().inject(this);
	}

	@Override
	protected List<StagePlayoff> load() {
		try {
			if (competitionId == -1) {
				return new ArrayList<StagePlayoff>();
			} else {
				return generalService.search(new Search(StagePlayoff.class).addFilter(
						Filter.equal("competitionId", competitionId)).addSort(
						new Sort("id", false)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<StagePlayoff>();
		}
	}

}
