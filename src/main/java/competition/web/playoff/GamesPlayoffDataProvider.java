package competition.web.playoff;

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

import competition.domain.entity.GamePlayoff;
import competition.service.GeneralService;

public class GamesPlayoffDataProvider extends SortableDataProvider<GamePlayoff, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int stagePlayoffId;
	
	public GamesPlayoffDataProvider(int stagePlayoffId) {
		Injector.get().inject(this);
		this.stagePlayoffId = stagePlayoffId;
	}

	@Override
	public Iterator<? extends GamePlayoff> iterator(long first, long count) {
		return getGames().iterator();
	}

	@Override
	public IModel<GamePlayoff> model(GamePlayoff game) {
		return new Model<GamePlayoff>(game);
	}

	@Override
	public long size() {
		return getGamesCount();
	}

	public void setstagePlayoffId(int stagePlayoffId) {
		this.stagePlayoffId = stagePlayoffId;
	}

	public List<GamePlayoff> getGames() {
		List<GamePlayoff> games = new ArrayList<GamePlayoff>();
		if (stagePlayoffId == -1) {
			return games;
		}
		try {
			Search search = new Search(GamePlayoff.class);			
			search.addFilterEqual("stagePlayoffId", stagePlayoffId);			
			search.addSort(new Sort("id"));
			games = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return games;
	}
	
	public List<Integer> getGamesIds() {
		List<Integer> result = new ArrayList<Integer>();
		List<GamePlayoff> games = getGames();
		for (GamePlayoff game : games) {
			result.add(game.getId());
		}
		return result;
	}

	private int getGamesCount() {
		if (stagePlayoffId == -1) {
			return 0;
		}
		try {
			Search search = new Search(GamePlayoff.class);
			search.addFilterEqual("stagePlayoffId", stagePlayoffId);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}

