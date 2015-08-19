package competition.web.team;

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

import competition.domain.entity.Team;
import competition.service.GeneralService;

public class TeamsDataProvider extends SortableDataProvider<Team, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int competitionId;

	public TeamsDataProvider(int competitionId) {
		Injector.get().inject(this);
		this.competitionId = competitionId;
	}

	@Override
	public Iterator<? extends Team> iterator(long first, long count) {
		return getTeams().iterator();
	}

	@Override
	public IModel<Team> model(Team team) {
		return new Model<Team>(team);
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

	@Override
	public long size() {
		return getTeamsCount();
	}

	private List<Team> getTeams() {
		List<Team> teams = new ArrayList<Team>();
		if (competitionId == -1) {
			return teams;
		}
		try {
			Search search = new Search(Team.class);
			search.addFilterEqual("competitionId", competitionId);
			search.addSort(new Sort("name", false));
			teams = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return teams;
	}

	private int getTeamsCount() {
		if (competitionId == -1) {
			return 0;
		}
		try {
			Search search = new Search(Team.class);
			search.addFilterEqual("competitionId", competitionId);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
