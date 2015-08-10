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

import competition.domain.entity.Question;
import competition.service.GeneralService;

public class QuestionsDataProvider extends SortableDataProvider<Question, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int competitionId;

	public QuestionsDataProvider(int competitionId) {
		Injector.get().inject(this);
		this.competitionId = competitionId;
	}

	@Override
	public Iterator<? extends Question> iterator(long first, long count) {
		return getQuestions().iterator();
	}

	@Override
	public IModel<Question> model(Question team) {
		return new Model<Question>(team);
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}

	@Override
	public long size() {
		return getQuestionsCount();
	}

	private List<Question> getQuestions() {
		List<Question> questions = new ArrayList<Question>();
		if (competitionId == -1) {
			return questions;
		}
		try {
			Search search = new Search(Question.class);
			search.addFilterEqual("competitionId", competitionId);
			search.addSort(new Sort("id", false));
			questions = service.search(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return questions;
	}

	private int getQuestionsCount() {
		if (competitionId == -1) {
			return 0;
		}
		try {
			Search search = new Search(Question.class);
			search.addFilterEqual("competitionId", competitionId);
			return service.count(search);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
