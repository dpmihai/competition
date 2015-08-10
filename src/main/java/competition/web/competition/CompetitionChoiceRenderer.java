package competition.web.competition;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

import competition.domain.entity.Competition;

public class CompetitionChoiceRenderer  extends ChoiceRenderer<Competition> {

	private static final long serialVersionUID = 1L;

	@Override
	public Object getDisplayValue(Competition object) {
		if (object == null) {
			return super.getDisplayValue(object);
		}
		
		return object.getName();
	}

}