package competition.web.game;

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

import competition.domain.entity.Game;
import competition.service.GeneralService;

public class GamesDataProvider extends SortableDataProvider<Game, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int stageId;
	
	public GamesDataProvider(int stageId) {
		Injector.get().inject(this);
		this.stageId = stageId;
	}

	@Override
	public Iterator<? extends Game> iterator(long first, long count) {
		return getGames().iterator();
	}

	@Override
	public IModel<Game> model(Game game) {
		return new Model<Game>(game);
	}

	@Override
	public long size() {
		return getGamesCount();
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public List<Game> getGames() {
		List<Game> games = new ArrayList<Game>();
		if (stageId == -1) {
			return games;
		}
		try {
			Search search = new Search(Game.class);			
			search.addFilterEqual("stageId", stageId);
			search.addSort(new Sort("fixtureDate"));
			search.addSort(new Sort("id"));
			games = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return games;
	}
	
	public List<Integer> getGamesIds() {
		List<Integer> result = new ArrayList<Integer>();
		List<Game> games = getGames();
		for (Game game : games) {
			result.add(game.getId());
		}
		return result;
	}

	private int getGamesCount() {
		if (stageId == -1) {
			return 0;
		}
		try {
			Search search = new Search(Game.class);
			search.addFilterEqual("stageId", stageId);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

}
