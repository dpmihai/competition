package competition.web.team;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import competition.domain.entity.Team;

public class TeamChoiceRenderer extends ChoiceRenderer<Team> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(Team object) {
		if (object == null) {
			return super.getDisplayValue(object);
		}
		
		return object.getName();
	}

}

