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

import competition.domain.entity.UserResponse;
import competition.service.GeneralService;

public class UserResponseDataProvider extends SortableDataProvider<UserResponse, String> {

	private static final long serialVersionUID = 1L;

	@SpringBean
	private GeneralService service;

	private int competitionId;
	private String username;

	public UserResponseDataProvider(int competitionId, String username) {
		Injector.get().inject(this);
		this.competitionId = competitionId;
		this.username = username;		
	}

	@Override
	public Iterator<? extends UserResponse> iterator(long first, long count) {
		return getResponses().iterator();
	}

	@Override
	public IModel<UserResponse> model(UserResponse team) {
		return new Model<UserResponse>(team);
	}

	public void setCompetitionId(int competitionId) {
		this.competitionId = competitionId;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public long size() {
		return getResponsesCount();
	}

	private List<UserResponse> getResponses() {
		List<UserResponse> responses = new ArrayList<UserResponse>();
		if (competitionId == -1) {
			return responses;
		}
						
		try {						
			Search search = new Search(UserResponse.class);
			search.addFilterEqual("competitionId", competitionId);
			search.addFilterEqual("username", username);
			search.addSort(new Sort("questionId", false));
			responses = service.search(search);						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return responses;
	}
	
	

	private int getResponsesCount() {
		if (competitionId == -1) {
			return 0;
		}						
		try {						
			Search search = new Search(UserResponse.class);
			search.addFilterEqual("competitionId", competitionId);
			search.addFilterEqual("username", username);
			List<UserResponse> responses = service.search(search);															
			return responses.size();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
